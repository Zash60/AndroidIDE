package com.androidide.project

import com.androidide.App
import java.io.File

object ProjectManager {

    fun getAllProjects(): List<Project> {
        val projects = mutableListOf<Project>()
        App.projectsDir.listFiles()?.forEach { dir ->
            if (dir.isDirectory) {
                Project.load(dir)?.let { projects.add(it) }
            }
        }
        return projects.sortedByDescending { it.lastModified }
    }

    fun createProject(name: String, packageName: String, minSdk: Int = 24, targetSdk: Int = 34): Project {
        val projectDir = File(App.projectsDir, name.replace(" ", "_"))
        projectDir.mkdirs()

        val project = Project(
            name = name,
            packageName = packageName,
            path = projectDir.absolutePath,
            minSdk = minSdk,
            targetSdk = targetSdk
        )

        createProjectStructure(project)
        project.save()
        return project
    }

    private fun createProjectStructure(project: Project) {
        project.kotlinDir.mkdirs()
        File(project.kotlinDir, project.packagePath).mkdirs()
        project.resDir.mkdirs()
        File(project.resDir, "layout").mkdirs()
        File(project.resDir, "values").mkdirs()

        // AndroidManifest.xml
        project.manifestFile.parentFile?.mkdirs()
        project.manifestFile.writeText(ProjectTemplate.getManifest(project))

        // MainActivity.kt
        val mainActivity = File(project.kotlinDir, "${project.packagePath}/MainActivity.kt")
        mainActivity.parentFile?.mkdirs()
        mainActivity.writeText(ProjectTemplate.getMainActivity(project))

        // activity_main.xml
        File(project.resDir, "layout/activity_main.xml").writeText(ProjectTemplate.getMainLayout())

        // strings.xml
        File(project.resDir, "values/strings.xml").writeText(ProjectTemplate.getStrings(project))
    }

    fun deleteProject(project: Project): Boolean = project.projectDir.deleteRecursively()
}
