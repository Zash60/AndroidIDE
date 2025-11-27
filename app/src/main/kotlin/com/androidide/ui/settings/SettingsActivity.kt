package com.androidide.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.androidide.databinding.ActivitySettingsBinding
import com.androidide.utils.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Configurações"

        setupUI()
    }

    private fun setupUI() {
        // Carregar valores atuais
        val currentSize = PreferenceManager.getFontSize(this)
        binding.seekFontSize.progress = currentSize.toInt()
        binding.textFontSizeLabel.text = "Tamanho da Fonte: ${currentSize.toInt()}sp"
        
        binding.switchLineNumbers.isChecked = PreferenceManager.isLineNumbersEnabled(this)
        binding.switchWordWrap.isChecked = PreferenceManager.isWordWrapEnabled(this)

        // Listeners
        binding.seekFontSize.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                // Limitar mínimo a 10
                val size = if (progress < 10) 10 else progress
                binding.textFontSizeLabel.text = "Tamanho da Fonte: ${size}sp"
                PreferenceManager.setFontSize(this@SettingsActivity, size.toFloat())
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        binding.switchLineNumbers.setOnCheckedChangeListener { _, isChecked ->
            PreferenceManager.setLineNumbersEnabled(this, isChecked)
        }
        
        binding.switchWordWrap.setOnCheckedChangeListener { _, isChecked ->
            PreferenceManager.setWordWrapEnabled(this, isChecked)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
