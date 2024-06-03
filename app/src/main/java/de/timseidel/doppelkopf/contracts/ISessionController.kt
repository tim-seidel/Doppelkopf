package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.Session

interface ISessionController {

    fun createSession(sessionName: String, members: List<Member>): Session

    fun getSession(): Session

    fun getGameController(): IGameController

    fun set(session: Session)
}