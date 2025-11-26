package com.androidide.compiler

import com.androidide.model.BuildResult
import com.androidide.model.BuildError
import java.io.File
import java.nio.file.Path

class DexCompiler {

    fun compile(classesDir: File, outputDir: File): BuildResult {
        return try {
            val classFiles = mutableListOf<Path>()
            classesDir.walkTopDown().forEach { file ->
                if (file.extension == "class") {
                    classFiles.add(file.toPath())
                }
            }

            if (classFiles.isEmpty()) {
                return BuildResult(
                    success = false,
                    errors = listOf(BuildError(message = "Nenhum arquivo .class encontrado"))
                )
            }

            // Usa D8 para converter .class em .dex
            // Nota: Requer dependência 'com.android.tools:r8' no build.gradle
            val command = com.android.tools.r8.D8Command.builder()
                .addProgramFiles(classFiles)
                .setOutput(outputDir.toPath(), com.android.tools.r8.OutputMode.DexIndexed)
                .setMinApiLevel(26)
                .build()

            com.android.tools.r8.D8.run(command)

            BuildResult(success = true)
        } catch (e: Exception) {
            e.printStackTrace()
            BuildResult(
                success = false,
                errors = listOf(BuildError(message = "Erro D8: ${e.message}"))
            )
        }
    }
}
