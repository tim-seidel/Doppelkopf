package de.timseidel.doppelkopf.db.request

import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.controller.SessionController
import de.timseidel.doppelkopf.db.request.base.BaseReadRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.Logging
import kotlin.collections.forEach


class StatisticUpdateRequest(
    private val groupId: String,
    private val currentSessions: List<ISessionController>
) : BaseReadRequest<List<ISessionController>>() {
    override fun execute(listener: ReadRequestListener<List<ISessionController>>) {
        readRequestListener = listener

        SessionInfoListRequest(groupId).execute(object : ReadRequestListener<List<Session>> {
            override fun onReadComplete(sessinInfoListResult: List<Session>) {
                val newSessions = sessinInfoListResult.filter { session ->
                    currentSessions.none { it.getSession().id == session.id }
                }
                Logging.d("StatisticUpdateRequest", "Current sessions: ${currentSessions.count()}. New sessions: ${newSessions.count()}")

                val newSessionControllers = mutableListOf<ISessionController>()
                var remainingLoadCounter = newSessions.count()

                if( newSessions.count() == 0) {
                    if (currentSessions.count() > 0) {
                        val latestSession =
                            sessinInfoListResult.maxBy { session -> session.date }
                        val updatedSessionController = SessionController()
                        updatedSessionController.set(latestSession)

                        SessionGameRequest(
                            DokoShortAccess.getGroupCtrl().getGroup().id,
                            latestSession.id,
                            DokoShortAccess.getMemberCtrl()
                        ).execute(
                            object : ReadRequestListener<List<Game>> {
                                override fun onReadComplete(
                                    latestSessionGameRequestResult: List<Game>
                                ) {
                                    Logging.d(
                                        "StatisticUpdateRequest",
                                        "Loading games for latest session ${latestSession.id} for update"
                                    )

                                    latestSessionGameRequestResult.forEach { game ->
                                        updatedSessionController.getGameController()
                                            .addGame(game)
                                    }

                                    val allSessions =
                                        mutableListOf<ISessionController>()
                                    currentSessions.forEach { currentSessions ->
                                        if (currentSessions.getSession().id != latestSession.id) {
                                            allSessions.add(currentSessions)
                                        } else {
                                            allSessions.add(
                                                updatedSessionController
                                            )
                                        }
                                    }
                                    onReadResult(allSessions)
                                }

                                override fun onReadFailed() {
                                    this.onReadFailed()
                                }
                            })
                    }
                    else{
                        onReadResult(currentSessions)
                    }
                }else{
                    for (newSession in newSessions) {
                        val newSessionController = SessionController()
                        newSessionController.set(newSession)

                        SessionGameRequest(
                            groupId,
                            newSession.id,
                            DokoShortAccess.getMemberCtrl()
                        ).execute(
                            object : ReadRequestListener<List<Game>> {
                                override fun onReadComplete(sessionGameRequestResult: List<Game>) {
                                    Logging.d(
                                        "StatisticUpdateRequest",
                                        "Loaded games for new session ${newSession.id}: ${sessionGameRequestResult.count()} games"
                                    )
                                    sessionGameRequestResult.forEach { game ->
                                        newSessionController.getGameController().addGame(game)
                                    }
                                    newSessionControllers.add(newSessionController)
                                    remainingLoadCounter -= 1

                                    if (remainingLoadCounter == 0) {
                                        if (currentSessions.count() > 0) {
                                            val latestSession =
                                                sessinInfoListResult.maxBy { session -> session.date }
                                            val updatedSessionController = SessionController()
                                            updatedSessionController.set(latestSession)

                                            SessionGameRequest(
                                                DokoShortAccess.getGroupCtrl().getGroup().id,
                                                latestSession.id,
                                                DokoShortAccess.getMemberCtrl()
                                            ).execute(
                                                object : ReadRequestListener<List<Game>> {
                                                    override fun onReadComplete(
                                                        latestSessionGameRequestResult: List<Game>
                                                    ) {
                                                        Logging.d(
                                                            "StatisticUpdateRequest",
                                                            "Loading games for latest session ${latestSession.id} for update"
                                                        )

                                                        latestSessionGameRequestResult.forEach { game ->
                                                            updatedSessionController.getGameController()
                                                                .addGame(game)
                                                        }

                                                        val allSessions =
                                                            mutableListOf<ISessionController>()
                                                        currentSessions.forEach { currentSessions ->
                                                            if (currentSessions.getSession().id != latestSession.id) {
                                                                allSessions.add(currentSessions)
                                                            } else {
                                                                allSessions.add(
                                                                    updatedSessionController
                                                                )
                                                            }
                                                        }
                                                        allSessions.addAll(newSessionControllers)

                                                        onReadResult(allSessions)
                                                    }

                                                    override fun onReadFailed() {
                                                        this.onReadFailed()
                                                    }
                                                })
                                        } else {
                                            onReadResult(newSessionControllers)
                                        }
                                    }
                                }

                                override fun onReadFailed() {
                                    this.onReadFailed()
                                }
                            })
                    }
                }
            }

            override fun onReadFailed() {
                this.onReadFailed()
            }
        })
    }
}
