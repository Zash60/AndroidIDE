package com.androidide.ui.editor

import android.os.Bundle
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager
import io.github.rosemoe.sora.lang.completion.CompletionPublisher
import io.github.rosemoe.sora.lang.completion.SimpleCompletionItem
import io.github.rosemoe.sora.lang.styling.StyleReceiver
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
        return object : AnalyzeManager {
            // Método obrigatório da interface AnalyzeManager
            override fun setReceiver(receiver: StyleReceiver?) {
                // Para BasicLanguage, não fazemos highlight, então ignoramos
            }
            
            // Em versões mais recentes do Sora-editor, a análise pode ser feita via return
            // ou outros métodos, mas como é uma BasicLanguage vazia, este objeto basta
            // para satisfazer a interface se não houver outros métodos abstratos.
        }
    }

    override fun getInterruptionLevel(): Int = 0

    // Método obrigatório: define o avanço da indentação (0 = sem indentação automática inteligente)
    override fun getIndentAdvance(content: ContentReference, line: Int, column: Int): Int {
        return 0
    }

    // Método obrigatório: define se usa TAB ou espaços
    override fun useTab(): Boolean = true

    override fun requireAutoComplete(
        content: ContentReference,
        position: CharPosition,
        publisher: CompletionPublisher,
        extraArguments: Bundle
    ) {
        keywords.forEach { kw ->
            // Label, Descrição, Score, Tipo
            publisher.addItem(SimpleCompletionItem(kw, "Keyword", kw.length, "Basic"))
        }
    }
}
