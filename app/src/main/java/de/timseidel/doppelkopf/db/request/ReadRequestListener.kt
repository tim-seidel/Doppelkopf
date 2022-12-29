package de.timseidel.doppelkopf.db.request

interface ReadRequestListener<R> {

    fun onReadComplete(result: R)

    fun onReadFailed()
}