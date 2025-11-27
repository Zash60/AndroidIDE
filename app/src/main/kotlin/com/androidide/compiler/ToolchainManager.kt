package com.androidide.compiler

import android.content.Context
import java.io.File
import java.io.FileOutputStream

object ToolchainManager {

    fun init(context: Context) {
        val sdkDir = File(context.filesDir, "sdk")
        if (!sdkDir.exists()) sdkDir.mkdirs()

        // Copia android.jar se não existir
        copyAsset(context, "sdk/android.jar", File(sdkDir, "android.jar"))
        
        // Copia kotlin-stdlib.jar se não existir
        copyAsset(context, "sdk/kotlin-stdlib.jar", File(sdkDir, "kotlin-stdlib.jar"))

        // Se você tiver o binário aapt2 nos assets (compilado para ARM/Android), copie também:
        // copyAsset(context, "sdk/aapt2", File(sdkDir, "aapt2"))
        
        val aapt2 = File(sdkDir, "aapt2")
        if (aapt2.exists()) aapt2.setExecutable(true)
    }

    private fun copyAsset(context: Context, assetPath: String, destFile: File) {
        if (destFile.exists()) return // Já copiado

        try {
            context.assets.open(assetPath).use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Se falhar silenciosamente aqui, o build vai falhar depois informando o erro
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
            throw RuntimeException("Command failed ($exitCode):\n${command.joinToString(" ")}\nOutput:\n$output")
        }
        return output
    }
}
