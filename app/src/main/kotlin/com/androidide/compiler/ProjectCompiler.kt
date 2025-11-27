package com.androidide.compiler

import android.content.Context
import com.androidide.App
import com.androidide.model.BuildResult
import com.androidide.model.BuildError
import com.androidide.model.BuildStep
import com.androidide.project.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jdt.core.compiler.batch.BatchCompiler
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class ProjectCompiler(
    private val project: Project,
    private val onProgress: (BuildStep, String) -> Unit
) {

    private val context: Context = App.instance
    private val buildDir = project.buildDir
    private val genDir = File(buildDir, "gen")
    private val objDir = File(buildDir, "obj") // Para .class
    private val apkDir = File(buildDir, "apk")
    
    // Caminhos das ferramentas
    private val aapt2 = ToolchainManager.getAapt2(context)
    private val androidJar = ToolchainManager.getAndroidJar(context)

    suspend fun compile(): BuildResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val errors = mutableListOf<BuildError>()

        try {
            // Verificar ferramentas
            if (!aapt2.exists() || !androidJar.exists()) {
                throw RuntimeException("Ferramentas SDK (aapt2/android.jar) não encontradas. Configure o SDK.")
            }

            onProgress(BuildStep.PREPARING, "Limpando diretórios...")
            buildDir.deleteRecursively()
            genDir.mkdirs()
            objDir.mkdirs()
            apkDir.mkdirs()

            // 1. AAPT2 Compile
            onProgress(BuildStep.COMPILING_RESOURCES, "Compilando recursos (AAPT2)...")
            val compiledResDir = File(buildDir, "compiled_res")
            compiledResDir.mkdirs()
            
            val resFiles = project.resDir.walkTopDown().filter { it.isFile }.toList()
            if (resFiles.isNotEmpty()) {
                val cmd = mutableListOf(aapt2.absolutePath, "compile")
                cmd.add("-o"); cmd.add(compiledResDir.absolutePath)
                resFiles.forEach { cmd.add(it.absolutePath) }
                
                ToolchainManager.runCommand(cmd)
            }

            // 2. AAPT2 Link (Gera R.java e resources.apk)
            onProgress(BuildStep.COMPILING_RESOURCES, "Linkando recursos...")
            val rJavaDir = File(genDir, "r_java")
            rJavaDir.mkdirs()
            val unalignedApk = File(apkDir, "unaligned.apk")
            
            val linkCmd = mutableListOf(
                aapt2.absolutePath, "link",
                "-o", unalignedApk.absolutePath,
                "-I", androidJar.absolutePath,
                "--manifest", project.manifestFile.absolutePath,
                "--java", rJavaDir.absolutePath,
                "--auto-add-overlay"
            )
            // Adiciona todos os .flat gerados
            compiledResDir.listFiles()?.forEach { linkCmd.add(it.absolutePath) }
            
            ToolchainManager.runCommand(linkCmd)

            // 3. Compilar Java/Kotlin
            onProgress(BuildStep.COMPILING_KOTLIN, "Compilando código Java/Kotlin...")
            val sourceFiles = mutableListOf<String>()
            project.srcDir.walkTopDown()
                .filter { it.extension == "java" || it.extension == "kt" }
                .forEach { sourceFiles.add(it.absolutePath) }
            
            // Adiciona R.java gerado
            rJavaDir.walkTopDown().filter { it.extension == "java" }.forEach { sourceFiles.add(it.absolutePath) }

            if (sourceFiles.isNotEmpty()) {
                // Usando ECJ para Java (Simplificado - em produção usaria Kotlin Compiler para mistos)
                val ecjSuccess = compileJavaWithECJ(sourceFiles, objDir, androidJar)
                if (!ecjSuccess) throw RuntimeException("Falha na compilação Java.")
            }

            // 4. D8 (Class -> Dex)
            onProgress(BuildStep.CREATING_DEX, "Convertendo para DEX (D8)...")
            // Como D8 é complexo de invocar via shell sem o binário, usamos a API do D8 se disponível nas dependências
            // ou invocamos o d8.jar via processo java
            try {
                val d8Command = com.android.tools.r8.D8Command.builder()
                    .addProgramFiles(objDir.walkTopDown().filter { it.extension == "class" }.map { it.toPath() }.toList())
                    .setOutput(apkDir.toPath(), com.android.tools.r8.OutputMode.DexIndexed)
                    .addLibraryFiles(androidJar.toPath())
                    .build()
                com.android.tools.r8.D8.run(d8Command)
            } catch (e: Exception) {
                 throw RuntimeException("Erro D8: ${e.message}")
            }

            // 5. Empacotamento Final (Adicionar classes.dex ao APK de recursos)
            onProgress(BuildStep.PACKAGING, "Finalizando APK...")
            val finalApk = File(apkDir, "${project.name}.apk")
            
            // Usamos ApkBuilder existente ou zip simples para juntar resources.apk + classes.dex
            ApkBuilder(project).build(apkDir, unalignedApk, apkDir) // (Reutilizando sua classe ApkBuilder)
            
            // 6. Assinatura
            onProgress(BuildStep.SIGNING, "Assinando...")
            val signedApk = File(apkDir, "${project.name}-signed.apk")
            ApkSigner().sign(File(apkDir, "${project.name}-unsigned.apk"), signedApk)

            onProgress(BuildStep.DONE, "Sucesso!")
            BuildResult(true, apkPath = signedApk.absolutePath, duration = System.currentTimeMillis() - startTime)

        } catch (e: Exception) {
            e.printStackTrace()
            errors.add(BuildError(message = e.message ?: "Erro desconhecido"))
            BuildResult(false, errors = errors)
        }
    }

    private fun compileJavaWithECJ(sources: List<String>, outputDir: File, androidJar: File): Boolean {
        val args = mutableListOf(
            "-1.8",
            "-proc:none",
            "-d", outputDir.absolutePath,
            "-cp", androidJar.absolutePath
        )
        args.addAll(sources)

        val outWriter = StringWriter()
        val errWriter = StringWriter()
        
        val success = BatchCompiler.compile(
            args.toTypedArray(),
            PrintWriter(outWriter),
            PrintWriter(errWriter),
            null
        )
        
        if (!success) {
            println("ECJ Error: $errWriter")
        }
        return success
    }
}
