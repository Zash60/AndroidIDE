package com.androidide.compiler

import com.androidide.model.BuildResult
import com.androidide.model.BuildError
import com.androidide.project.Project
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class ApkBuilder(private val project: Project) {

    fun build(
        dexDir: File,
        resourcesApk: File,
        outputDir: File
    ): BuildResult {
        return try {
            val outputApk = File(outputDir, "${project.name}-unsigned.apk")

            ZipOutputStream(FileOutputStream(outputApk)).use { zos ->
                // Adicionar recursos do resources.ap_
                if (resourcesApk.exists()) {
                    ZipFile(resourcesApk).use { zip ->
                        zip.entries().asSequence().forEach { entry ->
                            zos.putNextEntry(ZipEntry(entry.name))
                            zip.getInputStream(entry).copyTo(zos)
                            zos.closeEntry()
                        }
                    }
                }

                // Adicionar DEX files
                dexDir.listFiles()?.filter { it.extension == "dex" }?.forEach { dexFile ->
                    zos.putNextEntry(ZipEntry(dexFile.name))
                    dexFile.inputStream().copyTo(zos)
                    zos.closeEntry()
                }

                // Adicionar assets se existirem
                val assetsDir = File(project.srcDir, "assets")
                if (assetsDir.exists()) {
                    assetsDir.walkTopDown().forEach { file ->
                        if (file.isFile) {
                            val entryName = "assets/${file.relativeTo(assetsDir).path}"
                            zos.putNextEntry(ZipEntry(entryName))
                            file.inputStream().copyTo(zos)
                            zos.closeEntry()
                        }
                    }
