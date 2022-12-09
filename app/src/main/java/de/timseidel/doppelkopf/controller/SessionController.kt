package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.IGameController
import de.timseidel.doppelkopf.contracts.IPlayerController
import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.model.DokoSession

class SessionController : ISessionController {

    private val playerController : IPlayerController = PlayerController()
    private val gameController : IGameController = GameController()

    private lateinit var session : DokoSession

    override fun createSession(sessionName: String, playerNames: List<String>) {
        session = DokoSession(sessionName)

        val players = playerController.createPlayers(playerNames)
        playerController.addPlayers(players)
    }

    override fun getSession(): DokoSession {
        return session
    }

    override fun getPlayerController(): IPlayerController {
        return playerController
    }

    override fun getGameController(): IGameController {
        return gameController
    }
}