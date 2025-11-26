package com.androidide.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private const val PREF_NAME = "androidide_prefs"
    private const val KEY_FONT_SIZE = "font_size"
    private const val KEY_SHOW_LINE_NUMBERS = "show_line_numbers"
    private const val KEY_WORD_WRAP = "word_wrap"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getFontSize(context: Context): Float = getPrefs(context).getFloat(KEY_FONT_SIZE, 14f)

    fun setFontSize(context: Context, size: Float) {
        getPrefs(context).edit().putFloat(KEY_FONT_SIZE, size).apply()
    }

    fun isLineNumbersEnabled(context: Context): Boolean = getPrefs(context).getBoolean(KEY_SHOW_LINE_NUMBERS, true)

    fun setLineNumbersEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_SHOW_LINE_NUMBERS, enabled).apply()
    }
    
    fun isWordWrapEnabled(context: Context): Boolean = getPrefs(context).getBoolean(KEY_WORD_WRAP, false)

    fun setWordWrapEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_WORD_WRAP, enabled).apply()
    }
}
