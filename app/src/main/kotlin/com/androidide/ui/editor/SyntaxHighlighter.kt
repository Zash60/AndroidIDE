package com.androidide.ui.editor

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import java.util.regex.Pattern

object SyntaxHighlighter {

    // --- DRACULA THEME COLORS ---
    private val COLOR_BACKGROUND = Color.parseColor("#282A36")
    private val COLOR_FOREGROUND = Color.parseColor("#F8F8F2")
    private val COLOR_COMMENT = Color.parseColor("#6272A4")
    private val COLOR_CYAN = Color.parseColor("#8BE9FD") // Tipos, argumentos
    private val COLOR_GREEN = Color.parseColor("#50FA7B") // Strings, Annotations
    private val COLOR_ORANGE = Color.parseColor("#FFB86C") // Parameters
    private val COLOR_PINK = Color.parseColor("#FF79C6") // Keywords
    private val COLOR_PURPLE = Color.parseColor("#BD93F9") // Numbers, Constants
    private val COLOR_YELLOW = Color.parseColor("#F1FA8C") // Strings alternativa

    // Kotlin keywords
    private val KOTLIN_KEYWORDS = listOf(
        "fun", "val", "var", "class", "object", "interface", "enum",
        "if", "else", "when", "for", "while", "do", "return", "break",
        "continue", "throw", "try", "catch", "finally", "import", "package",
        "private", "public", "protected", "internal", "open", "final",
        "override", "abstract", "sealed", "data", "companion", "init",
        "constructor", "this", "super", "null", "true", "false", "is", "as",
        "in", "out", "suspend", "inline", "lateinit"
    )

    // Java keywords
    private val JAVA_KEYWORDS = listOf(
        "public", "private", "protected", "static", "final", "abstract",
        "class", "interface", "enum", "extends", "implements", "new",
        "void", "int", "long", "double", "float", "boolean", "char",
        "if", "else", "for", "while", "switch", "case", "return",
        "try", "catch", "finally", "import", "package", "this", "super",
        "null", "true", "false", "synchronized"
    )

    fun highlightKotlin(code: String): SpannableString {
        val spannable = SpannableString(code)

        // 1. Strings (Aspas) -> Amarelo/Verde
        highlightPattern(spannable, code, "\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\"", COLOR_YELLOW)

        // 2. Comentários -> Cinza (importante vir depois de string para não pintar urls dentro de strings)
        highlightPattern(spannable, code, "//.*", COLOR_COMMENT)
        highlightPattern(spannable, code, "/\\*[\\s\\S]*?\\*/", COLOR_COMMENT)

        // 3. Keywords -> Rosa
        highlightKeywords(spannable, code, KOTLIN_KEYWORDS, COLOR_PINK)

        // 4. Numbers -> Roxo
        highlightPattern(spannable, code, "\\b\\d+(\\.\\d+)?([fFdDlL])?\\b", COLOR_PURPLE)

        // 5. Annotations (@Something) -> Verde
        highlightPattern(spannable, code, "@\\w+", COLOR_GREEN)

        // 6. Funções (palavra antes de parenteses) -> Ciano
        highlightPattern(spannable, code, "\\b\\w+(?=\\()", COLOR_CYAN)

        return spannable
    }

    fun highlightJava(code: String): SpannableString {
        val spannable = SpannableString(code)
        
        highlightPattern(spannable, code, "\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\"", COLOR_YELLOW)
        highlightPattern(spannable, code, "//.*", COLOR_COMMENT)
        highlightPattern(spannable, code, "/\\*[\\s\\S]*?\\*/", COLOR_COMMENT)
        highlightKeywords(spannable, code, JAVA_KEYWORDS, COLOR_PINK)
        highlightPattern(spannable, code, "\\b\\d+(\\.\\d+)?([fFdDlL])?\\b", COLOR_PURPLE)
        highlightPattern(spannable, code, "@\\w+", COLOR_GREEN)
        
        return spannable
    }

    fun highlightXml(code: String): SpannableString {
        val spannable = SpannableString(code)
        
        // Tags <...>
        highlightPattern(spannable, code, "</?[\\w:.-]+", COLOR_PINK)
        highlightPattern(spannable, code, ">", COLOR_PINK)
        
        // Attributes name="value"
        // Nome do atributo -> Ciano
        highlightPattern(spannable, code, "\\s[\\w:.-]+=", COLOR_CYAN)
        
        // Valor do atributo -> Amarelo
        highlightPattern(spannable, code, "\"[^\"]*\"", COLOR_YELLOW)
        
        // Comentários
        highlightPattern(spannable, code, "<!--[\\s\\S]*?-->", COLOR_COMMENT)
        
        return spannable
    }

    private fun highlightKeywords(
        spannable: SpannableString,
        code: String,
        keywords: List<String>,
        color: Int
    ) {
        // Otimização: Criar um regex único para todas as keywords
        val patternString = "\\b(" + keywords.joinToString("|") + ")\\b"
        highlightPattern(spannable, code, patternString, color)
    }

    private fun highlightPattern(
        spannable: SpannableString,
        code: String,
        patternString: String,
        color: Int
    ) {
        try {
            val pattern = Pattern.compile(patternString)
            val matcher = pattern.matcher(code)
            while (matcher.find()) {
                spannable.setSpan(
                    ForegroundColorSpan(color),
                    matcher.start(), matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        } catch (e: Exception) {
            // Ignorar erros de regex complexos
        }
    }
}
