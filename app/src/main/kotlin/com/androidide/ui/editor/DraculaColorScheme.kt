package com.androidide.ui.editor

import android.graphics.Color
import io.github.rosemoe.sora.widget.EditorColorScheme

class DraculaColorScheme : EditorColorScheme() {

    init {
        // Cores Base do Editor (Fundo escuro, texto claro)
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
        
        // Cores de Sintaxe (Tokens)
        setColor(OPERATOR, Color.parseColor("#FF79C6"))       // =, +, -, etc (Rosa)
        setColor(KEYWORD, Color.parseColor("#FF79C6"))        // fun, val, class (Rosa)
        setColor(LITERAL, Color.parseColor("#BD93F9"))        // true, false, null (Roxo)
        setColor(ANNOTATION, Color.parseColor("#F1FA8C"))     // @Override (Amarelo)
        setColor(ATTRIBUTE_NAME, Color.parseColor("#50FA7B")) // android:text (Verde)
        setColor(ATTRIBUTE_VALUE, Color.parseColor("#F1FA8C"))// "Hello" (Amarelo)
        setColor(COMMENT, Color.parseColor("#6272A4"))        // Comentários (Cinza Azulado)
        setColor(HTML_TAG, Color.parseColor("#FF79C6"))       // <View> (Rosa)
        setColor(FUNCTION_NAME, Color.parseColor("#8BE9FD"))  // method() (Ciano)
        setColor(IDENTIFIER_NAME, Color.parseColor("#F8F8F2"))// Variaveis
    }
}
