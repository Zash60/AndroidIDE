package com.androidide.ui.editor

import android.os.Bundle
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager
// Correção do pacote do StyleReceiver (geralmente em analysis ou styling dependendo da versão exata,
// mas para 0.23.2, ele é usado pelo AnalyzeManager, vamos assumir o pacote correto via import wildcard ou específico se falhar)
import io.github.rosemoe.sora.lang.analysis.StyleReceiver
import io.github.rosemoe.sora.lang.completion.CompletionPublisher
import io.github.rosemoe.sora.lang.completion.SimpleCompletionItem
import io.github.rosemoe.sora.lang.format.Formatter
import io.github.rosemoe.sora.lang.format.FormatResult
import io.github.rosemoe.sora.lang.format.FormatOption
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
            // Implementação obrigatória: define o receptor de estilos (syntax highlighting)
            override fun setReceiver(receiver: StyleReceiver?) {
                // Como é uma linguagem básica sem highlight, ignoramos
            }

            // Implementação obrigatória: realiza a análise do código
            override fun analyze(content: ContentReference) {
                // Sem linting/análise
            }

            // Implementação obrigatória: reseta o estado da análise
            override fun reset(content: ContentReference, extraArguments: Bundle) {
                // Sem estado para resetar
            }
        }
    }

    override fun getInterruptionLevel(): Int = 0

    override fun getIndentAdvance(content: ContentReference, line: Int, column: Int): Int {
        return 0
    }

    override fun useTab(): Boolean = true

    // Implementação obrigatória: Fornece um formatador de código
    override fun getFormatter(): Formatter {
        return object : Formatter {
            override fun format(
                text: ContentReference,
                cursorRange: CharPosition,
                options: FormatOption,
                extraArguments: Bundle
            ): FormatResult? {
                // Retorna null indicando que não há formatação disponível
                return null
            }
        }
    }

    override fun requireAutoComplete(
        content: ContentReference,
        position: CharPosition,
        publisher: CompletionPublisher,
        extraArguments: Bundle
    ) {
        keywords.forEach { kw ->
            publisher.addItem(SimpleCompletionItem(kw, "Keyword", kw.length, "Basic"))
        }
    }
}
