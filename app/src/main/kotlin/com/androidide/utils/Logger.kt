package com.androidide.utils

import android.content.Context
import android.util.Log

object Logger {
    private const val TAG = "AndroidIDE"
    private var isDebug = true

    fun init(context: Context) {
        // Inicialização do logger
    }

    fun d(message: String, tag: String = TAG) {
        if (isDebug) Log.d(tag, message)
    }

    fun e(message: String, tag: String = TAG, e: Throwable? = null) {
        Log.e(tag, message, e)
    }
    
    fun i(message: String, tag: String = TAG) {
        Log.i(tag, message)
    }
}
