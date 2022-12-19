package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.IGroupController
import de.timseidel.doppelkopf.contracts.ISessionController

//TODO: Deep copy lists und so
class DoppelkopfManager {

    private val groupController: IGroupController = GroupController()
    private val sessionController: ISessionController = SessionController()

    companion object {
        private var instance: DoppelkopfManager = DoppelkopfManager()

        fun getInstance(): DoppelkopfManager {
            return instance
        }
    }

    fun getGroupController(): IGroupController {
        return groupController
    }

    fun getSessionController(): ISessionController {
        return sessionController
    }
}