package com.androidide.compiler

import com.androidide.model.BuildResult
import com.androidide.model.BuildError
import java.io.File

class JavaCompiler {

    fun compile(
        sourceFiles: List<File>,
        outputDir: File,
        classpath: List<File>
    ): BuildResult {
        // No Android, não temos javax.tools.ToolProvider.
        // A alternativa real é executar o comando 'javac' se ele estiver disponível no sistema (termux/root)
        // ou usar o ECJ (Eclipse Compiler for Java) embarcado.
        
        try {
            val javacCommand = mutableListOf("javac")
            
            // Output dir
            javacCommand.add("-d")
            javacCommand.add(outputDir.absolutePath)
            
            // Classpath
            if (classpath.isNotEmpty()) {
                javacCommand.add("-cp")
                javacCommand.add(classpath.joinToString(File.pathSeparator) { it.absolutePath })
            }
            
            // Versão
            javacCommand.add("-source")
            javacCommand.add("1.8")
            javacCommand.add("-target")
            javacCommand.add("1.8")
            
            // Arquivos
            sourceFiles.forEach { javacCommand.add(it.absolutePath) }
            
            val process = ProcessBuilder(javacCommand)
                .redirectErrorStream(true)
                .start()
                
            val exitCode = process.waitFor()
            val output = process.inputStream.bufferedReader().readText()
            
            return if (exitCode == 0) {
                BuildResult(success = true)
            } else {
                BuildResult(
                    success = false,
                    errors = listOf(BuildError(message = "Javac failed: $output"))
                )
            }
            
        } catch (e: Exception) {
            return BuildResult(
                success = false,
                errors = listOf(BuildError(message = "Erro ao executar javac (não instalado no ambiente?): ${e.message}"))
            )
        }
    }
}
