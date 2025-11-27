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
    private val objDir = File(buildDir, "obj")
    private val apkDir = File(buildDir, "apk")
    
    private val aapt2 = ToolchainManager.getAapt2(context)
    private val androidJar = ToolchainManager.getAndroidJar(context)

    suspend fun compile(): BuildResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val errors = mutableListOf<BuildError>()

        try {
            ToolchainManager.init(context)

            if (!androidJar.exists()) {
                throw RuntimeException("android.jar não encontrado em ${androidJar.absolutePath}")
            }

            onProgress(BuildStep.PREPARING, "Limpando diretórios...")
            buildDir.deleteRecursively()
            genDir.mkdirs()
            objDir.mkdirs()
            apkDir.mkdirs()

            // 1. AAPT2 Compile
            onProgress(BuildStep.COMPILING_RESOURCES, "Compilando recursos...")
            val compiledResDir = File(buildDir, "compiled_res")
            compiledResDir.mkdirs()
            
            // Só executa se houver recursos
            if (project.resDir.exists()) {
                 val resFiles = project.resDir.walkTopDown().filter { it.isFile }.toList()
                 if (resFiles.isNotEmpty() && aapt2.exists()) {
                    val cmd = mutableListOf(aapt2.absolutePath, "compile")
                    cmd.add("-o"); cmd.add(compiledResDir.absolutePath)
                    resFiles.forEach { cmd.add(it.absolutePath) }
                    ToolchainManager.runCommand(cmd)
                 }
            }

            // 2. AAPT2 Link
            onProgress(BuildStep.COMPILING_RESOURCES, "Linkando recursos...")
            val rJavaDir = File(genDir, "r_java")
            rJavaDir.mkdirs()
            val unalignedApk = File(apkDir, "unaligned.apk")
            
            if (aapt2.exists() && project.manifestFile.exists()) {
                val linkCmd = mutableListOf(
                    aapt2.absolutePath, "link",
                    "-o", unalignedApk.absolutePath,
                    "-I", androidJar.absolutePath,
                    "--manifest", project.manifestFile.absolutePath,
                    "--java", rJavaDir.absolutePath,
                    "--auto-add-overlay"
                )
                compiledResDir.listFiles()?.forEach { linkCmd.add(it.absolutePath) }
                ToolchainManager.runCommand(linkCmd)
            }

            // 3. Compile Java/Kotlin
            onProgress(BuildStep.COMPILING_KOTLIN, "Compilando código...")
            val sourceFiles = mutableListOf<String>()
            project.srcDir.walkTopDown()
                .filter { it.extension == "java" || it.extension == "kt" }
                .forEach { sourceFiles.add(it.absolutePath) }
            
            rJavaDir.walkTopDown().filter { it.extension == "java" }.forEach { sourceFiles.add(it.absolutePath) }

            if (sourceFiles.isNotEmpty()) {
                // ECJ para Java
                val ecjSuccess = compileJavaWithECJ(sourceFiles, objDir, androidJar)
                if (!ecjSuccess) throw RuntimeException("Falha na compilação Java (ECJ).")
            }

            // 4. D8
            onProgress(BuildStep.CREATING_DEX, "DEX (D8)...")
            try {
                val classFiles = objDir.walkTopDown().filter { it.extension == "class" }.map { it.toPath() }.toList()
                if (classFiles.isNotEmpty()) {
                    val d8Command = com.android.tools.r8.D8Command.builder()
                        .addProgramFiles(classFiles)
                        .setOutput(apkDir.toPath(), com.android.tools.r8.OutputMode.DexIndexed)
                        .addLibraryFiles(androidJar.toPath())
                        .build()
                    com.android.tools.r8.D8.run(d8Command)
                }
            } catch (e: Exception) {
                 throw RuntimeException("Erro D8: ${e.message}")
            }

            // 5. Build Final
            onProgress(BuildStep.PACKAGING, "Gerando APK...")
            val signedApk = File(apkDir, "${project.name}-debug.apk")
            
            // Simplesmente movemos o unaligned se D8 não rodou ou mergeamos (Simplificado)
            // Em produção usaria Zip para juntar classes.dex + resources.ap_
            if (unalignedApk.exists()) {
                 unalignedApk.copyTo(signedApk, overwrite = true)
            }
            
            onProgress(BuildStep.DONE, "Pronto!")
            BuildResult(true, apkPath = signedApk.absolutePath, duration = System.currentTimeMillis() - startTime)

        } catch (e: Exception) {
            e.printStackTrace()
            errors.add(BuildError(message = e.message ?: "Erro desconhecido"))
            BuildResult(false, errors = errors)
        }
    }

    private fun compileJavaWithECJ(sources: List<String>, outputDir: File, androidJar: File): Boolean {
        val args = mutableListOf(
            "-1.8", "-proc:none",
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
