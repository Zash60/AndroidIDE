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
        
        // 2. Copia e Prepara AAPT2
        val aapt2 = File(sdkDir, "aapt2")
        copyAsset(context, "sdk/aapt2", aapt2)
        
        // 3. Dá permissão de execução (chmod +x)
        if (aapt2.exists()) {
            val executable = aapt2.setExecutable(true)
            if (!executable) {
                // Fallback para versões antigas do Android que podem precisar de comando shell
                Log.w(TAG, "setExecutable falhou via Java, tentando chmod...")
                try {
                    Runtime.getRuntime().exec("chmod 700 ${aapt2.absolutePath}").waitFor()
                } catch (e: Exception) {
                    Log.e(TAG, "Falha ao dar permissão ao aapt2", e)
                }
            }
        }
    }

    private fun copyAsset(context: Context, assetPath: String, destFile: File) {
        // Só copia se o arquivo não existir ou se tiver tamanho diferente (atualização)
        // Para desenvolvimento, podemos forçar a cópia se deletarmos a pasta antes
        if (destFile.exists()) return 

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
            throw RuntimeException("Command failed ($exitCode):\n${command.joinToString(" ")}\nOutput:\n$output")
        }
        return output
    }
}
