package com.androidide.compiler

import android.content.Context
import java.io.File

object ToolchainManager {

    fun init(context: Context) {
        val sdkDir = File(context.filesDir, "sdk")
        sdkDir.mkdirs()
        
        // Em um ambiente real, você extrairia aapt2 e android.jar dos assets aqui
        // se eles não existissem. Como o script de build anterior baixa, assumimos que existem.
        
        val aapt2 = File(sdkDir, "aapt2")
        if (aapt2.exists()) aapt2.setExecutable(true)
    }

    fun getAapt2(context: Context): File = File(context.filesDir, "sdk/aapt2")
    fun getAndroidJar(context: Context): File = File(context.filesDir, "sdk/android.jar")
    
    fun runCommand(command: List<String>, workingDir: File? = null): String {
        val process = ProcessBuilder(command)
            .directory(workingDir)
            .redirectErrorStream(true)
            .start()
            
        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()
        
        if (exitCode != 0) {
            throw RuntimeException("Command failed ($exitCode):\n${command.joinToString(" ")}\nOutput:\n$output")
        }
        return output
    }
}
