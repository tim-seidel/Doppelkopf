package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.DokoSession

interface ISessionController {

    fun createSession(sessionName: String, playerNames: List<String>)

    fun getSession(): DokoSession

    fun getPlayerController() : IPlayerController

    fun getGameController(): IGameController
}