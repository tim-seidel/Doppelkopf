package de.timseidel.doppelkopf.db.request

import de.timseidel.doppelkopf.ui.util.ThreadingUtil

abstract class BaseReadRequest<R> {
    protected var readRequestListener: ReadRequestListener<R>? = null

    abstract fun execute(listener: ReadRequestListener<R>)

    protected fun onReadResult(result: R) {
        ThreadingUtil.runOnUIThread { readRequestListener?.onReadComplete(result) }
    }

    protected fun onReadFailed() {
        ThreadingUtil.runOnUIThread { readRequestListener?.onReadFailed() }
    }
}