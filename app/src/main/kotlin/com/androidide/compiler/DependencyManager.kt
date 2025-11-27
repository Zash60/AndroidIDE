package com.androidide.compiler

import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipFile

/**
 * Gerencia o download e extração de dependências do Maven Central/Google Maven.
 */
object DependencyManager {

    private const val MAVEN_CENTRAL = "https://repo1.maven.org/maven2"
    private const val GOOGLE_MAVEN = "https://maven.google.com"

    data class Dependency(val group: String, val name: String, val version: String)

    /**
     * Resolve dependências listadas no arquivo gradle e retorna a lista de JARs para o classpath.
     */
    fun resolveDependencies(gradleFile: File, cacheDir: File): List<File> {
        if (!cacheDir.exists()) cacheDir.mkdirs()
        
        val dependencies = parseDependencies(gradleFile)
        val classPathFiles = mutableListOf<File>()

        for (dep in dependencies) {
            try {
                // 1. Tenta baixar como JAR
                val jarName = "${dep.name}-${dep.version}.jar"
                val jarFile = File(cacheDir, jarName)
                
                if (jarFile.exists()) {
                    classPathFiles.add(jarFile)
                    continue
                }

                // Caminho Maven: group/name/version/artifact.jar
                val path = "${dep.group.replace('.', '/')}/${dep.name}/${dep.version}"
                
                // Tenta Maven Central (JAR)
                if (downloadFile("$MAVEN_CENTRAL/$path/$jarName", jarFile)) {
                    classPathFiles.add(jarFile)
                    continue
                }
                
                // Tenta Google Maven (JAR)
                if (downloadFile("$GOOGLE_MAVEN/$path/$jarName", jarFile)) {
                    classPathFiles.add(jarFile)
                    continue
                }

                // 2. Se falhar JAR, tenta como AAR (Android Archive)
                val aarName = "${dep.name}-${dep.version}.aar"
                val aarFile = File(cacheDir, aarName)
                
                var aarDownloaded = false
                if (downloadFile("$MAVEN_CENTRAL/$path/$aarName", aarFile)) {
                    aarDownloaded = true
                } else if (downloadFile("$GOOGLE_MAVEN/$path/$aarName", aarFile)) {
                    aarDownloaded = true
                }

                if (aarDownloaded) {
                    // Extrai o classes.jar de dentro do AAR
                    val extractedJar = File(cacheDir, "${dep.name}-${dep.version}-classes.jar")
                    if (extractClassesJarFromAar(aarFile, extractedJar)) {
                        classPathFiles.add(extractedJar)
                    }
                }

            } catch (e: Exception) {
                println("Erro ao resolver dependência ${dep.name}: ${e.message}")
            }
        }
        return classPathFiles
    }

    private fun parseDependencies(file: File): List<Dependency> {
        if (!file.exists()) return emptyList()
        val deps = mutableListOf<Dependency>()
        
        // Regex simples para capturar: implementation("com.exemplo:lib:1.0.0")
        // Suporta aspas simples ou duplas
        val regex = "implementation\\s*[(\"]+([^:]+):([^:]+):([^\"')]+)[\"')]+".toRegex()
        
        file.forEachLine { line ->
            val match = regex.find(line)
            if (match != null) {
                val (group, name, version) = match.destructured
                deps.add(Dependency(group, name, version))
            }
        }
        return deps
    }

    private fun downloadFile(urlStr: String, destination: File): Boolean {
        return try {
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 10000

            if (connection.responseCode == 200) {
                connection.inputStream.use { input ->
                    FileOutputStream(destination).use { output ->
                        input.copyTo(output)
                    }
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun extractClassesJarFromAar(aarFile: File, destJar: File): Boolean {
        return try {
            ZipFile(aarFile).use { zip ->
                val entry = zip.getEntry("classes.jar") ?: return false
                zip.getInputStream(entry).use { input ->
                    FileOutputStream(destJar).use { output ->
                        input.copyTo(output)
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
