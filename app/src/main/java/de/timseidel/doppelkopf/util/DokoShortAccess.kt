package de.timseidel.doppelkopf.util

import de.timseidel.doppelkopf.contracts.IGameController
import de.timseidel.doppelkopf.contracts.IGroupController
import de.timseidel.doppelkopf.contracts.IMemberController
import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.contracts.ISessionInfoController
import de.timseidel.doppelkopf.contracts.ISettingsController
import de.timseidel.doppelkopf.contracts.IStatisticsController
import de.timseidel.doppelkopf.controller.DoppelkopfManager

class DokoShortAccess {
    companion object {
        fun getGroupCtrl(): IGroupController {
            return DoppelkopfManager.getInstance().getGroupController()
        }

        fun getMemberCtrl(): IMemberController {
            return DoppelkopfManager.getInstance().getGroupController().getMemberController()
        }

        fun getSessionInfoCtrl(): ISessionInfoController {
            return DoppelkopfManager.getInstance().getGroupController().getSessionInfoController()
        }

        fun getSessionCtrl(): ISessionController {
            return DoppelkopfManager.getInstance().getSessionController()
        }

        fun getGameCtrl(): IGameController {
            return DoppelkopfManager.getInstance().getSessionController().getGameController()
        }

        fun getStatsCtrl(): IStatisticsController {
            return DoppelkopfManager.getInstance().getStatisticsController()
        }

        fun getSettingsCtrl(): ISettingsController {
            return DoppelkopfManager.getInstance().getSettingsController()
        }
    }
}