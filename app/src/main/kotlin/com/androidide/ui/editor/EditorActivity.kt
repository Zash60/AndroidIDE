package com.androidide.ui.editor

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
        supportActionBar?.apply {
            title = project.name
            setDisplayHomeAsUpEnabled(true)
        }
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
            // Configurar syntax highlighting
        }

        binding.codeEditor.setOnTextChangedListener { _, _ ->
            currentFile?.let {
                it.content = binding.codeEditor.text.toString()
                it.isModified = true
                updateTabTitle()
            }
        }
    }

    private fun setupFileTree() {
        fileAdapter = FileAdapter { file ->
            openFile(file)
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        binding.fileTreeRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@EditorActivity)
            adapter = fileAdapter
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : 
            com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                tab?.position?.let { position ->
                    if (position < openFiles.size) {
                        switchToFile(openFiles[position])
                    }
                }
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }

    private fun loadProjectFiles() {
        lifecycleScope.launch {
            val files = withContext(Dispatchers.IO) {
                getFilesRecursively(project.srcDir)
            }
            fileAdapter.setFiles(files)
        }
    }

    private fun getFilesRecursively(dir: File): List<File> {
        val files = mutableListOf<File>()
        dir.listFiles()?.sortedWith(compareBy({ !it.isDirectory }, { it.name }))?.forEach { file ->
            files.add(file)
            if (file.isDirectory) {
                files.addAll(getFilesRecursively(file))
            }
        }
        return files
    }

    private fun openFile(file: File) {
        if (file.isDirectory) return

        // Verificar se já está aberto
        val existingFile = openFiles.find { it.path == file.absolutePath }
        if (existingFile != null) {
            switchToFile(existingFile)
            return
        }

        // Abrir novo arquivo
        lifecycleScope.launch {
            val sourceFile = withContext(Dispatchers.IO) {
                SourceFile.fromFile(file)
            }

            openFiles.add(sourceFile)
            addTab(sourceFile)
            switchToFile(sourceFile)
        }
    }

    private fun addTab(file: SourceFile) {
        val tab = binding.tabLayout.newTab()
        tab.text = file.name
        binding.tabLayout.addTab(tab)
        tab.select()
    }

    private fun switchToFile(file: SourceFile) {
        currentFile = file
        binding.codeEditor.setText(file.content)
        
        // Configurar highlighting baseado no tipo
        setupSyntaxHighlighting(file.type)
        
        // Selecionar tab correspondente
        val index = openFiles.indexOf(file)
        if (index >= 0 && index < binding.tabLayout.tabCount) {
            binding.tabLayout.getTabAt(index)?.select()
        }
    }

    private fun setupSyntaxHighlighting(type: SourceFile.FileType) {
        // Configurar esquema de cores baseado no tipo de arquivo
        when (type) {
            SourceFile.FileType.KOTLIN -> {
                // Configurar highlighting Kotlin
            }
            SourceFile.FileType.JAVA -> {
                // Configurar highlighting Java
            }
            SourceFile.FileType.XML -> {
                // Configurar highlighting XML
            }
            else -> {
                // Texto simples
            }
        }
    }

    private fun updateTabTitle() {
        currentFile?.let { file ->
            val index = openFiles.indexOf(file)
            if (index >= 0) {
                val tab = binding.tabLayout.getTabAt(index)
                tab?.text = if (file.isModified) "${file.name}*" else file.name
            }
        }
    }

    private fun saveCurrentFile() {
        currentFile?.let { file ->
            file.content = binding.codeEditor.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                val success = file.save()
                withContext(Dispatchers.Main) {
                    if (success) {
                        Toast.makeText(this@EditorActivity, "Salvo!", Toast.LENGTH_SHORT).show()
                        updateTabTitle()
                    } else {
                        Toast.makeText(this@EditorActivity, "Erro ao salvar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun saveAllFiles() {
        lifecycleScope.launch(Dispatchers.IO) {
            openFiles.filter { it.isModified }.forEach { it.save() }
            withContext(Dispatchers.Main) {
                Toast.makeText(this@EditorActivity, "Todos os arquivos salvos!", Toast.LENGTH_SHORT).show()
                openFiles.forEachIndexed { index, _ ->
                    updateTabTitle()
                }
            }
        }
    }

    private fun closeCurrentTab() {
        currentFile?.let { file ->
            if (file.isModified) {
                AlertDialog.Builder(this)
                    .setTitle("Salvar alterações?")
                    .setMessage("O arquivo '${file.name}' foi modificado. Deseja salvar?")
                    .setPositiveButton("Salvar") { _, _ ->
                        file.save()
                        removeTab(file)
                    }
                    .setNegativeButton("Não salvar") { _, _ ->
                        removeTab(file)
                    }
                    .setNeutralButton("Cancelar", null)
                    .show()
            } else {
                removeTab(file)
            }
        }
    }

    private fun removeTab(file: SourceFile) {
        val index = openFiles.indexOf(file)
        if (index >= 0) {
            openFiles.removeAt(index)
            binding.tabLayout.removeTabAt(index)
            
            if (openFiles.isNotEmpty()) {
                val newIndex = (index - 1).coerceAtLeast(0)
                switchToFile(openFiles[newIndex])
            } else {
                currentFile = null
                binding.codeEditor.setText("")
            }
        }
    }

    private fun buildProject() {
        // Salvar todos os arquivos antes de compilar
        saveAllFiles()
        
        val intent = Intent(this, BuildActivity::class.java)
        intent.putExtra("project", project)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveCurrentFile()
                true
            }
            R.id.action_save_all -> {
                saveAllFiles()
                true
            }
            R.id.action_build -> {
                buildProject()
                true
            }
            R.id.action_close_tab -> {
                closeCurrentTab()
                true
            }
            R.id.action_undo -> {
                binding.codeEditor.undo()
                true
            }
            R.id.action_redo -> {
                binding.codeEditor.redo()
                true
            }
            R.id.action_find -> {
                showFindDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFindDialog() {
        val editText = android.widget.EditText(this)
        editText.hint = "Buscar..."

        AlertDialog.Builder(this)
            .setTitle("Buscar")
            .setView(editText)
            .setPositiveButton("Buscar") { _, _ ->
                val query = editText.text.toString()
                binding.codeEditor.searcher.search(query)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else if (openFiles.any { it.isModified }) {
            AlertDialog.Builder(this)
                .setTitle("Arquivos não salvos")
                .setMessage("Existem arquivos não salvos. Deseja salvar antes de sair?")
                .setPositiveButton("Salvar e sair") { _, _ ->
                    saveAllFiles()
                    super.onBackPressed()
                }
                .setNegativeButton("Sair sem salvar") { _, _ ->
                    super.onBackPressed()
                }
                .setNeutralButton("Cancelar", null)
                .show()
        } else {
            super.onBackPressed()
        }
    }
}
