package de.timseidel.doppelkopf.ui.util

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Runnable

class ThreadingUtil {
    companion object {
        private val mainHandler = Handler(Looper.getMainLooper())

        fun runOnUIThread(action: () -> Unit) {
            mainHandler.post(action)
        }
    }
}
