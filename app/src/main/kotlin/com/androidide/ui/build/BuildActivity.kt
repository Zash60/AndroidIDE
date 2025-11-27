package com.androidide.ui.build

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.androidide.databinding.ActivityBuildBinding
import com.androidide.compiler.ProjectCompiler
import com.androidide.model.BuildStep
import com.androidide.project.Project
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BuildActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuildBinding
    private lateinit var project: Project
    private var isBuilding = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuildBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperar Projeto
        project = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("project", Project::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("project")!!
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Build: ${project.name}"

        startBuild()
    }

    private fun startBuild() {
        if (isBuilding) return
        isBuilding = true
        
        binding.textLogs.text = ""
        appendLog("🚀 Iniciando build para ${project.name}...")
        
        val compiler = ProjectCompiler(project) { step, message ->
            runOnUiThread {
                updateStatus(step, message)
            }
        }

        lifecycleScope.launch {
            try {
                val result = compiler.compile()
                if (result.success) {
                    appendLog("\n✅ Build Concluído com Sucesso!")
                    appendLog("📂 APK salvo em: ${result.apkPath}")
                    binding.textStatus.text = "Sucesso!"
                    binding.textStatus.setTextColor(Color.GREEN)
                } else {
                    appendLog("\n❌ Build Falhou!")
                    result.errors.forEach { error ->
                        appendLog("Error: ${error.message} (${error.file}:${error.line})")
                    }
                    binding.textStatus.text = "Falha"
                    binding.textStatus.setTextColor(Color.RED)
                }
            } catch (e: Exception) {
                appendLog("\n❌ Erro Fatal: ${e.message}")
                e.printStackTrace()
            } finally {
                isBuilding = false
            }
        }
    }

    private fun updateStatus(step: BuildStep, message: String) {
        binding.textStatus.text = message
        appendLog("[${step.name}] $message")
    }

    private fun appendLog(text: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val newText = "$timestamp $text\n"
        binding.textLogs.append(newText)
        
        // Auto-scroll para o final
        binding.scrollView.post {
            binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
