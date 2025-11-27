package com.androidide.utils

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

object GitManager {

    fun cloneRepository(url: String, destDir: File): Boolean {
        return try {
            if (destDir.exists()) destDir.deleteRecursively()
            Git.cloneRepository()
                .setURI(url)
                .setDirectory(destDir)
                .call()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun pull(projectDir: File): Boolean {
        return try {
            val git = Git.open(projectDir)
            git.pull().call()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun commitAndPush(projectDir: File, message: String, token: String): Boolean {
        return try {
            val git = Git.open(projectDir)
            git.add().addFilepattern(".").call()
            git.commit().setMessage(message).call()
            
            if (token.isNotEmpty()) {
                val credentials = UsernamePasswordCredentialsProvider(token, "")
                git.push().setCredentialsProvider(credentials).call()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
