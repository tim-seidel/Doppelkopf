package de.timseidel.doppelkopf.util

import de.timseidel.doppelkopf.contracts.IGameController
import de.timseidel.doppelkopf.contracts.IGroupController
import de.timseidel.doppelkopf.contracts.IMemberController
import de.timseidel.doppelkopf.contracts.IPlayerController
import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.controller.DoppelkopfManager

class DokoShortAccess {
    companion object{
        fun getGroupCtrl(): IGroupController{
            return DoppelkopfManager.getInstance().getGroupController()
        }

        fun getMemberCtrl(): IMemberController{
            return DoppelkopfManager.getInstance().getGroupController().getMemberController()
        }

        fun getSessionCtrl(): ISessionController{
            return DoppelkopfManager.getInstance().getSessionController()
        }

        fun getPlayerCtrl(): IPlayerController {
            return DoppelkopfManager.getInstance().getSessionController().getPlayerController()
        }
        fun getGameCtrl(): IGameController {
            return DoppelkopfManager.getInstance().getSessionController().getGameController()
        }
    }
}