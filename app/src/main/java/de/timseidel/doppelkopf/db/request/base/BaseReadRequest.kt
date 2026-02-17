package de.timseidel.doppelkopf.db.request.base

import com.google.firebase.firestore.FirebaseFirestore
import de.timseidel.doppelkopf.ui.util.ThreadingUtil
import de.timseidel.doppelkopf.util.Logging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

abstract class BaseReadRequest<R> {
    protected var readRequestListener: ReadRequestListener<R>? = null
    protected val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    fun execute(listener: ReadRequestListener<R>) {
        readRequestListener = listener
        doExecute()
    }

    protected abstract fun doExecute()

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

    protected fun failWithLog(message: String, exception: Throwable) {
        Logging.e(message, exception)
        onReadFailed()
    }

    suspend fun await(): R = suspendCancellableCoroutine { continuation ->
        execute(object : ReadRequestListener<R> {
            override fun onReadComplete(result: R) {
                if (continuation.isActive) {
                    continuation.resume(result)
                }
            }

            override fun onReadFailed() {
                if (continuation.isActive) {
                    continuation.resumeWithException(
                        IllegalStateException("${this@BaseReadRequest::class.simpleName} failed")
                    )
                }
            }
        })
    }
}
