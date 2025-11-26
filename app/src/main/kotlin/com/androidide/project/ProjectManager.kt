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

    fun createProject(
        name: String,
        packageName: String,
        minSdk: Int = 24,
        targetSdk: Int = 34
    ): Project {
        val projectDir = File(App.projectsDir, name.replace(" ", "_"))
        projectDir.mkdirs()

        val project = Project(
            name = name,
            packageName = packageName,
            path = projectDir.absolutePath,
            minSdk = minSdk,
            targetSdk = targetSdk
        )

        // Criar estrutura
        createProjectStructure(project)
        project.save()

        return project
    }

    private fun createProjectStructure(project: Project) {
        // Diretórios
        project.kotlinDir.mkdirs()
        File(project.kotlinDir, project.packagePath).mkdirs()
        project.resDir.mkdirs()
        File(project.resDir, "layout").mkdirs()
        File(project.resDir, "values").mkdirs()
        File(project.resDir, "drawable").mkdirs()
        File(project.resDir, "mipmap-hdpi").mkdirs()
        File(project.resDir, "mipmap-xhdpi").mkdirs()
        File(project.resDir, "mipmap-xxhdpi").mkdirs()
        project.buildDir.mkdirs()
        project.outputDir.mkdirs()

        // AndroidManifest.xml
        project.manifestFile.writeText(ProjectTemplate.getManifest(project))

        // MainActivity.kt
        val mainActivity = File(project.kotlinDir, "${project.packagePath}/MainActivity.kt")
        mainActivity.parentFile?.mkdirs()
        mainActivity.writeText(ProjectTemplate.getMainActivity(project))

        // activity_main.xml
        val mainLayout = File(project.resDir, "layout/activity_main.xml")
        mainLayout.writeText(ProjectTemplate.getMainLayout())

        // strings.xml
        val strings = File(project.resDir, "values/strings.xml")
        strings.writeText(ProjectTemplate.getStrings(project))

        // colors.xml
        val colors = File(project.resDir, "values/colors.xml")
        colors.writeText(ProjectTemplate.getColors())

        // themes.xml
        val themes = File(project.resDir, "values/themes.xml")
        themes.writeText(ProjectTemplate.getThemes(project))
    }

    fun deleteProject(project: Project): Boolean {
        return project.projectDir.deleteRecursively()
    }

    fun duplicateProject(project: Project, newName: String): Project {
        val newProject = createProject(newName, project.packageName)
        
        // Copiar arquivos
        project.srcDir.copyRecursively(newProject.srcDir, overwrite = true)
        
        return newProject
    }
}
