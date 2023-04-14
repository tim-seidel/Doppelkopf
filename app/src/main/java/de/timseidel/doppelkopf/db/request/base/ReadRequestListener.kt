package de.timseidel.doppelkopf.db.request.base

interface ReadRequestListener<R> {

    fun onReadComplete(result: R)

    fun onReadFailed()
}