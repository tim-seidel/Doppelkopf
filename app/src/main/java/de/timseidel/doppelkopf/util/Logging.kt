package de.timseidel.doppelkopf.util

import android.util.Log

/**
 * This class provides a simple logging interface with default formatting to the android console.
 */
class Logging {

    companion object {
        private const val LOG_TAG = "DOKO"

        fun d(msg: String) {
            Log.d(LOG_TAG, msg)
        }

        fun e(msg: String) {
            Log.d(LOG_TAG, msg)
        }

        fun d(className: String, msg: String) {
            Log.d(LOG_TAG, "[$className]: $msg")
        }

        fun e(className: String, msg: String) {
            Log.e(LOG_TAG, "[$className]: $msg")
        }

        fun e(msg: String, e: Exception) {
            Log.e(LOG_TAG, "$msg || $e")
        }

        fun e(className: String, msg: String, e: Exception) {
            Log.e(LOG_TAG, "[$className]: $msg || $e")
        }
    }
}