package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.Session

interface ISessionController {

    fun createSession(sessionName: String): Session

    fun getSession(): Session

    fun getPlayerController(): IPlayerController

    fun getGameController(): IGameController

    fun set(session: Session)
}