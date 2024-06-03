package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.IGameController
import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.util.IdGenerator
import java.time.LocalDateTime

class SessionController : ISessionController {

    private lateinit var session: Session

    private val gameController: IGameController = GameController()

    override fun createSession(sessionName: String, members: List<Member>): Session {
        return Session(
            IdGenerator.generateIdWithTimestamp("session"),
            sessionName,
            LocalDateTime.now(),
            0.0,
            members.toMutableList()
        )
    }

    override fun getSession(): Session {
        return session
    }

    override fun getGameController(): IGameController {
        return gameController
    }

    override fun set(session: Session) {
        this.session = session
        gameController.reset()
    }
}