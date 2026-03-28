package de.timseidel.doppelkopf.db.request

import de.timseidel.doppelkopf.db.request.base.BaseReadRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.util.IdGenerator
import de.timseidel.doppelkopf.util.Logging

class CreateUniqueGroupCodeRequest(private val maxTry: Int) : BaseReadRequest<String>() {
    override fun doExecute() {
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
                try {
                    if (!result) {
                        Logging.d("CreateUniqueGroupCodeRequest", "Group code [$groupCode] is unique.")
                        onReadResult(groupCode)
                    } else {
                        Logging.d("CreateUniqueGroupCodeRequest", "Group code [$groupCode] already exists.")
                        if ((currentTry + 1) < maxTry) {
                            createAndCheckGroupCode(currentTry + 1)
                        } else {
                            failWithLog("Unable to create unique group code after [$maxTry] tries.")
                        }
                    }
                } catch (e: Exception) {
                    failWithLog("CreateUniqueGroupCodeRequest handling failed for groupCode=$groupCode", e)
                }
            }

            override fun onReadFailed() {
                failWithLog("CreateUniqueGroupCodeRequest failed while checking group code [$groupCode].")
            }
        })
    }
}
