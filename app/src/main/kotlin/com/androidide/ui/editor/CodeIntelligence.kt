package com.androidide.ui.editor

import android.os.Bundle
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager
import io.github.rosemoe.sora.lang.styling.StyleReceiver
import io.github.rosemoe.sora.lang.completion.CompletionPublisher
import io.github.rosemoe.sora.lang.completion.SimpleCompletionItem
import io.github.rosemoe.sora.lang.format.Formatter
import io.github.rosemoe.sora.lang.smartEnter.SymbolPairMatch
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.text.ContentReference
import io.github.rosemoe.sora.util.TextRange

class BasicLanguage : Language() {

    private val keywords = listOf(
        "fun", "val", "var", "class", "object", "interface", "return",
        "if", "else", "when", "for", "while", "import", "package",
        "private", "public", "protected", "override", "super", "this",
        "Int", "String", "Boolean", "Float", "Double", "Long"
    )

    override fun getAnalyzeManager(): AnalyzeManager {
        return object : AnalyzeManager {
            override fun setReceiver(receiver: StyleReceiver?) {
                // Não usado para linguagem básica
            }

            override fun insert(start: CharPosition, end: CharPosition, insertedText: CharSequence) {
                // Rastrear inserções se necessário
            }

            override fun delete(start: CharPosition, end: CharPosition, deletedText: CharSequence) {
                // Rastrear deleções se necessário
            }

            override fun rerun() {
                // Análise completa
            }
            
            // Em versões mais antigas/específicas pode ser necessário o reset
             fun reset(content: ContentReference, extraArguments: Bundle) {
            }
        }
    }

    override fun getInterruptionLevel(): Int = 0

    override fun getIndentAdvance(content: ContentReference, line: Int, column: Int): Int {
        return 0
    }

    override fun useTab(): Boolean = true

    override fun getFormatter(): Formatter {
        return object : Formatter {
            override fun format(text: Content, range: TextRange) {
                // Formatação básica (nenhuma)
            }
        }
    }

    override fun getSymbolPairs(): SymbolPairMatch {
        return SymbolPairMatch.DefaultSymbolPairs
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
