package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.IGameController
import de.timseidel.doppelkopf.contracts.IPlayerController
import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.model.DokoSession
import de.timseidel.doppelkopf.util.IdGenerator
import java.time.LocalDateTime

class SessionController : ISessionController {

    private lateinit var _session: DokoSession

    private val playerController: IPlayerController = PlayerController()
    private val gameController: IGameController = GameController()

    override fun createSession(sessionName: String): DokoSession {
        return DokoSession(
            IdGenerator.generateIdWithTimestamp("session"),
            sessionName,
            LocalDateTime.now(),
            0.0
        )
    }

    override fun getSession(): DokoSession {
        return _session
    }

    override fun getPlayerController(): IPlayerController {
        return playerController
    }

    override fun getGameController(): IGameController {
        return gameController
    }

    override fun set(session: DokoSession) {
        _session = session
        playerController.reset()
        gameController.reset()
    }
}