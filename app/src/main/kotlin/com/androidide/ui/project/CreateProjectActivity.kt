package com.androidide.ui.project

import android.os.Bundle
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
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Novo Projeto"
        }

        setupSpinners()
        setupListeners()
    }

    private fun setupSpinners() {
        val sdkVersions = (21..34).map { "API $it" }.toTypedArray()
        
        val minSdkAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sdkVersions)
        binding.spinnerMinSdk.adapter = minSdkAdapter
        binding.spinnerMinSdk.setSelection(3) // API 24

        val targetSdkAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sdkVersions)
        binding.spinnerTargetSdk.adapter = targetSdkAdapter
        binding.spinnerTargetSdk.setSelection(13) // API 34
    }

    private fun setupListeners() {
        // Auto-gerar package name
        binding.editProjectName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && binding.editPackageName.text.isNullOrEmpty()) {
                val projectName = binding.editProjectName.text.toString()
                    .lowercase()
                    .replace(" ", "")
                    .replace(Regex("[^a-z0-9]"), "")
                binding.editPackageName.setText("com.example.$projectName")
            }
        }

        binding.buttonCreate.setOnClickListener {
            createProject()
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }
    }

    private fun createProject() {
        val name = binding.editProjectName.text.toString().trim()
        val packageName = binding.editPackageName.text.toString().trim()
        val minSdk = 21 + binding.spinnerMinSdk.selectedItemPosition
        val targetSdk = 21 + binding.spinnerTargetSdk.selectedItemPosition

        // Validações
        if (name.isEmpty()) {
            binding.inputLayoutName.error = "Nome é obrigatório"
            return
        }

        if (packageName.isEmpty()) {
            binding.inputLayoutPackage.error = "Package é obrigatório"
            return
        }

        if (!packageName.matches(Regex("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$"))) {
            binding.inputLayoutPackage.error = "Package inválido"
            return
        }

        if (minSdk > targetSdk) {
            Toast.makeText(this, "Min SDK não pode ser maior que Target SDK", Toast.LENGTH_SHORT).show()
            return
        }

        // Criar projeto
        binding.buttonCreate.isEnabled = false
        binding.progressBar.visibility = android.view.View.VISIBLE

        try {
            ProjectManager.createProject(name, packageName, minSdk, targetSdk)
            Toast.makeText(this, "Projeto criado com sucesso!", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao criar projeto: ${e.message}", Toast.LENGTH_LONG).show()
            binding.buttonCreate.isEnabled = true
            binding.progressBar.visibility = android.view.View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
