package com.androidide.model

data class BuildResult(
    val success: Boolean,
    val apkPath: String? = null,
    val errors: List<String> = emptyList(),
    val duration: Long = 0
)
