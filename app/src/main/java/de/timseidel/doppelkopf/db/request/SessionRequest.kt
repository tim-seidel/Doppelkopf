package de.timseidel.doppelkopf.db.request

import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.controller.SessionController
import de.timseidel.doppelkopf.db.request.base.BaseReadRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.util.DokoShortAccess
import java.time.ZoneOffset

class SessionListRequest(private val sessionInfos: List<Session>) :
    BaseReadRequest<List<ISessionController>>() {
    override fun execute(listener: ReadRequestListener<List<ISessionController>>) {
        readRequestListener = listener

        if (sessionInfos.isEmpty()) {
            onReadResult(mutableListOf())
            return
        }

        val sessions = mutableListOf<ISessionController>()
        var remainingLoadCounter = sessionInfos.size

        for (sessionInfo in sessionInfos) {
            val sessionController = SessionController()
            sessionController.set(sessionInfo)

            SessionPlayerRequest(
                DokoShortAccess.getGroupCtrl().getGroup().id,
                sessionInfo.id
            ).execute(object :
                ReadRequestListener<List<Player>> {

                override fun onReadComplete(result: List<Player>) {
                    sessionController.getPlayerController().addPlayers(result)

                    SessionGameRequest(
                        DokoShortAccess.getGroupCtrl().getGroup().id,
                        sessionInfo.id,
                        sessionController.getPlayerController()
                    ).execute(
                        object : ReadRequestListener<List<Game>> {
                            override fun onReadComplete(result: List<Game>) {
                                result.forEach { game ->
                                    sessionController.getGameController().addGame(game)
                                }

                                sessions.add(sessionController)
                                remainingLoadCounter -= 1

                                if (remainingLoadCounter == 0) {
                                    sessions.sortBy { s ->
                                        s.getSession().date.toInstant(ZoneOffset.UTC).toEpochMilli()
                                    }

                                    onReadResult(sessions)
                                }
                            }

                            override fun onReadFailed() {
                                this.onReadFailed()
                            }
                        })
                }

                override fun onReadFailed() {
                    this.onReadFailed()
                }
            })
        }
    }
}