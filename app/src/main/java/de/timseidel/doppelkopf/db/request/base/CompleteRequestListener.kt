package de.timseidel.doppelkopf.db.request.base

interface CompleteRequestListener {

    fun onRequestCompleted()

    fun onRequestFailed()
}