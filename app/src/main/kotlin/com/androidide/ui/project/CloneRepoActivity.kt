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
                binding.inputName.error = "Nome do projeto obrigatório"
                return@setOnClickListener
            }

            binding.inputUrl.error = null
            binding.inputName.error = null

            startCloneProcess(url, name)
        }
    }

    private fun startCloneProcess(url: String, name: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnClone.isEnabled = false
        binding.textStatus.text = "Clonando repositório..."
        binding.textStatus.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val destDir = File(App.projectsDir, name)
                
                if (destDir.exists()) {
                    withContext(Dispatchers.Main) {
                        binding.inputName.error = "Projeto já existe"
                        binding.progressBar.visibility = View.GONE
                        binding.btnClone.isEnabled = true
                    }
                    return@launch
                }

                val success = GitManager.cloneRepository(url, destDir)

                if (success) {
                    // Busca recursiva pelo manifesto para pegar o pacote correto
                    val packageName = findPackageNameRecursively(destDir) ?: "com.imported.project"
                    
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
                        Toast.makeText(this@CloneRepoActivity, "Concluído!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        binding.textStatus.text = "Falha ao clonar."
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnClone.isEnabled = true
                    binding.textStatus.text = "Erro: ${e.message}"
                    e.printStackTrace()
                }
            }
        }
    }

    private fun findPackageNameRecursively(dir: File): String? {
        // Limite de profundidade para não demorar demais
        return dir.walkTopDown()
            .maxDepth(4) 
            .filter { it.name == "AndroidManifest.xml" }
            .mapNotNull { parseManifestPackage(it) }
            .firstOrNull()
    }

    private fun parseManifestPackage(manifestFile: File): String? {
        return try {
            val content = manifestFile.readText()
            // Regex simples para capturar package="com.exemplo"
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
