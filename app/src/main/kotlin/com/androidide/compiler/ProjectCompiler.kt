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
    // Usando construtor explícito para evitar ambiguidade
    private val classesDir = File(buildDir.absolutePath, "classes")
    private val dexDir = File(buildDir.absolutePath, "dex")
    private val apkDir = File(buildDir.absolutePath, "apk")

    suspend fun compile(): BuildResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val errors = mutableListOf<BuildError>()

        try {
            onProgress(BuildStep.PREPARING, "Preparando build...")
            cleanBuildDir()
            createBuildDirs()

            onProgress(BuildStep.COMPILING_RESOURCES, "Compilando recursos...")
            val resourceResult = compileResources()
            if (!resourceResult.success) return@withContext resourceResult

            onProgress(BuildStep.COMPILING_KOTLIN, "Compilando código...")
            val compileResult = compileSource()
            if (!compileResult.success) return@withContext compileResult

            onProgress(BuildStep.CREATING_DEX, "Criando DEX...")
            val dexResult = createDex()
            if (!dexResult.success) return@withContext dexResult

            onProgress(BuildStep.PACKAGING, "Empacotando APK...")
            val apkResult = packageApk()
            if (!apkResult.success) return@withContext apkResult

            onProgress(BuildStep.SIGNING, "Assinando APK...")
            val signResult = signApk()
            if (!signResult.success) return@withContext signResult

            onProgress(BuildStep.DONE, "Build concluído!")

            val duration = System.currentTimeMillis() - startTime
            BuildResult(
                success = true,
                apkPath = File(apkDir, "${project.name}-debug.apk").absolutePath,
                duration = duration
            )

        } catch (e: Exception) {
            errors.add(BuildError(message = "Erro fatal: ${e.message}"))
            BuildResult(success = false, errors = errors)
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
        ResourceCompiler(project).compile()
    }

    private suspend fun compileSource(): BuildResult = withContext(Dispatchers.IO) {
        val kotlinFiles = mutableListOf<File>()
        val javaFiles = mutableListOf<File>()

        project.srcDir.walkTopDown().forEach { file ->
            if (file.extension == "kt") kotlinFiles.add(file)
            if (file.extension == "java") javaFiles.add(file)
        }

        val classpath = getClasspath()

        // Compila Kotlin
        if (kotlinFiles.isNotEmpty()) {
            val kResult = KotlinCompiler().compile(kotlinFiles, classesDir, classpath)
            if (!kResult.success) return@withContext kResult
        }

        // Compila Java
        if (javaFiles.isNotEmpty()) {
            // Adiciona classesDir ao classpath para que Java veja o Kotlin compilado
            val javaClasspath = classpath + classesDir
            val jResult = JavaCompiler().compile(javaFiles, classesDir, javaClasspath)
            if (!jResult.success) return@withContext jResult
        }

        BuildResult(success = true)
    }

    private suspend fun createDex(): BuildResult = withContext(Dispatchers.IO) {
        DexCompiler().compile(classesDir, dexDir)
    }

    private suspend fun packageApk(): BuildResult = withContext(Dispatchers.IO) {
        ApkBuilder(project).build(
            dexDir = dexDir,
            resourcesApk = File(project.buildDir, "res/resources.ap_"),
            outputDir = apkDir
        )
    }

    private suspend fun signApk(): BuildResult = withContext(Dispatchers.IO) {
        val unsigned = File(apkDir, "${project.name}-unsigned.apk")
        val signed = File(apkDir, "${project.name}-debug.apk")
        
        try {
            ApkSigner().sign(unsigned, signed)
            unsigned.delete()
            BuildResult(success = true, apkPath = signed.absolutePath)
        } catch (e: Exception) {
            BuildResult(success = false, errors = listOf(BuildError(message = e.message ?: "Erro ao assinar")))
        }
    }

    private fun getClasspath(): List<File> {
        val cp = mutableListOf<File>()
        // Adiciona android.jar e kotlin-stdlib
        File(App.sdkDir, "android.jar").let { if(it.exists()) cp.add(it) }
        File(App.sdkDir, "kotlin-stdlib.jar").let { if(it.exists()) cp.add(it) }
        return cp
    }
}
