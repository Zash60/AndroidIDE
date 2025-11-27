package com.androidide.ui.editor

import android.os.Bundle
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager
import io.github.rosemoe.sora.lang.completion.CompletionPublisher
import io.github.rosemoe.sora.lang.completion.SimpleCompletionItem
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.ContentReference

class BasicLanguage : Language() {

    private val keywords = listOf(
        "fun", "val", "var", "class", "object", "interface", "return",
        "if", "else", "when", "for", "while", "import", "package",
        "private", "public", "protected", "override", "super", "this",
        "Int", "String", "Boolean", "Float", "Double", "Long"
    )

    override fun getAnalyzeManager(): AnalyzeManager {
        // Retorna um gerenciador de análise vazio para evitar erros
        return object : AnalyzeManager {
            override fun analyze(content: ContentReference) {
                // Análise estática (linting) seria implementada aqui
            }
        }
    }

    override fun getInterruptionLevel(): Int = 0

    override fun requireAutoComplete(
        content: ContentReference,
        position: CharPosition,
        publisher: CompletionPublisher,
        extraArguments: Bundle
    ) {
        val prefix = "" // Lógica simplificada: sugere tudo. Em produção, calcularia o prefixo atual.
        
        keywords.forEach { kw ->
            // SimpleCompletionItem(label, description, score, type)
            publisher.addItem(SimpleCompletionItem(kw, "Keyword", kw.length, "Basic"))
        }
    }
}
