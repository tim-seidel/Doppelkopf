package de.timseidel.doppelkopf.db.request.base

import de.timseidel.doppelkopf.ui.util.ThreadingUtil
import de.timseidel.doppelkopf.util.Logging
import java.lang.Exception

abstract class BaseReadRequest<R> {
    protected var readRequestListener: ReadRequestListener<R>? = null

    abstract fun execute(listener: ReadRequestListener<R>)

    protected fun onReadResult(result: R) {
        ThreadingUtil.runOnUIThread { readRequestListener?.onReadComplete(result) }
    }

    protected fun onReadFailed() {
        ThreadingUtil.runOnUIThread { readRequestListener?.onReadFailed() }
    }

    protected fun failWithLog(message: String) {
        Logging.e(message)
        onReadFailed()
    }

    protected fun failWithLog(message: String, exception: Exception) {
        Logging.e(message, exception)
        onReadFailed()
    }
}