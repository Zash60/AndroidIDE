#!/bin/bash

set -e

echo "🚀 Creating Complete Android IDE Project Structure..."

# =============================================================================
# CRIAR DIRETÓRIOS
# =============================================================================

echo "📁 Creating directories..."

mkdir -p app/src/main/kotlin/com/androidide/ui/editor
mkdir -p app/src/main/kotlin/com/androidide/ui/project
mkdir -p app/src/main/kotlin/com/androidide/ui/filemanager
mkdir -p app/src/main/kotlin/com/androidide/ui/build
mkdir -p app/src/main/kotlin/com/androidide/ui/settings
mkdir -p app/src/main/kotlin/com/androidide/compiler
mkdir -p app/src/main/kotlin/com/androidide/project
mkdir -p app/src/main/kotlin/com/androidide/model
mkdir -p app/src/main/kotlin/com/androidide/utils
mkdir -p app/src/main/res/layout
mkdir -p app/src/main/res/values
mkdir -p app/src/main/res/drawable
mkdir -p app/src/main/res/menu
mkdir -p app/src/main/res/xml
mkdir -p app/src/main/res/mipmap-anydpi-v26
mkdir -p app/src/main/assets/sdk
mkdir -p gradle/wrapper

# =============================================================================
# GRADLE FILES
# =============================================================================

echo "📦 Creating Gradle files..."

cat > settings.gradle.kts << 'GRADLE_SETTINGS'
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "AndroidIDE"
include(":app")
GRADLE_SETTINGS

cat > build.gradle.kts << 'GRADLE_ROOT'
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false
}
GRADLE_ROOT

cat > gradle.properties << 'GRADLE_PROPS'
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8 -XX:+UseParallelGC
org.gradle.parallel=true
org.gradle.caching=true
android.useAndroidX=true
android.nonTransitiveRClass=true
kotlin.code.style=official
GRADLE_PROPS

