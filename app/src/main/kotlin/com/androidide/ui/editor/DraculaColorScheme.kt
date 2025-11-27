package com.androidide.ui.editor

import android.graphics.Color
import io.github.rosemoe.sora.widget.EditorColorScheme

class DraculaColorScheme : EditorColorScheme() {

    init {
        // Cores Base do Editor
        setColor(WHOLE_BACKGROUND, Color.parseColor("#282A36"))
        setColor(TEXT_NORMAL, Color.parseColor("#F8F8F2"))
        setColor(LINE_NUMBER_BACKGROUND, Color.parseColor("#282A36"))
        setColor(LINE_NUMBER, Color.parseColor("#6272A4"))
        setColor(LINE_DIVIDER, Color.parseColor("#44475A"))
        setColor(SELECTION_INSERT, Color.parseColor("#F8F8F2"))
        setColor(SELECTION_HANDLE, Color.parseColor("#BD93F9"))
        setColor(BLOCK_LINE, Color.parseColor("#44475A"))
        setColor(BLOCK_LINE_CURRENT, Color.parseColor("#44475A"))
        setColor(NON_PRINTABLE_CHAR, Color.parseColor("#6272A4"))
        
        // Cores de Sintaxe
        setColor(OPERATOR, Color.parseColor("#FF79C6"))
        setColor(KEYWORD, Color.parseColor("#FF79C6"))
        setColor(LITERAL, Color.parseColor("#BD93F9"))
        setColor(ANNOTATION, Color.parseColor("#F1FA8C"))
        setColor(ATTRIBUTE_NAME, Color.parseColor("#50FA7B"))
        setColor(ATTRIBUTE_VALUE, Color.parseColor("#F1FA8C"))
        setColor(COMMENT, Color.parseColor("#6272A4"))
        setColor(HTML_TAG, Color.parseColor("#FF79C6"))
        setColor(FUNCTION_NAME, Color.parseColor("#8BE9FD"))
        setColor(IDENTIFIER_NAME, Color.parseColor("#F8F8F2"))
    }
}
