package com.androidide

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
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
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
