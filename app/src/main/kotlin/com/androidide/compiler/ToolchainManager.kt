package com.androidide.compiler

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object ToolchainManager {

    private const val TAG = "ToolchainManager"

    fun init(context: Context) {
        val sdkDir = File(context.filesDir, "sdk")
        if (!sdkDir.exists()) sdkDir.mkdirs()

        // 1. Copia JARs
        copyAsset(context, "sdk/android.jar", File(sdkDir, "android.jar"))
        copyAsset(context, "sdk/kotlin-stdlib.jar", File(sdkDir, "kotlin-stdlib.jar"))
        
        // 2. Copia AAPT2
        val aapt2 = File(sdkDir, "aapt2")
        // Sempre tenta copiar novamente para garantir integridade, ou verifique existência
        if (!aapt2.exists()) {
            copyAsset(context, "sdk/aapt2", aapt2)
        }
        
        // 3. FORÇA PERMISSÃO DE EXECUÇÃO (A parte crítica)
        // O Android não permite executar se não tiver bit +x. 
        // File.setExecutable() as vezes falha em alguns dispositivos, então usamos o shell.
        if (aapt2.exists()) {
            try {
                // chmod 777 garante leitura, escrita e execução para todos
                val process = Runtime.getRuntime().exec("chmod 777 ${aapt2.absolutePath}")
                process.waitFor()
            } catch (e: Exception) {
                Log.e(TAG, "Falha ao rodar chmod no aapt2", e)
            }
        }
    }

    private fun copyAsset(context: Context, assetPath: String, destFile: File) {
        try {
            context.assets.open(assetPath).use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao copiar asset: $assetPath", e)
        }
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
            // Log mais detalhado para debug
            throw RuntimeException("Command failed ($exitCode):\nCmd: ${command.joinToString(" ")}\nOutput:\n$output")
        }
        return output
    }
}
