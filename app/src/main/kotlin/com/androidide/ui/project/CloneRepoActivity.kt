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

            // Limpa erros anteriores
            binding.inputUrl.error = null
            binding.inputName.error = null

            startCloneProcess(url, name)
        }
    }

    private fun startCloneProcess(url: String, name: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnClone.isEnabled = false
        binding.textStatus.text = "Clonando repositório... Isso pode demorar."
        binding.textStatus.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Define o diretório de destino na pasta de projetos do App
                val destDir = File(App.projectsDir, name)
                
                if (destDir.exists()) {
                    withContext(Dispatchers.Main) {
                        binding.inputName.error = "Já existe um projeto com este nome"
                        binding.progressBar.visibility = View.GONE
                        binding.btnClone.isEnabled = true
                    }
                    return@launch
                }

                // Usa o GitManager existente para clonar
                val success = GitManager.cloneRepository(url, destDir)

                if (success) {
                    // --- CORREÇÃO: CRIAR O METADATA DO PROJETO (project.json) ---
                    // Tenta adivinhar o package name lendo o AndroidManifest, se existir
                    val packageName = findPackageName(destDir) ?: "com.imported.project"
                    
                    val newProject = Project(
                        name = name,
                        packageName = packageName,
                        path = destDir.absolutePath,
                        minSdk = 21,  // Padrão seguro
                        targetSdk = 34
                    )
                    
                    // Salva o arquivo project.json para que o ProjectManager reconheça a pasta
                    newProject.save()
                    // -----------------------------------------------------------
                }

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnClone.isEnabled = true
                    
                    if (success) {
                        Toast.makeText(this@CloneRepoActivity, "Clonagem concluída!", Toast.LENGTH_LONG).show()
                        finish() // Volta para a tela anterior
                    } else {
                        binding.textStatus.text = "Falha ao clonar. Verifique a URL ou sua conexão."
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

    // Função auxiliar para tentar ler o package do AndroidManifest.xml clonado
    private fun findPackageName(projectDir: File): String? {
        try {
            // Tenta achar em app/src/main/AndroidManifest.xml (estrutura padrão)
            var manifest = File(projectDir, "app/src/main/AndroidManifest.xml")
            if (!manifest.exists()) {
                // Tenta na raiz
                manifest = File(projectDir, "AndroidManifest.xml")
            }

            if (manifest.exists()) {
                val content = manifest.readText()
                val pattern = Pattern.compile("package=\"([^\"]+)\"")
                val matcher = pattern.matcher(content)
                if (matcher.find()) {
                    return matcher.group(1)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
