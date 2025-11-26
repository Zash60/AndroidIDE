package com.androidide.ui.build

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.androidide.databinding.ActivityBuildBinding
import com.androidide.project.Project

class BuildActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuildBinding
    private lateinit var project: Project

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuildBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperação do objeto Project com compatibilidade para Android 13 (Tiramisu)
        project = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("project", Project::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("project")!!
        }
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Build: ${project.name}"

        setupView()
    }

    private fun setupView() {
        binding.textStatus.text = "Build não implementado ainda"
        binding.textLogs.text = """
            Android IDE - Build System
            
            Este é um placeholder para o sistema de build.
            
            Para compilar apps Android no dispositivo, seria necessário:
            1. AAPT2 para compilar recursos
            2. Compilador Kotlin/Java
            3. D8/R8 para criar DEX
            4. Ferramenta de empacotamento APK
            5. Assinatura de APK
            
            Projeto: ${project.name}
            Package: ${project.packageName}
            Min SDK: ${project.minSdk}
            Target SDK: ${project.targetSdk}
        """.trimIndent()
    }

    override fun onSupportNavigateUp(): Boolean {
        // Substitui o onBackPressed() depreciado
        finish()
        return true
    }
}