cat > app/build.gradle.kts << 'APP_GRADLE'
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.androidide"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.androidide"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        multiDexEnabled = true
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/*.kotlin_module",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/versions/9/previous-compilation-data.bin"
            )
        }
    }

    lint {
        abortOnError = false
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("io.github.Rosemoe.sora-editor:editor:0.23.2")
    implementation("io.github.Rosemoe.sora-editor:language-textmate:0.23.2")
    implementation("com.google.code.gson:gson:2.10.1")
}
APP_GRADLE

cat > app/proguard-rules.pro << 'PROGUARD'
-keepattributes *Annotation*
-keep class io.github.rosemoe.sora.** { *; }
PROGUARD

# =============================================================================
# ANDROID MANIFEST
# =============================================================================

echo "📱 Creating AndroidManifest.xml..."

cat > app/src/main/AndroidManifest.xml << 'MANIFEST'
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" tools:ignore="ScopedStorage" />
    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:name=".App"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.AndroidIDE"
        android:largeHeap="true"
        android:requestLegacyExternalStorage="true"
        tools:targetApi="34">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.AndroidIDE.Splash">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity android:name=".ui.project.ProjectManagerActivity" />
        <activity android:name=".ui.project.CreateProjectActivity" />
        <activity android:name=".ui.editor.EditorActivity" android:windowSoftInputMode="adjustResize" />
        <activity android:name=".ui.build.BuildActivity" />
        <activity android:name=".ui.settings.SettingsActivity" />

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>

    </application>
</manifest>
MANIFEST

# =============================================================================
# KOTLIN SOURCE FILES
# =============================================================================

echo "📝 Creating Kotlin source files..."

# ===== App.kt =====
cat > app/src/main/kotlin/com/androidide/App.kt << 'KOTLIN'
package com.androidide

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import java.io.File

class App : Application() {

    companion object {
        lateinit var instance: App
            private set

        const val CHANNEL_BUILD = "build_channel"

        val projectsDir: File
            get() = File(instance.getExternalFilesDir(null), "projects")

        val sdkDir: File
            get() = File(instance.filesDir, "sdk")

        val tempDir: File
            get() = File(instance.cacheDir, "temp")
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initDirectories()
        createNotificationChannels()
    }

    private fun initDirectories() {
        projectsDir.mkdirs()
        sdkDir.mkdirs()
        tempDir.mkdirs()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_BUILD,
                "Build Progress",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
KOTLIN

# ===== MainActivity.kt =====
cat > app/src/main/kotlin/com/androidide/MainActivity.kt << 'KOTLIN'
package com.androidide

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.androidide.databinding.ActivityMainBinding
import com.androidide.ui.project.ProjectManagerActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            proceedToApp()
        } else {
            showPermissionDenied()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermissions()
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                proceedToApp()
            } else {
                requestAllFilesPermission()
            }
        } else {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val notGranted = permissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }
            if (notGranted.isEmpty()) {
                proceedToApp()
            } else {
                permissionLauncher.launch(notGranted.toTypedArray())
            }
        }
    }

    private fun requestAllFilesPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, 100)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent, 100)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                proceedToApp()
            } else {
                showPermissionDenied()
            }
        }
    }

    private fun proceedToApp() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.buttonRetry.visibility = View.GONE
            binding.textStatus.text = "Carregando..."
            delay(1000)
            startActivity(Intent(this@MainActivity, ProjectManagerActivity::class.java))
            finish()
        }
    }

    private fun showPermissionDenied() {
        binding.progressBar.visibility = View.GONE
        binding.textStatus.text = "Permissão necessária"
        binding.buttonRetry.visibility = View.VISIBLE
        binding.buttonRetry.setOnClickListener { checkPermissions() }
    }
}
KOTLIN

# ===== model/SourceFile.kt =====
cat > app/src/main/kotlin/com/androidide/model/SourceFile.kt << 'KOTLIN'
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
        KOTLIN, JAVA, XML, GRADLE, JSON, TEXT, UNKNOWN
    }

    companion object {
        fun fromFile(file: File): SourceFile {
            val type = when (file.extension.lowercase()) {
                "kt" -> FileType.KOTLIN
                "java" -> FileType.JAVA
                "xml" -> FileType.XML
                "gradle", "kts" -> FileType.GRADLE
                "json" -> FileType.JSON
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
KOTLIN

# ===== model/BuildResult.kt =====
cat > app/src/main/kotlin/com/androidide/model/BuildResult.kt << 'KOTLIN'
package com.androidide.model

data class BuildResult(
    val success: Boolean,
    val apkPath: String? = null,
    val errors: List<String> = emptyList(),
    val duration: Long = 0
)
KOTLIN

# ===== project/Project.kt =====
cat > app/src/main/kotlin/com/androidide/project/Project.kt << 'KOTLIN'
package com.androidide.project

import android.os.Parcelable
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import java.io.File

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
    val resDir: File get() = File(srcDir, "res")
    val manifestFile: File get() = File(srcDir, "AndroidManifest.xml")
    val packagePath: String get() = packageName.replace(".", "/")

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
        File(projectDir, "project.json").writeText(Gson().toJson(this))
    }
}
KOTLIN

# ===== project/ProjectManager.kt =====
cat > app/src/main/kotlin/com/androidide/project/ProjectManager.kt << 'KOTLIN'
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
KOTLIN

# ===== project/ProjectTemplate.kt =====
cat > app/src/main/kotlin/com/androidide/project/ProjectTemplate.kt << 'KOTLIN'
package com.androidide.project

object ProjectTemplate {

    fun getManifest(project: Project): String = """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="${project.packageName}">
    <application
        android:allowBackup="true"
        android:label="@string/app_name"
        android:theme="@android:style/Theme.Material.Light.NoActionBar">
        <activity android:name=".MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
    """.trimIndent()

    fun getMainActivity(project: Project): String = """
package ${project.packageName}

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.textView).text = "Hello from ${project.name}!"
    }
}
    """.trimIndent()

    fun getMainLayout(): String = """
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical">
    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        android:textSize="24sp" />
</LinearLayout>
    """.trimIndent()

    fun getStrings(project: Project): String = """
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">${project.name}</string>
</resources>
    """.trimIndent()
}
KOTLIN

# ===== ui/project/ProjectManagerActivity.kt =====
cat > app/src/main/kotlin/com/androidide/ui/project/ProjectManagerActivity.kt << 'KOTLIN'
package com.androidide.ui.project

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidide.R
import com.androidide.databinding.ActivityProjectManagerBinding
import com.androidide.project.Project
import com.androidide.project.ProjectManager
import com.androidide.ui.editor.EditorActivity
import com.androidide.ui.settings.SettingsActivity

class ProjectManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjectManagerBinding
    private lateinit var adapter: ProjectAdapter
    private val projects = mutableListOf<Project>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Android IDE"

        setupRecyclerView()
        binding.fabNewProject.setOnClickListener {
            startActivity(Intent(this, CreateProjectActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadProjects()
    }

    private fun setupRecyclerView() {
        adapter = ProjectAdapter(
            projects = projects,
            onProjectClick = { openProject(it) },
            onProjectLongClick = { showProjectOptions(it) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun loadProjects() {
        projects.clear()
        projects.addAll(ProjectManager.getAllProjects())
        adapter.notifyDataSetChanged()
        binding.emptyView.visibility = if (projects.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun openProject(project: Project) {
        val intent = Intent(this, EditorActivity::class.java)
        intent.putExtra("project", project)
        startActivity(intent)
    }

    private fun showProjectOptions(project: Project) {
        AlertDialog.Builder(this)
            .setTitle(project.name)
            .setItems(arrayOf("Abrir", "Excluir")) { _, which ->
                when (which) {
                    0 -> openProject(project)
                    1 -> confirmDelete(project)
                }
            }
            .show()
    }

    private fun confirmDelete(project: Project) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Projeto")
            .setMessage("Excluir ${project.name}?")
            .setPositiveButton("Excluir") { _, _ ->
                ProjectManager.deleteProject(project)
                loadProjects()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_project_manager, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_refresh -> {
                loadProjects()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
KOTLIN

# ===== ui/project/ProjectAdapter.kt =====
cat > app/src/main/kotlin/com/androidide/ui/project/ProjectAdapter.kt << 'KOTLIN'
package com.androidide.ui.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidide.databinding.ItemProjectBinding
import com.androidide.project.Project
import java.text.SimpleDateFormat
import java.util.*

class ProjectAdapter(
    private val projects: List<Project>,
    private val onProjectClick: (Project) -> Unit,
    private val onProjectLongClick: (Project) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    inner class ViewHolder(private val binding: ItemProjectBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.textProjectName.text = project.name
            binding.textPackageName.text = project.packageName
            binding.textLastModified.text = dateFormat.format(Date(project.lastModified))
            binding.textSdkVersion.text = "SDK: ${project.minSdk}-${project.targetSdk}"
            binding.root.setOnClickListener { onProjectClick(project) }
            binding.root.setOnLongClickListener { onProjectLongClick(project); true }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(projects[position])
    override fun getItemCount() = projects.size
}
KOTLIN

# ===== ui/project/CreateProjectActivity.kt =====
cat > app/src/main/kotlin/com/androidide/ui/project/CreateProjectActivity.kt << 'KOTLIN'
package com.androidide.ui.project

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidide.databinding.ActivityCreateProjectBinding
import com.androidide.project.ProjectManager

class CreateProjectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProjectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Novo Projeto"

        setupSpinners()
        setupListeners()
    }

    private fun setupSpinners() {
        val sdkVersions = (21..34).map { "API $it" }.toTypedArray()
        binding.spinnerMinSdk.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sdkVersions)
        binding.spinnerMinSdk.setSelection(3)
        binding.spinnerTargetSdk.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sdkVersions)
        binding.spinnerTargetSdk.setSelection(13)
    }

    private fun setupListeners() {
        binding.buttonCreate.setOnClickListener { createProject() }
        binding.buttonCancel.setOnClickListener { finish() }
    }

    private fun createProject() {
        val name = binding.editProjectName.text.toString().trim()
        val packageName = binding.editPackageName.text.toString().trim()

        if (name.isEmpty()) {
            binding.inputLayoutName.error = "Nome obrigatório"
            return
        }
        if (packageName.isEmpty()) {
            binding.inputLayoutPackage.error = "Package obrigatório"
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.buttonCreate.isEnabled = false

        try {
            val minSdk = 21 + binding.spinnerMinSdk.selectedItemPosition
            val targetSdk = 21 + binding.spinnerTargetSdk.selectedItemPosition
            ProjectManager.createProject(name, packageName, minSdk, targetSdk)
            Toast.makeText(this, "Projeto criado!", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            binding.buttonCreate.isEnabled = true
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
KOTLIN

# ===== ui/editor/EditorActivity.kt =====
cat > app/src/main/kotlin/com/androidide/ui/editor/EditorActivity.kt << 'KOTLIN'
package com.androidide.ui.editor

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidide.R
import com.androidide.databinding.ActivityEditorBinding
import com.androidide.model.SourceFile
import com.androidide.project.Project
import com.androidide.ui.build.BuildActivity
import com.androidide.ui.filemanager.FileAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class EditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorBinding
    private lateinit var project: Project
    private lateinit var fileAdapter: FileAdapter
    private var currentFile: SourceFile? = null
    private val openFiles = mutableListOf<SourceFile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        project = intent.getParcelableExtra("project")!!
        
        setupToolbar()
        setupDrawer()
        setupEditor()
        setupFileTree()
        setupTabs()
        loadProjectFiles()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = project.name
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.drawer_open, R.string.drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setupEditor() {
        binding.codeEditor.apply {
            setTextSize(14f)
            setTabWidth(4)
        }
    }

    private fun setupFileTree() {
        fileAdapter = FileAdapter { file ->
            openFile(file)
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        binding.fileTreeRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.fileTreeRecyclerView.adapter = fileAdapter
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : 
            com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                tab?.position?.let { pos ->
                    if (pos < openFiles.size) switchToFile(openFiles[pos])
                }
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }

    private fun loadProjectFiles() {
        lifecycleScope.launch {
            val files = withContext(Dispatchers.IO) {
                project.srcDir.walkTopDown().filter { it.isFile }.toList()
            }
            fileAdapter.setFiles(files, project.srcDir)
        }
    }

    private fun openFile(file: File) {
        if (file.isDirectory) return
        
        val existing = openFiles.find { it.path == file.absolutePath }
        if (existing != null) {
            switchToFile(existing)
            return
        }

        lifecycleScope.launch {
            val sourceFile = withContext(Dispatchers.IO) { SourceFile.fromFile(file) }
            openFiles.add(sourceFile)
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(sourceFile.name))
            switchToFile(sourceFile)
        }
    }

    private fun switchToFile(file: SourceFile) {
        currentFile?.content = binding.codeEditor.text.toString()
        currentFile = file
        binding.codeEditor.setText(file.content)
        val index = openFiles.indexOf(file)
        if (index >= 0) binding.tabLayout.getTabAt(index)?.select()
    }

    private fun saveCurrentFile() {
        currentFile?.let { file ->
            file.content = binding.codeEditor.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                val success = file.save()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditorActivity, 
                        if (success) "Salvo!" else "Erro ao salvar", 
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> { saveCurrentFile(); true }
            R.id.action_build -> { 
                startActivity(Intent(this, BuildActivity::class.java).putExtra("project", project))
                true 
            }
            R.id.action_undo -> { binding.codeEditor.undo(); true }
            R.id.action_redo -> { binding.codeEditor.redo(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
KOTLIN

# ===== ui/filemanager/FileAdapter.kt =====
cat > app/src/main/kotlin/com/androidide/ui/filemanager/FileAdapter.kt << 'KOTLIN'
package com.androidide.ui.filemanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidide.R
import com.androidide.databinding.ItemFileBinding
import java.io.File

class FileAdapter(
    private val onFileClick: (File) -> Unit
) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    private val items = mutableListOf<FileItem>()

    data class FileItem(val file: File, val depth: Int)

    inner class ViewHolder(private val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FileItem) {
            val padding = item.depth * 24
            binding.root.setPadding(padding, 8, 16, 8)
            binding.textFileName.text = item.file.name
            
            val iconRes = when {
                item.file.isDirectory -> R.drawable.ic_folder
                item.file.extension == "kt" -> R.drawable.ic_kotlin
                item.file.extension == "java" -> R.drawable.ic_java
                item.file.extension == "xml" -> R.drawable.ic_xml
                else -> R.drawable.ic_file
            }
            binding.imageFileIcon.setImageResource(iconRes)
            binding.root.setOnClickListener { onFileClick(item.file) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size

    fun setFiles(files: List<File>, rootDir: File) {
        items.clear()
        files.sortedWith(compareBy({ !it.isDirectory }, { it.name })).forEach { file ->
            val depth = file.absolutePath.removePrefix(rootDir.absolutePath).count { it == '/' }
            items.add(FileItem(file, depth))
        }
        notifyDataSetChanged()
    }
}
KOTLIN

# ===== ui/build/BuildActivity.kt =====
cat > app/src/main/kotlin/com/androidide/ui/build/BuildActivity.kt << 'KOTLIN'
package com.androidide.ui.build

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.androidide.databinding.ActivityBuildBinding
import com.androidide.project.Project

class BuildActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuildBinding
    private lateinit var project: Project

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuildBinding.inflate(layoutInflater)
        setContentView(binding.root)

        project = intent.getParcelableExtra("project")!!
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Build: ${project.name}"

        binding.textStatus.text = "Build não implementado ainda"
        binding.textLogs.text = """
            Android IDE - Build System
            
            Este é um placeholder para o sistema de build.
            
            Para compilar apps Android no dispositivo, seria necessário:
            1. AAPT2 para compilar recursos
            2. Compilador Kotlin/Java
            3. D8/R8 para criar DEX
            4. Ferramenta de empacotamento APK
            5. Assinatura de APK
            
            Projeto: ${project.name}
            Package: ${project.packageName}
            Min SDK: ${project.minSdk}
            Target SDK: ${project.targetSdk}
        """.trimIndent()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
KOTLIN

# ===== ui/settings/SettingsActivity.kt =====
cat > app/src/main/kotlin/com/androidide/ui/settings/SettingsActivity.kt << 'KOTLIN'
package com.androidide.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.androidide.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Configurações"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
KOTLIN

# =============================================================================
# RESOURCE FILES
# =============================================================================

echo "🎨 Creating resource files..."

# values/strings.xml
cat > app/src/main/res/values/strings.xml << 'XML'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Android IDE</string>
    <string name="drawer_open">Abrir</string>
    <string name="drawer_close">Fechar</string>
    <string name="action_save">Salvar</string>
    <string name="action_build">Compilar</string>
    <string name="action_settings">Configurações</string>
    <string name="action_refresh">Atualizar</string>
</resources>
XML

# values/colors.xml
cat > app/src/main/res/values/colors.xml << 'XML'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    <color name="white">#FFFFFFFF</color>
    <color name="black">#FF000000</color>
    <color name="editor_bg">#2B2B2B</color>
</resources>
XML

# values/themes.xml
cat > app/src/main/res/values/themes.xml << 'XML'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.AndroidIDE" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        <item name="colorPrimary">@color/purple_500</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/white</item>
    </style>
    <style name="Theme.AndroidIDE.Splash" parent="Theme.AndroidIDE">
        <item name="android:windowBackground">@color/purple_700</item>
    </style>
</resources>
XML

# xml/file_paths.xml
cat > app/src/main/res/xml/file_paths.xml << 'XML'
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path name="external" path="." />
    <files-path name="files" path="." />
</paths>
XML

# Drawables
cat > app/src/main/res/drawable/ic_file.xml << 'XML'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#757575" android:pathData="M14,2H6C4.9,2 4,2.9 4,4v16c0,1.1 0.9,2 2,2h12c1.1,0 2,-0.9 2,-2V8L14,2zM13,9V3.5L18.5,9H13z"/>
</vector>
XML

cat > app/src/main/res/drawable/ic_folder.xml << 'XML'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFC107" android:pathData="M10,4H4c-1.1,0 -2,0.9 -2,2v12c0,1.1 0.9,2 2,2h16c1.1,0 2,-0.9 2,-2V8c0,-1.1 -0.9,-2 -2,-2h-8l-2,-2z"/>
</vector>
XML

cat > app/src/main/res/drawable/ic_kotlin.xml << 'XML'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#7F52FF" android:pathData="M2,2h20L12,12L22,22H2Z"/>
</vector>
XML

cat > app/src/main/res/drawable/ic_java.xml << 'XML'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#F89820" android:pathData="M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10,-4.48 10,-10S17.52,2 12,2z"/>
</vector>
XML

cat > app/src/main/res/drawable/ic_xml.xml << 'XML'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#4CAF50" android:pathData="M9.4,16.6L4.8,12l4.6,-4.6L8,6l-6,6l6,6l1.4,-1.4zM14.6,16.6l4.6,-4.6l-4.6,-4.6L16,6l6,6l-6,6l-1.4,-1.4z"/>
</vector>
XML

cat > app/src/main/res/drawable/ic_launcher_background.xml << 'XML'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108">
    <path android:fillColor="#6200EE" android:pathData="M0,0h108v108H0z"/>
</vector>
XML

cat > app/src/main/res/drawable/ic_launcher_foreground.xml << 'XML'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108">
    <path android:fillColor="#FFFFFF" android:pathData="M34,34h40v40H34z"/>
</vector>
XML

# Mipmaps
cat > app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml << 'XML'
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
XML

cat > app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml << 'XML'
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
XML

# Menus
cat > app/src/main/res/menu/menu_project_manager.xml << 'XML'
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android" xmlns:app="http://schemas.android.com/apk/res-auto">
    <item android:id="@+id/action_refresh" android:title="@string/action_refresh" app:showAsAction="ifRoom"/>
    <item android:id="@+id/action_settings" android:title="@string/action_settings" app:showAsAction="never"/>
</menu>
XML

cat > app/src/main/res/menu/menu_editor.xml << 'XML'
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android" xmlns:app="http://schemas.android.com/apk/res-auto">
    <item android:id="@+id/action_save" android:title="@string/action_save" app:showAsAction="always"/>
    <item android:id="@+id/action_build" android:title="@string/action_build" app:showAsAction="always"/>
    <item android:id="@+id/action_undo" android:title="Undo" app:showAsAction="ifRoom"/>
    <item android:id="@+id/action_redo" android:title="Redo" app:showAsAction="ifRoom"/>
</menu>
XML

# Layouts
cat > app/src/main/res/layout/activity_main.xml << 'XML'
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent" android:layout_height="match_parent"
    android:orientation="vertical" android:gravity="center" android:background="@color/purple_700">
    <ProgressBar android:id="@+id/progressBar" android:layout_width="wrap_content" android:layout_height="wrap_content"/>
    <TextView android:id="@+id/textStatus" android:layout_width="wrap_content" android:layout_height="wrap_content"
        android:text="Carregando..." android:textColor="@color/white" android:layout_marginTop="16dp"/>
    <Button android:id="@+id/buttonRetry" android:layout_width="wrap_content" android:layout_height="wrap_content"
        android:text="Tentar Novamente" android:visibility="gone"/>
</LinearLayout>
XML

cat > app/src/main/res/layout/activity_project_manager.xml << 'XML'
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto" android:layout_width="match_parent" android:layout_height="match_parent">
    <com.google.android.material.appbar.AppBarLayout android:layout_width="match_parent" android:layout_height="wrap_content">
        <androidx.appcompat.widget.Toolbar android:id="@+id/toolbar" android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize" android:background="@color/purple_500"/>
    </com.google.android.material.appbar.AppBarLayout>
    <FrameLayout android:layout_width="match_parent" android:layout_height="match_parent" app:layout_behavior="@string/appbar_scrolling_view_behavior">
        <androidx.recyclerview.widget.RecyclerView android:id="@+id/recyclerView" android:layout_width="match_parent" android:layout_height="match_parent"/>
        <TextView android:id="@+id/emptyView" android:layout_width="wrap_content" android:layout_height="wrap_content"
            android:text="Nenhum projeto" android:layout_gravity="center" android:visibility="gone"/>
    </FrameLayout>
    <com.google.android.material.floatingactionbutton.FloatingActionButton android:id="@+id/fabNewProject"
        android:layout_width="wrap_content" android:layout_height="wrap_content" android:layout_gravity="bottom|end"
        android:layout_margin="16dp" android:src="@drawable/ic_file"/>
</androidx.coordinatorlayout.widget.CoordinatorLayout>
XML

cat > app/src/main/res/layout/activity_create_project.xml << 'XML'
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto" android:layout_width="match_parent"
    android:layout_height="match_parent" android:orientation="vertical">
    <androidx.appcompat.widget.Toolbar android:id="@+id/toolbar" android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize" android:background="@color/purple_500"/>
    <ScrollView android:layout_width="match_parent" android:layout_height="match_parent" android:padding="16dp">
        <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical">
            <com.google.android.material.textfield.TextInputLayout android:id="@+id/inputLayoutName"
                android:layout_width="match_parent" android:layout_height="wrap_content" android:hint="Nome do Projeto">
                <com.google.android.material.textfield.TextInputEditText android:id="@+id/editProjectName"
                    android:layout_width="match_parent" android:layout_height="wrap_content"/>
            </com.google.android.material.textfield.TextInputLayout>
            <com.google.android.material.textfield.TextInputLayout android:id="@+id/inputLayoutPackage"
                android:layout_width="match_parent" android:layout_height="wrap_content" android:hint="Package" android:layout_marginTop="16dp">
                <com.google.android.material.textfield.TextInputEditText android:id="@+id/editPackageName"
                    android:layout_width="match_parent" android:layout_height="wrap_content"/>
            </com.google.android.material.textfield.TextInputLayout>
            <Spinner android:id="@+id/spinnerMinSdk" android:layout_width="match_parent" android:layout_height="48dp" android:layout_marginTop="16dp"/>
            <Spinner android:id="@+id/spinnerTargetSdk" android:layout_width="match_parent" android:layout_height="48dp" android:layout_marginTop="8dp"/>
            <ProgressBar android:id="@+id/progressBar" android:layout_width="wrap_content" android:layout_height="wrap_content"
                android:layout_gravity="center" android:visibility="gone" android:layout_marginTop="16dp"/>
            <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:gravity="end" android:layout_marginTop="24dp">
                <Button android:id="@+id/buttonCancel" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="Cancelar"/>
                <Button android:id="@+id/buttonCreate" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="Criar" android:layout_marginStart="8dp"/>
            </LinearLayout>
        </LinearLayout>
    </ScrollView>
</LinearLayout>
XML

cat > app/src/main/res/layout/activity_editor.xml << 'XML'
<?xml version="1.0" encoding="utf-8"?>
<androidx.drawerlayout.widget.DrawerLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto" android:id="@+id/drawerLayout"
    android:layout_width="match_parent" android:layout_height="match_parent">
    <LinearLayout android:layout_width="match_parent" android:layout_height="match_parent" android:orientation="vertical">
        <androidx.appcompat.widget.Toolbar android:id="@+id/toolbar" android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize" android:background="@color/purple_500"/>
        <com.google.android.material.tabs.TabLayout android:id="@+id/tabLayout" android:layout_width="match_parent"
            android:layout_height="wrap_content" app:tabMode="scrollable"/>
        <io.github.rosemoe.sora.widget.CodeEditor android:id="@+id/codeEditor" android:layout_width="match_parent"
            android:layout_height="match_parent" android:background="@color/editor_bg"/>
    </LinearLayout>
    <LinearLayout android:layout_width="280dp" android:layout_height="match_parent" android:layout_gravity="start"
        android:background="@color/white" android:orientation="vertical">
        <TextView android:layout_width="match_parent" android:layout_height="wrap_content" android:text="Arquivos"
            android:padding="16dp" android:background="@color/purple_500" android:textColor="@color/white"/>
        <androidx.recyclerview.widget.RecyclerView android:id="@+id/fileTreeRecyclerView"
            android:layout_width="match_parent" android:layout_height="match_parent"/>
    </LinearLayout>
</androidx.drawerlayout.widget.DrawerLayout>
XML

cat > app/src/main/res/layout/activity_build.xml << 'XML'
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android" android:layout_width="match_parent"
    android:layout_height="match_parent" android:orientation="vertical">
    <androidx.appcompat.widget.Toolbar android:id="@+id/toolbar" android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize" android:background="@color/purple_500"/>
    <TextView android:id="@+id/textStatus" android:layout_width="match_parent" android:layout_height="wrap_content"
        android:padding="16dp" android:textStyle="bold"/>
    <ScrollView android:layout_width="match_parent" android:layout_height="match_parent">
        <TextView android:id="@+id/textLogs" android:layout_width="match_parent" android:layout_height="wrap_content"
            android:padding="16dp" android:fontFamily="monospace"/>
    </ScrollView>
</LinearLayout>
XML

cat > app/src/main/res/layout/activity_settings.xml << 'XML'
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android" android:layout_width="match_parent"
    android:layout_height="match_parent" android:orientation="vertical">
    <androidx.appcompat.widget.Toolbar android:id="@+id/toolbar" android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize" android:background="@color/purple_500"/>
    <TextView android:layout_width="match_parent" android:layout_height="wrap_content" android:padding="16dp"
        android:text="Configurações em breve..."/>
</LinearLayout>
XML

cat > app/src/main/res/layout/item_project.xml << 'XML'
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto" android:layout_width="match_parent"
    android:layout_height="wrap_content" android:layout_margin="8dp" app:cardCornerRadius="8dp">
    <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content"
        android:orientation="vertical" android:padding="16dp">
        <TextView android:id="@+id/textProjectName" android:layout_width="wrap_content"
            android:layout_height="wrap_content" android:textSize="18sp" android:textStyle="bold"/>
        <TextView android:id="@+id/textPackageName" android:layout_width="wrap_content"
            android:layout_height="wrap_content" android:layout_marginTop="4dp"/>
        <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:layout_marginTop="8dp">
            <TextView android:id="@+id/textSdkVersion" android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <TextView android:id="@+id/textLastModified" android:layout_width="wrap_content" android:layout_height="wrap_content"/>
        </LinearLayout>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
XML

cat > app/src/main/res/layout/item_file.xml << 'XML'
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android" android:layout_width="match_parent"
    android:layout_height="wrap_content" android:orientation="horizontal" android:gravity="center_vertical"
    android:padding="8dp" android:background="?selectableItemBackground">
    <ImageView android:id="@+id/imageFileIcon" android:layout_width="24dp" android:layout_height="24dp"/>
    <TextView android:id="@+id/textFileName" android:layout_width="wrap_content" android:layout_height="wrap_content"
        android:layout_marginStart="12dp"/>
</LinearLayout>
XML

echo "✅ Project structure created successfully!"
echo ""
echo "📁 Files created:"
find app/src -name "*.kt" -o -name "*.xml" | head -30
echo "..."
