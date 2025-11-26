package com.androidide.compiler

import com.androidide.model.BuildResult
import com.androidide.model.BuildError
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import java.io.File

class KotlinCompiler {

    fun compile(
        sourceFiles: List<File>,
        outputDir: File,
        classpath: List<File>
    ): BuildResult {
        val errors = mutableListOf<BuildError>()
        val warnings = mutableListOf<String>()

        val messageCollector = object : MessageCollector {
            override fun clear() {}

            override fun hasErrors() = errors.isNotEmpty()

            override fun report(
                severity: CompilerMessageSeverity,
                message: String,
                location: CompilerMessageSourceLocation?
            ) {
                when (severity) {
                    CompilerMessageSeverity.ERROR,
                    CompilerMessageSeverity.EXCEPTION -> {
                        errors.add(BuildError(
                            file = location?.path ?: "",
                            line = location?.line ?: 0,
                            column = location?.column ?: 0,
                            message = message
                        ))
                    }
                    CompilerMessageSeverity.WARNING,
                    CompilerMessageSeverity.STRONG_WARNING -> {
                        warnings.add(message)
                    }
                    else -> {}
                }
            }
        }

        val args = K2JVMCompilerArguments().apply {
            freeArgs = sourceFiles.map { it.absolutePath }
            destination = outputDir.absolutePath
            this.classpath = classpath.joinToString(File.pathSeparator) { it.absolutePath }
            jvmTarget = "17"
            noStdlib = false
            noReflect = true
        }

        val compiler = K2JVMCompiler()
        val exitCode = compiler.exec(messageCollector, Services.EMPTY, args)

        return BuildResult(
            success = exitCode.code == 0 && errors.isEmpty(),
            errors = errors
        )
    }
}
