package com.androidide.compiler

import android.content.Context
import java.io.File
import java.io.FileOutputStream

/**
 * Gerencia as ferramentas de compilação nativas (aapt2, d8, android.jar)
 * Nota: Os binários devem ser baixados ou extraídos dos assets na primeira execução.
 */
object ToolchainManager {

    fun init(context: Context) {
        val sdkDir = File(context.filesDir, "sdk")
        sdkDir.mkdirs()
        
        // Em um app real, você baixaria esses binários da internet para a arquitetura aarch64
        // ou extrairia de assets se você os incluir no APK.
        // Aqui vamos verificar se existem e definir permissão de execução.
        
        val aapt2 = File(sdkDir, "aapt2")
        val d8 = File(sdkDir, "d8") // Normalmente é um .jar wrapper ou script bash
        
        if (aapt2.exists()) aapt2.setExecutable(true)
        if (d8.exists()) d8.setExecutable(true)
    }

    fun getAapt2(context: Context): File = File(context.filesDir, "sdk/aapt2")
    fun getAndroidJar(context: Context): File = File(context.filesDir, "sdk/android.jar")
    
    // Função auxiliar para executar comandos
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
