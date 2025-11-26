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
