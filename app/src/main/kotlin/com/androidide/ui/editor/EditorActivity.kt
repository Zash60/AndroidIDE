package com.androidide.ui.editor

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
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
import com.androidide.utils.PreferenceManager
import com.google.android.material.tabs.TabLayout
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

        // Recuperar Projeto (Compatibilidade Android 13+)
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
    }

    override fun onResume() {
        super.onResume()
        // Aplica as configurações sempre que a Activity for retomada (ex: voltar da tela de Settings)
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
        // Configurações iniciais padrão
        binding.codeEditor.apply {
            setTextSize(14f)
            setTabWidth(4)
        }
    }

    private fun applySettings() {
        // Carrega configurações do PreferenceManager
        val fontSize = PreferenceManager.getFontSize(this)
        val showLines = PreferenceManager.isLineNumbersEnabled(this)
        val wordWrap = PreferenceManager.isWordWrapEnabled(this)

        binding.codeEditor.textSize = fontSize
        binding.codeEditor.isLineNumberEnabled = showLines
        binding.codeEditor.isWordwrap = wordWrap
    }

    private fun setupFileTree() {
        fileAdapter = FileAdapter { file ->
            openFile(file)
            // Fecha a gaveta após selecionar um arquivo (opcional, remova se preferir manter aberta)
            // binding.drawerLayout.closeDrawer(GravityCompat.START) 
        }
        binding.fileTreeRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.fileTreeRecyclerView.adapter = fileAdapter
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { pos ->
                    if (pos >= 0 && pos < openFiles.size) {
                        switchToFile(openFiles[pos])
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadProjectFiles() {
        lifecycleScope.launch {
            // Lista arquivos em background
            val files = withContext(Dispatchers.IO) {
                // Pega todos os arquivos recursivamente
                project.srcDir.walkTopDown().toList()
            }
            // Atualiza UI na thread principal
            fileAdapter.setFiles(files, project.srcDir)
        }
    }

    private fun openFile(file: File) {
        if (file.isDirectory) return
        
        // Verifica se já está aberto
        val existing = openFiles.find { it.path == file.absolutePath }
        if (existing != null) {
            switchToFile(existing)
            return
        }

        // Carrega e abre novo arquivo
        lifecycleScope.launch {
            val sourceFile = withContext(Dispatchers.IO) { SourceFile.fromFile(file) }
            openFiles.add(sourceFile)
            
            // Adiciona a aba
            val tab = binding.tabLayout.newTab().setText(sourceFile.name)
            binding.tabLayout.addTab(tab)
            
            // Seleciona a aba recém criada (isso aciona o listener que chama switchToFile)
            tab.select()
        }
    }

    private fun switchToFile(file: SourceFile) {
        // Salva o estado do arquivo anterior na memória antes de trocar
        currentFile?.content = binding.codeEditor.text.toString()
        
        currentFile = file
        binding.codeEditor.setText(file.content)
        
        // Sincroniza a aba selecionada se a troca não veio do clique na aba
        val index = openFiles.indexOf(file)
        if (index >= 0 && binding.tabLayout.selectedTabPosition != index) {
            binding.tabLayout.getTabAt(index)?.select()
        }
        
        // Aqui você poderia adicionar lógica para mudar a linguagem do editor (Syntax Highlight)
        // baseada na extensão do arquivo (file.type)
    }

    private fun saveCurrentFile() {
        val fileToSave = currentFile
        if (fileToSave != null) {
            // Atualiza conteúdo na memória
            fileToSave.content = binding.codeEditor.text.toString()
            
            lifecycleScope.launch(Dispatchers.IO) {
                val success = fileToSave.save()
                withContext(Dispatchers.Main) {
                    val msg = if (success) "Salvo: ${fileToSave.name}" else "Erro ao salvar"
                    Toast.makeText(this@EditorActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Nenhum arquivo aberto", Toast.LENGTH_SHORT).show()
        }
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
            R.id.action_build -> { 
                // Inicia a Activity de Build passando o projeto
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Opcional: Perguntar se deseja salvar antes de sair se houver alterações
            super.onBackPressed()
        }
    }
}
