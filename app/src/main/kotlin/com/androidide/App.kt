package com.androidide

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.androidide.utils.Logger
import java.io.File

class App : Application() {

    companion object {
        lateinit var instance: App
            private set

        const val CHANNEL_BUILD = "build_channel"

        val projectsDir: File
            get() = File(instance.getExternalFilesDir(null), "projects")

        val sdkDir: File
            get() = File(instance.filesDir, "sdk")

        val tempDir: File
            get() = File(instance.cacheDir, "temp")
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        initDirectories()
        createNotificationChannels()
        extractSdkFiles()

        Logger.init(this)
    }

    private fun initDirectories() {
        projectsDir.mkdirs()
        sdkDir.mkdirs()
        tempDir.mkdirs()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_BUILD,
                "Build Progress",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows build progress"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun extractSdkFiles() {
        // Extrair arquivos necessários do assets para o SDK
        val androidJar = File(sdkDir, "android.jar")
        if (!androidJar.exists()) {
            assets.open("sdk/android.jar").use { input ->
                androidJar.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        val kotlinStdlib = File(sdkDir, "kotlin-stdlib.jar")
        if (!kotlinStdlib.exists()) {
            assets.open("sdk/kotlin-stdlib.jar").use { input ->
                kotlinStdlib.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }
}
