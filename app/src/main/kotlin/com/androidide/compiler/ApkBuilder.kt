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
            
            // Garante que o diretório existe
            outputDir.mkdirs()

            ZipOutputStream(FileOutputStream(outputApk)).use { zos ->
                // 1. Copiar recursos do APK base (resources.ap_)
                if (resourcesApk.exists()) {
                    ZipFile(resourcesApk).use { zip ->
                        zip.entries().asSequence().forEach { entry ->
                            // Não copiar o AndroidManifest do resources.ap_ se já vamos adicionar um novo ou processado
                            // Mas geralmente o aapt2 já empacota o manifesto binário corretamente.
                            zos.putNextEntry(ZipEntry(entry.name))
                            zip.getInputStream(entry).copyTo(zos)
                            zos.closeEntry()
                        }
                    }
                }

                // 2. Adicionar arquivos DEX (classes compiladas)
                if (dexDir.exists()) {
                    dexDir.listFiles()?.filter { it.extension == "dex" }?.forEach { dexFile ->
                        zos.putNextEntry(ZipEntry(dexFile.name))
                        dexFile.inputStream().copyTo(zos)
                        zos.closeEntry()
                    }
                }

                // 3. Adicionar Native Libraries (se houver) - Exemplo básico
                val libDir = File(project.projectDir, "app/src/main/jniLibs")
                if (libDir.exists()) {
                     libDir.walkTopDown().forEach { file ->
                        if (file.isFile) {
                            val relativePath = file.relativeTo(libDir).path
                            zos.putNextEntry(ZipEntry("lib/$relativePath"))
                            file.inputStream().copyTo(zos)
                            zos.closeEntry()
                        }
                     }
                }
            }
            
            BuildResult(success = true)
        } catch (e: Exception) {
            e.printStackTrace()
            BuildResult(
                success = false,
                errors = listOf(BuildError("", 0, 0, "Erro ao empacotar APK: ${e.message}"))
            )
        }
    }
}
