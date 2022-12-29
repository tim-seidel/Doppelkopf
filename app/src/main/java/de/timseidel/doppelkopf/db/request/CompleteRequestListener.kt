package de.timseidel.doppelkopf.db.request

interface CompleteRequestListener {

    fun onRequestCompleted()

    fun onRequestFailed()
}