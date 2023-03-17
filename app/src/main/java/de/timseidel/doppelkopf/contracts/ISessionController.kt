package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.contracts.statistic.ISessionStatisticsController
import de.timseidel.doppelkopf.model.DokoSession

interface ISessionController {

    fun createSession(sessionName: String): DokoSession

    fun getSession(): DokoSession

    fun getPlayerController(): IPlayerController

    fun getGameController(): IGameController

    fun getSessionStatisticsController(): ISessionStatisticsController

    fun set(session: DokoSession)
}