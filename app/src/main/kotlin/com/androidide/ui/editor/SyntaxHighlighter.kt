package com.androidide.ui.editor

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import java.util.regex.Pattern

object SyntaxHighlighter {

    // Cores
    private const val COLOR_KEYWORD = 0xFFCC7832.toInt()      // Laranja
    private const val COLOR_STRING = 0xFF6A8759.toInt()       // Verde
    private const val COLOR_NUMBER = 0xFF6897BB.toInt()       // Azul claro
    private const val COLOR_COMMENT = 0xFF808080.toInt()      // Cinza
    private const val COLOR_ANNOTATION = 0xFFBBB529.toInt()   // Amarelo
    private const val COLOR_FUNCTION = 0xFFFFC66D.toInt()     // Amarelo claro
    private const val COLOR_TYPE = 0xFFA9B7C6.toInt()         // Cinza claro

    // Kotlin keywords
    private val KOTLIN_KEYWORDS = listOf(
        "fun", "val", "var", "class", "object", "interface", "enum",
        "if", "else", "when", "for", "while", "do", "return", "break",
        "continue", "throw", "try", "catch", "finally", "import", "package",
        "private", "public", "protected", "internal", "open", "final",
        "override", "abstract", "sealed", "data", "companion", "init",
        "constructor", "this", "super", "null", "true", "false", "is", "as",
        "in", "out", "suspend", "inline", "crossinline", "noinline", "reified",
        "lateinit", "by", "lazy", "get", "set"
    )

    // Java keywords
    private val JAVA_KEYWORDS = listOf(
        "public", "private", "protected", "static", "final", "abstract",
        "class", "interface", "enum", "extends", "implements", "new",
        "void", "int", "long", "double", "float", "boolean", "char",
        "byte", "short", "if", "else", "for", "while", "do", "switch",
        "case", "default", "break", "continue", "return", "try", "catch",
        "finally", "throw", "throws", "import", "package", "this", "super",
        "null", "true", "false", "instanceof", "synchronized", "volatile",
        "transient", "native", "strictfp"
    )

    // XML patterns
    private val XML_TAG_PATTERN = Pattern.compile("</?[a-zA-Z][a-zA-Z0-9_:.-]*")
    private val XML_ATTRIBUTE_PATTERN = Pattern.compile("\\s[a-zA-Z][a-zA-Z0-9_:.-]*=")
    private val XML_STRING_PATTERN = Pattern.compile("\"[^\"]*\"")
    private val XML_COMMENT_PATTERN = Pattern.compile("<!--.*?-->", Pattern.DOTALL)

    fun highlightKotlin(code: String): SpannableString {
        val spannable = SpannableString(code)
        
        // Keywords
        highlightKeywords(spannable, code, KOTLIN_KEYWORDS, COLOR_KEYWORD)
        
        // Strings
        highlightPattern(spannable, code, "\"([^\"\\\\]|\\\\.)*\"", COLOR_STRING)
        
        // Numbers
        highlightPattern(spannable, code, "\\b\\d+(\\.\\d+)?[fFdDlL]?\\b", COLOR_NUMBER)
        
        // Single-line comments
        highlightPattern(spannable, code, "//.*$", COLOR_COMMENT)
        
        // Multi-line comments
        highlightPattern(spannable, code, "/\\*.*?\\*/", COLOR_COMMENT)
        
        // Annotations
        highlightPattern(spannable, code, "@[a-zA-Z][a-zA-Z0-9_]*", COLOR_ANNOTATION)
        
        // Functions
        highlightPattern(spannable, code, "\\bfun\\s+([a-zA-Z][a-zA-Z0-9_]*)", COLOR_FUNCTION)
        
        return spannable
    }

    fun highlightJava(code: String): SpannableString {
        val spannable = SpannableString(code)
        
        highlightKeywords(spannable, code, JAVA_KEYWORDS, COLOR_KEYWORD)
        highlightPattern(spannable, code, "\"([^\"\\\\]|\\\\.)*\"", COLOR_STRING)
        highlightPattern(spannable, code, "\\b\\d+(\\.\\d+)?[fFdDlL]?\\b", COLOR_NUMBER)
        highlightPattern(spannable, code, "//.*$", COLOR_COMMENT)
        highlightPattern(spannable, code, "/\\*.*?\\*/", COLOR_COMMENT)
        highlightPattern(spannable, code, "@[a-zA-Z][a-zA-Z0-9_]*", COLOR_ANNOTATION)
        
        return spannable
    }

    fun highlightXml(code: String): SpannableString {
        val spannable = SpannableString(code)
        
        // Tags
        var matcher = XML_TAG_PATTERN.matcher(code)
        while (matcher.find()) {
            spannable.setSpan(
                ForegroundColorSpan(COLOR_KEYWORD),
                matcher.start(), matcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        
        // Attributes
        matcher = XML_ATTRIBUTE_PATTERN.matcher(code)
        while (matcher.find()) {
            spannable.setSpan(
                ForegroundColorSpan(COLOR_FUNCTION),
                matcher.start(), matcher.end() - 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        
        // Strings
        matcher = XML_STRING_PATTERN.matcher(code)
        while (matcher.find()) {
            spannable.setSpan(
                ForegroundColorSpan(COLOR_STRING),
                matcher.start(), matcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        
        // Comments
        matcher = XML_COMMENT_PATTERN.matcher(code)
        while (matcher.find()) {
            spannable.setSpan(
                ForegroundColorSpan(COLOR_COMMENT),
                matcher.start(), matcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        
        return spannable
    }

    private fun highlightKeywords(
        spannable: SpannableString,
        code: String,
        keywords: List<String>,
        color: Int
    ) {
        keywords.forEach { keyword ->
            val pattern = Pattern.compile("\\b$keyword\\b")
            val matcher = pattern.matcher(code)
            while (matcher.find()) {
                spannable.setSpan(
                    ForegroundColorSpan(color),
                    matcher.start(), matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    private fun highlightPattern(
        spannable: SpannableString,
        code: String,
        patternString: String,
        color: Int
    ) {
        try {
            val pattern = Pattern.compile(patternString, Pattern.MULTILINE or Pattern.DOTALL)
            val matcher = pattern.matcher(code)
            while (matcher.find()) {
                spannable.setSpan(
                    ForegroundColorSpan(color),
                    matcher.start(), matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        } catch (e: Exception) {
            // Ignorar erros de regex
        }
    }
}
