package com.androidide.ui.project

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.androidide.App
import com.androidide.databinding.ActivityCloneRepoBinding
import com.androidide.project.Project
import com.androidide.utils.GitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.regex.Pattern

class CloneRepoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCloneRepoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCloneRepoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Clonar do GitHub"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnClone.setOnClickListener {
            val url = binding.editUrl.text.toString().trim()
            val name = binding.editName.text.toString().trim()

            if (url.isEmpty()) {
                binding.inputUrl.error = "URL obrigatória"
                return@setOnClickListener
            }
            if (name.isEmpty()) {
                binding.inputName.error = "Nome obrigatório"
                return@setOnClickListener
            }

            startCloneProcess(url, name)
        }
    }

    private fun startCloneProcess(url: String, name: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnClone.isEnabled = false
        binding.textStatus.text = "Clonando..."
        binding.textStatus.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val destDir = File(App.projectsDir, name)
                
                if (destDir.exists()) {
                    withContext(Dispatchers.Main) {
                        binding.textStatus.text = "Erro: Pasta já existe."
                        binding.progressBar.visibility = View.GONE
                        binding.btnClone.isEnabled = true
                    }
                    return@launch
                }

                val success = GitManager.cloneRepository(url, destDir)

                if (success) {
                    // Tenta encontrar o nome do pacote real
                    val packageName = findPackageName(destDir) ?: "com.imported.project"
                    
                    val newProject = Project(
                        name = name,
                        packageName = packageName,
                        path = destDir.absolutePath,
                        minSdk = 26,
                        targetSdk = 34
                    )
                    newProject.save()
                }

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnClone.isEnabled = true
                    
                    if (success) {
                        Toast.makeText(this@CloneRepoActivity, "Sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        binding.textStatus.text = "Falha no Clone."
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnClone.isEnabled = true
                    binding.textStatus.text = "Erro: ${e.message}"
                }
            }
        }
    }

    private fun findPackageName(projectDir: File): String? {
        // 1. Tenta o caminho padrão do Android Studio
        val standardManifest = File(projectDir, "app/src/main/AndroidManifest.xml")
        val pkg = parseManifest(standardManifest)
        if (pkg != null) return pkg

        // 2. Tenta na raiz
        val rootManifest = File(projectDir, "AndroidManifest.xml")
        val rootPkg = parseManifest(rootManifest)
        if (rootPkg != null) return rootPkg

        // 3. Busca recursiva (último recurso)
        return projectDir.walkTopDown()
            .maxDepth(5)
            .filter { it.name == "AndroidManifest.xml" }
            .mapNotNull { parseManifest(it) }
            .firstOrNull()
    }

    private fun parseManifest(file: File): String? {
        if (!file.exists()) return null
        return try {
            val content = file.readText()
            // Regex flexível para capturar package="xyz" ou package = "xyz"
            val pattern = Pattern.compile("package\\s*=\\s*\"([^\"]+)\"")
            val matcher = pattern.matcher(content)
            if (matcher.find()) {
                matcher.group(1)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
