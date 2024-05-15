package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.IGroupController
import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.contracts.ISettingsController
import de.timseidel.doppelkopf.contracts.IStatisticsController

//TODO: Deep copy lists und so
class DoppelkopfManager {

    private val groupController: IGroupController = GroupController()
    private val sessionController: ISessionController = SessionController()

    private val statisticsController: IStatisticsController = StatisticsController()

    private val setingsController: ISettingsController = SettingsController()

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

    fun getStatisticsController(): IStatisticsController {
        return statisticsController
    }

    fun getSettingsController(): ISettingsController {
        return setingsController
    }
}