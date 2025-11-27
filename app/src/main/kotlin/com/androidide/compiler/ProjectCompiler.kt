package com.androidide.compiler

import android.content.Context
import com.androidide.App
import com.androidide.model.BuildError
import com.androidide.model.BuildResult
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
    
    // Diretórios de Build
    private val buildDir = project.buildDir
    private val genDir = File(buildDir, "gen")
    private val objDir = File(buildDir, "obj")
    private val apkDir = File(buildDir, "apk")
    private val rJavaDir = File(genDir, "src/main/java") // Caminho padrão para R.java

    // Ferramentas
    private val aapt2 = ToolchainManager.getAapt2(context)
    private val androidJar = ToolchainManager.getAndroidJar(context)

    suspend fun compile(): BuildResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val errors = mutableListOf<BuildError>()

        try {
            // 0. Inicialização e Verificações
            ToolchainManager.init(context)

            if (!androidJar.exists()) {
                throw RuntimeException("android.jar não encontrado em ${androidJar.absolutePath}. Verifique se o download foi concluído.")
            }
            if (!aapt2.exists()) {
                throw RuntimeException("aapt2 não encontrado em ${aapt2.absolutePath}.")
            }

            onProgress(BuildStep.PREPARING, "Limpando diretórios de build...")
            buildDir.deleteRecursively()
            
            // Recria estrutura de pastas
            genDir.mkdirs()
            objDir.mkdirs()
            apkDir.mkdirs()
            rJavaDir.mkdirs()

            // =========================================================================
            // 1. AAPT2 Compile (Recursos -> .flat)
            // =========================================================================
            onProgress(BuildStep.COMPILING_RESOURCES, "Compilando recursos...")
            val compiledResDir = File(buildDir, "compiled_res")
            compiledResDir.mkdirs()
            
            val resFiles = if (project.resDir.exists()) {
                project.resDir.walkTopDown()
                    .filter { it.isFile && !it.name.startsWith(".") }
                    .toList()
            } else {
                emptyList()
            }

            if (resFiles.isNotEmpty()) {
                val compileCmd = mutableListOf(
                    aapt2.absolutePath, "compile",
                    "-o", compiledResDir.absolutePath
                )
                resFiles.forEach { compileCmd.add(it.absolutePath) }
                
                try {
                    ToolchainManager.runCommand(compileCmd)
                } catch (e: Exception) {
                    throw RuntimeException("Erro ao compilar recursos (AAPT2 Compile): ${e.message}")
                }
            }

            // =========================================================================
            // 2. AAPT2 Link (Gera R.java e resources.ap_)
            // =========================================================================
            onProgress(BuildStep.COMPILING_RESOURCES, "Linkando recursos...")
            
            // O arquivo intermediário de recursos (recursos compilados + manifesto)
            val resourcesApk = File(apkDir, "resources.ap_")
            
            if (project.manifestFile.exists()) {
                val linkCmd = mutableListOf(
                    aapt2.absolutePath, "link",
                    "-o", resourcesApk.absolutePath,
                    "-I", androidJar.absolutePath,
                    "--manifest", project.manifestFile.absolutePath,
                    "--java", rJavaDir.absolutePath,
                    "--auto-add-overlay",
                    "--min-sdk-version", project.minSdk.toString(),
                    "--target-sdk-version", project.targetSdk.toString()
                )
                
                // Adiciona todos os arquivos .flat gerados
                compiledResDir.listFiles()?.forEach { 
                    if (it.name.endsWith(".flat")) {
                        linkCmd.add(it.absolutePath)
                    }
                }
                
                try {
                    ToolchainManager.runCommand(linkCmd)
                } catch (e: Exception) {
                     throw RuntimeException("Erro ao linkar recursos (AAPT2 Link): ${e.message}")
                }
            } else {
                throw RuntimeException("AndroidManifest.xml não encontrado em ${project.manifestFile.absolutePath}")
            }

            // =========================================================================
            // 3. Compilação de Código (Kotlin & Java)
            // =========================================================================
            onProgress(BuildStep.COMPILING_KOTLIN, "Compilando fontes...")
            
            // Coletar arquivos fonte (.kt e .java)
            val sourceFiles = mutableListOf<File>()
            
            // Fontes do projeto
            if (project.srcDir.exists()) {
                project.srcDir.walkTopDown()
                    .filter { it.extension == "java" || it.extension == "kt" }
                    .forEach { sourceFiles.add(it) }
            }
            
            // R.java gerado
            rJavaDir.walkTopDown()
                .filter { it.extension == "java" }
                .forEach { sourceFiles.add(it) }

            if (sourceFiles.isEmpty()) {
                throw RuntimeException("Nenhum arquivo de código fonte encontrado.")
            }

            // Classpath básico
            val classpath = listOf(androidJar)

            // A. Compilador Kotlin
            val kotlinFiles = sourceFiles.filter { it.extension == "kt" }
            if (kotlinFiles.isNotEmpty()) {
                onProgress(BuildStep.COMPILING_KOTLIN, "Compilando Kotlin...")
                val kotlinResult = KotlinCompiler().compile(
                    sourceFiles = kotlinFiles,
                    outputDir = objDir,
                    classpath = classpath
                )
                
                if (!kotlinResult.success) {
                    val msg = kotlinResult.errors.firstOrNull()?.message ?: "Erro desconhecido no Kotlin"
                    throw RuntimeException("Erro Kotlin: $msg")
                }
            }

            // B. Compilador Java (ECJ)
            // Compila arquivos .java e também precisa ver as classes Kotlin já compiladas (se houver) no classpath
            val javaFiles = sourceFiles.filter { it.extension == "java" }
            if (javaFiles.isNotEmpty()) {
                onProgress(BuildStep.COMPILING_JAVA, "Compilando Java...")
                
                // Adiciona objDir ao classpath para que o Java veja as classes Kotlin compiladas
                val javaClasspath = classpath + objDir 
                
                val ecjSuccess = compileJavaWithECJ(
                    sources = javaFiles,
                    outputDir = objDir,
                    classpath = javaClasspath
                )
                
                if (!ecjSuccess) {
                    throw RuntimeException("Erro na compilação Java (ECJ). Verifique logs do console.")
                }
            }

            // =========================================================================
            // 4. D8 (Class -> Dex)
            // =========================================================================
            onProgress(BuildStep.CREATING_DEX, "Convertendo para DEX (D8)...")
            
            val classFiles = objDir.walkTopDown()
                .filter { it.extension == "class" }
                .map { it.toPath() }
                .toList()

            if (classFiles.isEmpty()) {
                 throw RuntimeException("Nenhum arquivo .class gerado. A compilação falhou silenciosamente?")
            }

            try {
                val d8Command = com.android.tools.r8.D8Command.builder()
                    .addProgramFiles(classFiles)
                    .setOutput(apkDir.toPath(), com.android.tools.r8.OutputMode.DexIndexed)
                    .addLibraryFiles(androidJar.toPath())
                    .setMinApiLevel(project.minSdk)
                    .build()
                    
                com.android.tools.r8.D8.run(d8Command)
            } catch (e: Exception) {
                throw RuntimeException("Erro D8 (Dexing): ${e.message}")
            }

            // =========================================================================
            // 5. Empacotamento (ApkBuilder)
            // =========================================================================
            onProgress(BuildStep.PACKAGING, "Empacotando APK...")
            
            // O ApkBuilder junta o resources.ap_ com o classes.dex
            val unsignedApk = File(apkDir, "${project.name}-unsigned.apk")
            val builder = ApkBuilder(project)
            val buildResult = builder.build(
                dexDir = apkDir, // Onde está o classes.dex
                resourcesApk = resourcesApk,
                outputDir = apkDir
            )
            
            if (!buildResult.success) {
                throw RuntimeException("Erro ao empacotar APK: ${buildResult.errors.firstOrNull()?.message}")
            }

            // =========================================================================
            // 6. Assinatura (ApkSigner)
            // =========================================================================
            onProgress(BuildStep.SIGNING, "Assinando APK...")
            
            val finalApk = File(project.outputDir, "${project.name}-debug.apk")
            // Garante diretório de saída final
            if (!finalApk.parentFile.exists()) finalApk.parentFile.mkdirs()

            try {
                val signer = ApkSigner()
                // Assumindo que o builder gerou o nome padrão "-unsigned.apk"
                signer.sign(unsignedApk, finalApk)
            } catch (e: Exception) {
                // Se falhar assinatura (ex: falta keystore), copia o não assinado como fallback
                unsignedApk.copyTo(finalApk, overwrite = true)
                // Não lançamos erro fatal aqui para permitir teste em emuladores permissivos,
                // mas em devices reais a instalação falhará.
            }

            onProgress(BuildStep.DONE, "Build Concluído!")
            
            return BuildResult(
                success = true,
                apkPath = finalApk.absolutePath,
                duration = System.currentTimeMillis() - startTime
            )

        } catch (e: Exception) {
            e.printStackTrace()
            errors.add(BuildError(message = e.message ?: "Erro desconhecido durante o build"))
            return BuildResult(success = false, errors = errors)
        }
    }

    /**
     * Compila código Java usando o compilador Eclipse (ECJ) embutido.
     * Necessário porque o Android não possui o binário 'javac' do sistema.
     */
    private fun compileJavaWithECJ(
        sources: List<File>,
        outputDir: File,
        classpath: List<File>
    ): Boolean {
        // Monta os argumentos para o compilador batch do ECJ
        val args = mutableListOf<String>()
        
        args.add("-1.8") // Compatibilidade Java 8
        args.add("-proc:none") // Desativa processamento de anotações complexas para velocidade
        args.add("-d"); args.add(outputDir.absolutePath) // Diretório de saída
        
        // Classpath
        if (classpath.isNotEmpty()) {
            args.add("-cp")
            args.add(classpath.joinToString(File.pathSeparator) { it.absolutePath })
        }
        
        // Arquivos Fonte
        sources.forEach { args.add(it.absolutePath) }

        // Captura de logs
        val outWriter = StringWriter()
        val errWriter = StringWriter()
        val outPrint = PrintWriter(outWriter)
        val errPrint = PrintWriter(errWriter)

        // Executa
        val success = BatchCompiler.compile(
            args.toTypedArray(),
            outPrint,
            errPrint,
            null
        )

        if (!success) {
            println("ECJ Erro:\n$errWriter")
            println("ECJ Log:\n$outWriter")
            // Poderia parsear os erros aqui para o objeto BuildError
        }

        return success
    }
}
