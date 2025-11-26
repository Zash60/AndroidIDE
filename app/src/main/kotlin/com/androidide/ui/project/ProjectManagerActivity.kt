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
        setupFab()
        loadProjects()
    }

    override fun onResume() {
        super.onResume()
        loadProjects()
    }

    private fun setupRecyclerView() {
        adapter = ProjectAdapter(
            projects = projects,
            onProjectClick = { project -> openProject(project) },
            onProjectLongClick = { project -> showProjectOptions(project) }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ProjectManagerActivity)
            adapter = this@ProjectManagerActivity.adapter
        }
    }

    private fun setupFab() {
        binding.fabNewProject.setOnClickListener {
            startActivity(Intent(this, CreateProjectActivity::class.java))
        }
    }

    private fun loadProjects() {
        projects.clear()
        projects.addAll(ProjectManager.getAllProjects())
        adapter.notifyDataSetChanged()

        binding.emptyView.visibility = if (projects.isEmpty()) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (projects.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun openProject(project: Project) {
        val intent = Intent(this, EditorActivity::class.java)
        intent.putExtra("project", project)
        startActivity(intent)
    }

    private fun showProjectOptions(project: Project) {
        val options = arrayOf("Abrir", "Renomear", "Duplicar", "Excluir")

        AlertDialog.Builder(this)
            .setTitle(project.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openProject(project)
                    1 -> showRenameDialog(project)
                    2 -> duplicateProject(project)
                    3 -> showDeleteConfirmation(project)
                }
            }
            .show()
    }

    private fun showRenameDialog(project: Project) {
        val editText = android.widget.EditText(this)
        editText.setText(project.name)
        editText.selectAll()

        AlertDialog.Builder(this)
            .setTitle("Renomear Projeto")
            .setView(editText)
            .setPositiveButton("Renomear") { _, _ ->
                // Implementar renomeação
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun duplicateProject(project: Project) {
        val editText = android.widget.EditText(this)
        editText.setText("${project.name}_copy")

        AlertDialog.Builder(this)
            .setTitle("Duplicar Projeto")
            .setView(editText)
            .setPositiveButton("Duplicar") { _, _ ->
                val newName = editText.text.toString()
                if (newName.isNotBlank()) {
                    ProjectManager.duplicateProject(project, newName)
                    loadProjects()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDeleteConfirmation(project: Project) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Projeto")
            .setMessage("Tem certeza que deseja excluir '${project.name}'? Esta ação não pode ser desfeita.")
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
