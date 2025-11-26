package com.androidide.project

import android.os.Parcelable
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import java.io.File
import java.util.Date

@Parcelize
data class Project(
    val name: String,
    val packageName: String,
    val path: String,
    val minSdk: Int = 24,
    val targetSdk: Int = 34,
    val versionCode: Int = 1,
    val versionName: String = "1.0.0",
    val createdAt: Long = System.currentTimeMillis(),
    var lastModified: Long = System.currentTimeMillis()
) : Parcelable {

    val projectDir: File get() = File(path)
    val srcDir: File get() = File(projectDir, "app/src/main")
    val kotlinDir: File get() = File(srcDir, "kotlin")
    val javaDir: File get() = File(srcDir, "java")
    val resDir: File get() = File(srcDir, "res")
    val manifestFile: File get() = File(srcDir, "AndroidManifest.xml")
    val buildDir: File get() = File(projectDir, "build")
    val outputDir: File get() = File(buildDir, "outputs")

    val packagePath: String get() = packageName.replace(".", "/")

    fun getSourceDir(): File {
        val ktDir = File(kotlinDir, packagePath)
        val javaDir = File(javaDir, packagePath)
        return when {
            ktDir.exists() -> ktDir
            javaDir.exists() -> javaDir
            else -> ktDir.also { it.mkdirs() }
        }
    }

    companion object {
        private const val CONFIG_FILE = "project.json"

        fun load(projectDir: File): Project? {
            val configFile = File(projectDir, CONFIG_FILE)
            return if (configFile.exists()) {
                try {
                    Gson().fromJson(configFile.readText(), Project::class.java)
                } catch (e: Exception) {
                    null
                }
            } else null
        }
    }

    fun save() {
        lastModified = System.currentTimeMillis()
        val configFile = File(projectDir, CONFIG_FILE)
        configFile.writeText(Gson().toJson(this))
    }
}
