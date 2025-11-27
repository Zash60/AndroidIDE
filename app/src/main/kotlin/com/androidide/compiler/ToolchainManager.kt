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
        if (!aapt2.exists()) {
            copyAsset(context, "sdk/aapt2", aapt2)
        }
        
        // 3. TORNA EXECUTÁVEL (Substitua o bloco try-catch existente por isso)
        if (aapt2.exists()) {
            if (!aapt2.setExecutable(true, false)) {  // true para executável, false para apenas o owner (app)
                Log.e(TAG, "Falha ao tornar aapt2 executável")
                // Opcional: Lance uma exceção ou notifique o usuário se falhar
            } else {
                Log.d(TAG, "aapt2 agora é executável")
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
