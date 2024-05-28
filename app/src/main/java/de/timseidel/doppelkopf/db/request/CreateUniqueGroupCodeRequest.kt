package de.timseidel.doppelkopf.db.request

import de.timseidel.doppelkopf.db.request.base.BaseReadRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.util.IdGenerator
import de.timseidel.doppelkopf.util.Logging

class CreateUniqueGroupCodeRequest(private val maxTry: Int) : BaseReadRequest<String>() {
    override fun execute(listener: ReadRequestListener<String>) {
        readRequestListener = listener

        createAndCheckGroupCode(0)
    }

    private fun createAndCheckGroupCode(currentTry: Int) {
        val groupCode = IdGenerator.generateGroupCode()
        Logging.d(
            "CreateUniqueGroupCodeRequest",
            "Try to create group code [$groupCode] on try [${currentTry + 1}] of [$maxTry]"
        )

        GroupCodeExistsRequest(groupCode).execute(object : ReadRequestListener<Boolean> {
            override fun onReadComplete(result: Boolean) {
                if (!result) {
                    Logging.d("CreateUniqueGroupCodeRequest", "Group code [$groupCode] is unique.")
                    readRequestListener?.onReadComplete(groupCode)
                } else {
                    Logging.d("CreateUniqueGroupCodeRequest", "Group code [$groupCode] already exists.")
                    if ((currentTry + 1) < maxTry) {
                        createAndCheckGroupCode(currentTry + 1)
                    } else {
                        readRequestListener?.onReadFailed()
                    }
                }
            }

            override fun onReadFailed() {
                readRequestListener?.onReadFailed()
            }
        })
    }
}