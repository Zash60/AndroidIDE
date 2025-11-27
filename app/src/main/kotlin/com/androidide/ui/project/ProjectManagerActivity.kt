package com.androidide.ui.project

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
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

        binding.fabGitClone.setOnClickListener {
            startActivity(Intent(this, CloneRepoActivity::class.java))
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
        val options = arrayOf("Abrir", "Editar Configurações", "Excluir")
        
        AlertDialog.Builder(this)
            .setTitle(project.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openProject(project)
                    1 -> showEditProjectDialog(project) // Nova função
                    2 -> confirmDelete(project)
                }
            }
            .show()
    }

    private fun showEditProjectDialog(project: Project) {
        // Layout simples criado via código para o Dialog
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val inputName = EditText(this).apply { 
            hint = "Nome do Projeto"
            setText(project.name)
        }
        
        val inputPackage = EditText(this).apply { 
            hint = "Package Name (ex: com.app)"
            setText(project.packageName)
        }

        layout.addView(inputName)
        layout.addView(inputPackage)

        AlertDialog.Builder(this)
            .setTitle("Editar Projeto")
            .setView(layout)
            .setPositiveButton("Salvar") { _, _ ->
                val newName = inputName.text.toString()
                val newPackage = inputPackage.text.toString()

                if (newName.isNotEmpty() && newPackage.isNotEmpty()) {
                    // Atualiza o objeto do projeto (Kotlin Data Class copy não altera o original in-place, cria um novo)
                    // Mas como o Project tem var lastModified, precisamos salvar um novo json
                    
                    val updatedProject = project.copy(
                        name = newName,
                        packageName = newPackage
                    )
                    
                    // Salva no disco
                    updatedProject.save()
                    
                    Toast.makeText(this, "Salvo!", Toast.LENGTH_SHORT).show()
                    loadProjects()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmDelete(project: Project) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Projeto")
            .setMessage("Excluir ${project.name} e todos os arquivos?")
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
