package de.timseidel.doppelkopf.db.request

abstract class BaseCompleteRequest {

    protected var requestListener: CompleteRequestListener? = null

    protected fun notify(success: Boolean = true) {
        if (success) {
            requestListener?.onRequestCompleted()
        } else {
            requestListener?.onRequestFailed()
        }
        requestListener = null
    }
}