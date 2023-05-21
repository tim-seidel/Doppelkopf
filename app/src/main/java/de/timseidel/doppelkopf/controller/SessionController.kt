package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.IGameController
import de.timseidel.doppelkopf.contracts.IPlayerController
import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.util.IdGenerator
import java.time.LocalDateTime

class SessionController : ISessionController {

    private lateinit var session: Session

    private val playerController: IPlayerController = PlayerController()
    private val gameController: IGameController = GameController()

    override fun createSession(sessionName: String): Session {
        return Session(
            IdGenerator.generateIdWithTimestamp("session"),
            sessionName,
            LocalDateTime.now(),
            0.0
        )
    }

    override fun getSession(): Session {
        return session
    }

    override fun getPlayerController(): IPlayerController {
        return playerController
    }

    override fun getGameController(): IGameController {
        return gameController
    }

    override fun set(session: Session) {
        this.session = session
        playerController.reset()
        gameController.reset()
    }
}