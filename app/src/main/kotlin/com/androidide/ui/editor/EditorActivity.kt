package com.androidide.ui.editor

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
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
import com.androidide.utils.GitManager
import com.androidide.utils.PreferenceManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import io.github.rosemoe.sora.langs.java.JavaLanguage
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

        // Recuperar Projeto
        project = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("project", Project::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("project")!!
        }

        setupToolbar()
        setupDrawer()
        setupEditor()
        setupFileTree()
        setupTabs()
        loadProjectFiles()

        // Botão flutuante principal (cria na raiz src/main)
        binding.fabAddFile.setOnClickListener {
            showCreateFileDialog(project.srcDir)
        }
    }

    override fun onResume() {
        super.onResume()
        applySettings()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = project.name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
            // APLICA O TEMA DRACULA
            colorScheme = DraculaColorScheme()
        }
    }

    private fun applySettings() {
        val fontSize = PreferenceManager.getFontSize(this)
        val showLines = PreferenceManager.isLineNumbersEnabled(this)
        val wordWrap = PreferenceManager.isWordWrapEnabled(this)

        binding.codeEditor.setTextSize(fontSize)
        binding.codeEditor.isLineNumberEnabled = showLines
        binding.codeEditor.isWordwrap = wordWrap
    }

    private fun setupFileTree() {
        // CORREÇÃO: Passando 'this' (Context) e o callback 'onFileAction'
        fileAdapter = FileAdapter(
            context = this,
            onFileClick = { file -> openFile(file) },
            onFileAction = { action, file -> handleFileAction(action, file) }
        )
        binding.fileTreeRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.fileTreeRecyclerView.adapter = fileAdapter
    }

    private fun handleFileAction(action: FileAdapter.Action, file: File) {
        when (action) {
            FileAdapter.Action.NEW_FILE -> {
                // Abre o diálogo apontando para a pasta selecionada
                showCreateFileDialog(if (file.isDirectory) file else file.parentFile)
            }
            FileAdapter.Action.DELETE -> {
                AlertDialog.Builder(this)
                    .setTitle("Excluir")
                    .setMessage("Excluir ${file.name}?")
                    .setPositiveButton("Sim") { _, _ ->
                        file.deleteRecursively()
                        loadProjectFiles()
                    }
                    .setNegativeButton("Não", null)
                    .show()
            }
            FileAdapter.Action.RENAME -> {
                val input = android.widget.EditText(this)
                input.setText(file.name)
                AlertDialog.Builder(this)
                    .setTitle("Renomear")
                    .setView(input)
                    .setPositiveButton("OK") { _, _ ->
                        val newName = input.text.toString()
                        if (newName.isNotEmpty()) {
                            file.renameTo(File(file.parentFile, newName))
                            loadProjectFiles()
                        }
                    }
                    .show()
            }
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val pos = tab?.position ?: return
                if (pos >= 0 && pos < openFiles.size) {
                    switchToFile(openFiles[pos])
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadProjectFiles() {
        // CORREÇÃO: Usando loadDirectory em vez de setFiles
        lifecycleScope.launch(Dispatchers.Main) {
            fileAdapter.loadDirectory(project.srcDir)
        }
    }

    private fun openFile(file: File) {
        if (file.isDirectory) return

        val existingIndex = openFiles.indexOfFirst { it.path == file.absolutePath }
        if (existingIndex >= 0) {
            binding.tabLayout.getTabAt(existingIndex)?.select()
            return
        }

        lifecycleScope.launch {
            val sourceFile = withContext(Dispatchers.IO) { SourceFile.fromFile(file) }
            openFiles.add(sourceFile)
            addTab(sourceFile)
        }
    }

    private fun addTab(sourceFile: SourceFile) {
        val tab = binding.tabLayout.newTab()
        val tabView = LayoutInflater.from(this).inflate(R.layout.item_tab_custom, null)
        
        val title = tabView.findViewById<TextView>(R.id.tabTitle)
        val btnClose = tabView.findViewById<ImageButton>(R.id.btnCloseTab)
        
        title.text = sourceFile.name
        btnClose.setOnClickListener {
            closeFile(sourceFile)
        }
        
        tab.customView = tabView
        binding.tabLayout.addTab(tab)
        tab.select()
    }

    private fun closeFile(file: SourceFile) {
        val index = openFiles.indexOf(file)
        if (index >= 0) {
            openFiles.removeAt(index)
            binding.tabLayout.removeTabAt(index)
            
            if (openFiles.isEmpty()) {
                currentFile = null
                binding.codeEditor.setText("")
            }
        }
    }

    private fun switchToFile(file: SourceFile) {
        currentFile?.content = binding.codeEditor.text.toString()
        currentFile = file
        binding.codeEditor.setText(file.content)
        
        if (file.path.endsWith(".java")) {
            binding.codeEditor.setEditorLanguage(JavaLanguage())
        } else {
            // Usa sua classe BasicLanguage (ou SyntaxHighlighter se implementado como wrapper)
            binding.codeEditor.setEditorLanguage(BasicLanguage()) 
        }

        val index = openFiles.indexOf(file)
        if (index >= 0 && binding.tabLayout.selectedTabPosition != index) {
            binding.tabLayout.getTabAt(index)?.select()
        }
    }

    private fun showCreateFileDialog(parentFolder: File) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_file, null)
        val editName = dialogView.findViewById<TextInputEditText>(R.id.editFileName)
        val checkFolder = dialogView.findViewById<CheckBox>(R.id.checkIsFolder)

        AlertDialog.Builder(this)
            .setTitle("Novo em: ${parentFolder.name}")
            .setView(dialogView)
            .setPositiveButton("Criar") { _, _ ->
                val name = editName.text.toString()
                if (name.isNotEmpty()) {
                    createNewFile(parentFolder, name, checkFolder.isChecked)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun createNewFile(parentDir: File, name: String, isFolder: Boolean) {
        val newFile = File(parentDir, name)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                newFile.parentFile?.mkdirs()
                
                if (isFolder) {
                    newFile.mkdirs()
                } else {
                    newFile.createNewFile()
                    // Templates básicos
                    if (newFile.length() == 0L) {
                        if (newFile.extension == "kt") {
                            newFile.writeText("package ${project.packageName}\n\nclass ${newFile.nameWithoutExtension} {\n}")
                        } else if (newFile.extension == "xml") {
                             newFile.writeText("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n    android:layout_width=\"match_parent\"\n    android:layout_height=\"match_parent\">\n</LinearLayout>")
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    loadProjectFiles()
                    if (!isFolder) openFile(newFile)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditorActivity, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveCurrentFile() {
        val fileToSave = currentFile
        if (fileToSave != null) {
            fileToSave.content = binding.codeEditor.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                fileToSave.save()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        menu.add(0, 101, 0, "Git Pull")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveCurrentFile()
                Toast.makeText(this, "Salvo", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_build -> { 
                saveCurrentFile()
                val intent = Intent(this, BuildActivity::class.java)
                intent.putExtra("project", project)
                startActivity(intent)
                true 
            }
            R.id.action_undo -> {
                if (binding.codeEditor.canUndo()) binding.codeEditor.undo()
                true
            }
            R.id.action_redo -> {
                if (binding.codeEditor.canRedo()) binding.codeEditor.redo()
                true
            }
            101 -> { 
                 lifecycleScope.launch(Dispatchers.IO) {
                    val success = GitManager.pull(project.projectDir)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditorActivity, if(success) "Pull OK" else "Erro Pull", Toast.LENGTH_SHORT).show()
                        loadProjectFiles()
                    }
                }
                true
            }
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
