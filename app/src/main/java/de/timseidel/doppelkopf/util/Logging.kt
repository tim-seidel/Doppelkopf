package de.timseidel.doppelkopf.util

import android.util.Log

class Logging {

    companion object {
        private const val logTag = "DOKO"

        fun d(msg: String) {
            Log.d(logTag, msg)
        }

        fun e(msg: String) {
            Log.d(logTag, msg)
        }

        fun d(className: String, msg: String){
            Log.d(logTag, "[$className]: $msg")
        }

        fun e(className: String, msg: String){
            Log.e(logTag, "[$className]: $msg")
        }

        fun e(msg: String, ex: Exception){
            Log.e(logTag, "$msg || ${ex.toString()}" )
        }

        fun e(className: String, msg: String, ex: Exception){
            Log.e(logTag, "[$className]: $msg || ${ex.toString()}" )
        }
    }
}