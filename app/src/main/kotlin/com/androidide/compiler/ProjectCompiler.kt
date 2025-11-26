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

    private val buildDirPath: String = project.buildDir.absolutePath
    private val classesDir = File(buildDirPath, "classes")
    private val dexDir = File(buildDirPath, "dex")
    private val apkDir = File(buildDirPath, "apk")

    suspend fun compile(): BuildResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val errors = mutableListOf<BuildError>()

        try {
            onProgress(BuildStep.PREPARING, "Preparando build...")
            cleanBuildDir()
            createBuildDirs()

            onProgress(BuildStep.COMPILING_RESOURCES, "Compilando recursos...")
            val resourceResult = ResourceCompiler(project).compile()
            if (!resourceResult.success) return@withContext resourceResult

            onProgress(BuildStep.COMPILING_KOTLIN, "Compilando código...")
            val compileResult = compileSource()
            if (!compileResult.success) return@withContext compileResult

            onProgress(BuildStep.CREATING_DEX, "Criando DEX...")
            val dexResult = DexCompiler().compile(classesDir, dexDir)
            if (!dexResult.success) return@withContext dexResult

            onProgress(BuildStep.PACKAGING, "Empacotando APK...")
            val apkResult = ApkBuilder(project).build(
                dexDir = dexDir,
                resourcesApk = File(project.buildDir, "res/resources.ap_"),
                outputDir = apkDir
            )
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
            e.printStackTrace()
            errors.add(BuildError(message = "Erro fatal: ${e.message}"))
            BuildResult(success = false, errors = errors)
        }
    }

    private fun cleanBuildDir() {
        project.buildDir.deleteRecursively()
    }

    private fun createBuildDirs() {
        classesDir.mkdirs()
        dexDir.mkdirs()
        apkDir.mkdirs()
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
            // ✅ CORRIGIDO: return@withContext
            if (!kResult.success) return@withContext kResult
        }

        // Compila Java
        if (javaFiles.isNotEmpty()) {
            val javaClasspath = classpath + classesDir
            val jResult = JavaCompiler().compile(javaFiles, classesDir, javaClasspath)
            // ✅ CORRIGIDO: return@withContext
            if (!jResult.success) return@withContext jResult
        }

        BuildResult(success = true)
    }

    private suspend fun signApk(): BuildResult = withContext(Dispatchers.IO) {
        val unsigned = File(apkDir, "${project.name}-unsigned.apk")
        val signed = File(apkDir, "${project.name}-debug.apk")
        
        try {
            ApkSigner().sign(unsigned, signed)
            unsigned.delete()
            BuildResult(success = true, apkPath = signed.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            BuildResult(success = false, errors = listOf(BuildError(message = e.message ?: "Erro ao assinar")))
        }
    }

    private fun getClasspath(): List<File> {
        val cp = mutableListOf<File>()
        val sdkDir = App.sdkDir
        File(sdkDir, "android.jar").let { if(it.exists()) cp.add(it) }
        File(sdkDir, "kotlin-stdlib.jar").let { if(it.exists()) cp.add(it) }
        return cp
    }
}
