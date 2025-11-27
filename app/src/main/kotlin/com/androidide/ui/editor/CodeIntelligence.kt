package com.androidide.ui.editor

import io.github.rosemoe.sora.interfaces.AutoCompleteProvider
import io.github.rosemoe.sora.text.TextAnalyzeResult
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.EditorColorScheme
import java.util.regex.Pattern

class BasicAutoCompleteProvider : AutoCompleteProvider {

    private val keywords = listOf(
        "fun", "val", "var", "class", "object", "interface", "return",
        "if", "else", "when", "for", "while", "import", "package",
        "private", "public", "protected", "override", "super", "this",
        "Int", "String", "Boolean", "Float", "Double", "Long"
    )

    override fun getAutoCompleteItems(
        prefix: String,
        analyzeResult: TextAnalyzeResult?,
        line: Int,
        column: Int
    ): List<io.github.rosemoe.sora.data.CompletionItem> {
        val items = mutableListOf<io.github.rosemoe.sora.data.CompletionItem>()

        // Sugerir palavras-chave
        keywords.filter { it.startsWith(prefix, ignoreCase = true) }.forEach { kw ->
            items.add(asCompletionItem(kw, "Keyword"))
        }

        // Sugerir tokens encontrados no texto (variáveis locais, etc)
        // Isso é uma implementação simplificada. Uma real usaria AST.
        return items
    }

    private fun asCompletionItem(label: String, desc: String): io.github.rosemoe.sora.data.CompletionItem {
        return object : io.github.rosemoe.sora.data.CompletionItem(label, desc) {
            override fun select() {
                // Ação ao selecionar (padrão insere o texto)
            }
        }
    }
}
