package com.androidide.compiler

import com.androidide.model.BuildResult
import com.androidide.model.BuildError
import java.io.File
import javax.tools.ToolProvider

class JavaCompiler {

    fun compile(
        sourceFiles: List<File>, 
        outputDir: File, 
        classpath: List<File>
    ): BuildResult {
        val compiler = ToolProvider.getSystemJavaCompiler()
        
        if (compiler == null) {
            return BuildResult(
                success = false,
                errors = listOf(BuildError("", 0, 0, "Compilador Java não encontrado (ToolProvider retornou null)."))
            )
        }

        val errors = mutableListOf<BuildError>()
        
        // Preparar argumentos
        val classpathStr = classpath.joinToString(File.pathSeparator) { it.absolutePath }
        val args = mutableListOf<String>()
        args.add("-d")
        args.add(outputDir.absolutePath)
        args.add("-cp")
        args.add(classpathStr)
        args.add("-source")
        args.add("1.8")
        args.add("-target")
        args.add("1.8")
        
        sourceFiles.forEach { args.add(it.absolutePath) }

        // Executar
        val exitCode = compiler.run(null, null, null, *args.toTypedArray())

        return BuildResult(
            success = exitCode == 0,
            errors = errors // Captura de erros de stream seria necessária para popular isso detalhadamente
        )
    }
}
