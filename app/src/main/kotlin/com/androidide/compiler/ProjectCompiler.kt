package com.androidide.compiler

import com.androidide.App
import com.androidide.model.BuildResult
import com.androidide.model.BuildError
import com.androidide.model.BuildStep
import com.androidide.project.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ProjectCompiler(
    private val project: Project,
    private val onProgress: (BuildStep, String) -> Unit
) {

    private val buildDir = project.buildDir
    private val classesDir = File(buildDir, "classes")
    private val dexDir = File(buildDir, "dex")
    private val apkDir = File(buildDir, "apk")

    suspend fun compile(): BuildResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val errors = mutableListOf<BuildError>()

        try {
            // Limpar build anterior
            onProgress(BuildStep.PREPARING, "Preparando build...")
            cleanBuildDir()
            createBuildDirs()

            // Compilar recursos (AAPT2)
            onProgress(BuildStep.COMPILING_RESOURCES, "Compilando recursos...")
            val resourceResult = compileResources()
            if (!resourceResult.success) {
                return@withContext resourceResult
            }

            // Compilar Kotlin/Java
            onProgress(BuildStep.COMPILING_KOTLIN, "Compilando código Kotlin...")
            val compileResult = compileSource()
            if (!compileResult.success) {
                return@withContext compileResult
            }

            // Criar DEX
            onProgress(BuildStep.CREATING_DEX, "Criando DEX...")
            val dexResult = createDex()
            if (!dexResult.success) {
                return@withContext dexResult
            }

            // Empacotar APK
            onProgress(BuildStep.PACKAGING, "Empacotando APK...")
            val apkResult = packageApk()
            if (!apkResult.success) {
                return@withContext apkResult
            }

            // Assinar APK
            onProgress(BuildStep.SIGNING, "Assinando APK...")
            val signResult = signApk()
            if (!signResult.success) {
                return@withContext signResult
            }

            onProgress(BuildStep.DONE, "Build concluído!")

            val duration = System.currentTimeMillis() - startTime
            BuildResult(
                success = true,
                apkPath = File(apkDir, "${project.name}-debug.apk").absolutePath,
                duration = duration
            )

        } catch (e: Exception) {
            errors.add(BuildError(
                file = "",
                line = 0,
                column = 0,
                message = "Erro de build: ${e.message}"
            ))

            BuildResult(
                success = false,
                errors = errors,
                duration = System.currentTimeMillis() - startTime
            )
        }
    }

    private fun cleanBuildDir() {
        buildDir.deleteRecursively()
    }

    private fun createBuildDirs() {
        classesDir.mkdirs()
        dexDir.mkdirs()
        apkDir.mkdirs()
    }

    private suspend fun compileResources(): BuildResult = withContext(Dispatchers.IO) {
        try {
            val resourceCompiler = ResourceCompiler(project)
            resourceCompiler.compile()
        } catch (e: Exception) {
            BuildResult(
                success = false,
                errors = listOf(BuildError("", 0, 0, "Erro ao compilar recursos: ${e.message}"))
            )
        }
    }

    private suspend fun compileSource(): BuildResult = withContext(Dispatchers.IO) {
        try {
            // Coletar arquivos Kotlin
            val kotlinFiles = mutableListOf<File>()
            project.kotlinDir.walkTopDown().forEach { file ->
                if (file.extension == "kt") {
                    kotlinFiles.add(file)
                }
            }

            // Coletar arquivos Java
            val javaFiles = mutableListOf<File>()
            project.javaDir.walkTopDown().forEach { file ->
                if (file.extension == "java") {
                    javaFiles.add(file)
                }
            }

            // Compilar Kotlin
            if (kotlinFiles.isNotEmpty()) {
                val kotlinCompiler = KotlinCompiler()
                val result = kotlinCompiler.compile(
                    sourceFiles = kotlinFiles,
                    outputDir = classesDir,
                    classpath = getClasspath()
                )
                if (!result.success) return@withContext result
            }

            // Compilar Java
            if (javaFiles.isNotEmpty()) {
                val javaCompiler = JavaCompiler()
                val result = javaCompiler.compile(
                    sourceFiles = javaFiles,
                    outputDir = classesDir,
                    classpath = getClasspath() + listOf(classesDir)
                )
                if (!result.success) return@withContext result
            }

            // Adicionar R.java compilado
            val rFile = File(buildDir, "gen/${project.packagePath}/R.java")
            if (rFile.exists()) {
                val javaCompiler = JavaCompiler()
                javaCompiler.compile(
                    sourceFiles = listOf(rFile),
                    outputDir = classesDir,
                    classpath = getClasspath()
                )
            }

            BuildResult(success = true)
        } catch (e: Exception) {
            BuildResult(
                success = false,
                errors = listOf(BuildError("", 0, 0, "Erro de compilação: ${e.message}"))
            )
        }
    }

    private suspend fun createDex(): BuildResult = withContext(Dispatchers.IO) {
        try {
            val dexCompiler = DexCompiler()
            dexCompiler.compile(
                classesDir = classesDir,
                outputDir = dexDir
            )
        } catch (e: Exception) {
            BuildResult(
                success = false,
                errors = listOf(BuildError("", 0, 0, "Erro ao criar DEX: ${e.message}"))
            )
        }
    }

    private suspend fun packageApk(): BuildResult = withContext(Dispatchers.IO) {
        try {
            val apkBuilder = ApkBuilder(project)
            apkBuilder.build(
                dexDir = dexDir,
                resourcesApk = File(buildDir, "res/resources.ap_"),
                outputDir = apkDir
            )
        } catch (e: Exception) {
            BuildResult(
                success = false,
                errors = listOf(BuildError("", 0, 0, "Erro ao empacotar APK: ${e.message}"))
            )
        }
    }

    private suspend fun signApk(): BuildResult = withContext(Dispatchers.IO) {
        try {
            val apkSigner = ApkSigner()
            val unsignedApk = File(apkDir, "${project.name}-unsigned.apk")
            val signedApk = File(apkDir, "${project.name}-debug.apk")
            
            apkSigner.sign(unsignedApk, signedApk)
            
            // Remover APK não assinado
            unsignedApk.delete()
            
            BuildResult(success = true, apkPath = signedApk.absolutePath)
        } catch (e: Exception) {
            BuildResult(
                success = false,
                errors = listOf(BuildError("", 0, 0, "Erro ao assinar APK: ${e.message}"))
            )
        }
    }

    private fun getClasspath(): List<File> {
        return listOf(
            File(App.sdkDir, "android.jar"),
            File(App.sdkDir, "kotlin-stdlib.jar")
        )
    }
}
