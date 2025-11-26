package com.androidide.model

data class BuildResult(
    val success: Boolean,
    val apkPath: String? = null,
    val errors: List<BuildError> = emptyList(),
    val warnings: List<BuildWarning> = emptyList(),
    val duration: Long = 0
)

data class BuildError(
    val file: String = "",
    val line: Int = 0,
    val column: Int = 0,
    val message: String
)

data class BuildWarning(
    val file: String = "",
    val line: Int = 0,
    val message: String
)

enum class BuildStep {
    PREPARING,
    COMPILING_RESOURCES,
    COMPILING_KOTLIN,
    COMPILING_JAVA,
    CREATING_DEX,
    PACKAGING,
    SIGNING,
    DONE
}
