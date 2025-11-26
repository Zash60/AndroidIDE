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
