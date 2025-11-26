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
