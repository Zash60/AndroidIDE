package com.androidide.ui.project

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.androidide.App
import com.androidide.databinding.ActivityCloneRepoBinding // Certifique-se que o XML tem esse nome
import com.androidide.utils.GitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
