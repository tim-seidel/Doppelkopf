package de.timseidel.doppelkopf.util

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Runnable

class ThreadingUtil {
    companion object {
        fun runOnUIThread(r: Runnable) {
            Handler(Looper.getMainLooper()).post(r)
        }
    }
}