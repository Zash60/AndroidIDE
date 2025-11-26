package com.androidide.compiler

import com.androidide.model.BuildResult
import com.androidide.model.BuildError
import java.io.File

class DexCompiler {

    fun compile(classesDir: File, outputDir: File): BuildResult {
        return try {
            // Usar D8 para criar DEX
            val classFiles = mutableListOf<File>()
            classesDir.walkTopDown().forEach { file ->
                if (file.extension == "class") {
                    classFiles.add(file)
                }
            }

            if (classFiles.isEmpty()) {
                return BuildResult(
                    success = false,
                    errors = listOf(BuildError("", 0, 0, "Nenhum arquivo .class encontrado"))
                )
            }

            // Usar D8 via linha de comando ou API
            val dexFile = File(outputDir, "classes.dex")
            
            // Implementação simplificada - usar D8 API
            com.android.tools.r8.D8.run(
                com.android.tools.r8.D8Command.builder()
                    .addProgramFiles(classFiles.map { it.toPath() })
                    .setOutput(outputDir.toPath(), com.android.tools.r8.OutputMode.DexIndexed)
                    .setMinApiLevel(24)
                    .build()
            )

            BuildResult(success = dexFile.exists())
        } catch (e: Exception) {
            BuildResult(
                success = false,
                errors = listOf(BuildError("", 0, 0, "Erro D8: ${e.message}"))
            )
        }
    }
}
