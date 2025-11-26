package com.androidide.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class SourceFile(
    val path: String,
    val name: String,
    val type: FileType,
    var content: String = "",
    var isModified: Boolean = false
) : Parcelable {

    enum class FileType {
        KOTLIN,
        JAVA,
        XML,
        GRADLE,
        JSON,
        TEXT,
        UNKNOWN
    }

    companion object {
        fun fromFile(file: File): SourceFile {
            val type = when (file.extension.lowercase()) {
                "kt" -> FileType.KOTLIN
                "java" -> FileType.JAVA
                "xml" -> FileType.XML
                "gradle", "kts" -> FileType.GRADLE
                "json" -> FileType.JSON
                "txt", "md" -> FileType.TEXT
                else -> FileType.UNKNOWN
            }

            return SourceFile(
                path = file.absolutePath,
                name = file.name,
                type = type,
                content = if (file.exists()) file.readText() else ""
            )
        }
    }

    fun save(): Boolean {
        return try {
            File(path).writeText(content)
            isModified = false
            true
        } catch (e: Exception) {
            false
        }
    }
}
