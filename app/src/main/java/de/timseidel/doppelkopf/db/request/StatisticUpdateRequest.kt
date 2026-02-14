package de.timseidel.doppelkopf.db.request

import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.controller.SessionController
import de.timseidel.doppelkopf.db.request.base.BaseReadRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.Logging


class StatisticUpdateRequest(
    private val groupId: String,
    private val currentSessions: List<ISessionController>
) : BaseReadRequest<List<ISessionController>>() {

    override fun execute(listener: ReadRequestListener<List<ISessionController>>) {
        readRequestListener = listener

        SessionInfoListRequest(groupId).execute(object : ReadRequestListener<List<Session>> {
            override fun onReadComplete(sessionInfoListResult: List<Session>) {
                val newSessions = sessionInfoListResult.filter { session ->
                    currentSessions.none { it.getSession().id == session.id }
                }
                Logging.d(
                    "StatisticUpdateRequest",
                    "Current sessions: ${currentSessions.count()}. New sessions: ${newSessions.count()}"
                )

                val latestSession = sessionInfoListResult.maxByOrNull { session -> session.date }
                val needsLatestSessionRefresh = latestSession != null &&
                    currentSessions.any { it.getSession().id == latestSession.id }

                val sessionsToLoad = mutableListOf<Session>()
                sessionsToLoad.addAll(newSessions)
                if (needsLatestSessionRefresh && latestSession != null) {
                    sessionsToLoad.add(latestSession)
                }

                if (sessionsToLoad.isEmpty()) {
                    onReadResult(currentSessions)
                    return
                }

                loadSessionControllers(sessionsToLoad, object : ReadRequestListener<Map<String, ISessionController>> {
                    override fun onReadComplete(loadedSessionsById: Map<String, ISessionController>) {
                        val currentSessionsById = currentSessions.associateBy { it.getSession().id }
                        val allSessions = mutableListOf<ISessionController>()
                        sessionInfoListResult.forEach { sessionInfo ->
                            val sessionController =
                                loadedSessionsById[sessionInfo.id] ?: currentSessionsById[sessionInfo.id]

                            if (sessionController != null) {
                                allSessions.add(sessionController)
                            } else {
                                failWithLog("StatisticUpdateRequest: Missing session controller for session ${sessionInfo.id}")
                                return
                            }
                        }

                        onReadResult(allSessions)
                    }

                    override fun onReadFailed() {
                        this@StatisticUpdateRequest.onReadFailed()
                    }
                })
            }

            override fun onReadFailed() {
                this@StatisticUpdateRequest.onReadFailed()
            }
        })
    }

    private fun loadSessionControllers(
        sessions: List<Session>,
        listener: ReadRequestListener<Map<String, ISessionController>>
    ) {
        if (sessions.isEmpty()) {
            listener.onReadComplete(emptyMap())
            return
        }

        val loadedSessionControllersById = mutableMapOf<String, ISessionController>()
        var remainingSessionsToLoad = sessions.count()
        var hasFailed = false

        sessions.forEach { session ->
            loadSessionController(session, object : ReadRequestListener<ISessionController> {
                override fun onReadComplete(sessionController: ISessionController) {
                    if (hasFailed) return

                    loadedSessionControllersById[sessionController.getSession().id] = sessionController
                    remainingSessionsToLoad -= 1
                    if (remainingSessionsToLoad == 0) {
                        listener.onReadComplete(loadedSessionControllersById)
                    }
                }

                override fun onReadFailed() {
                    if (hasFailed) return
                    hasFailed = true
                    listener.onReadFailed()
                }
            }
            )
        }
    }

    private fun loadSessionController(
        session: Session,
        listener: ReadRequestListener<ISessionController>
    ) {
        val sessionController = SessionController()
        sessionController.set(session)

        SessionGameRequest(
            groupId,
            session.id,
            DokoShortAccess.getMemberCtrl()
        ).execute(
            object : ReadRequestListener<List<Game>> {
                override fun onReadComplete(sessionGames: List<Game>) {
                    Logging.d(
                        "StatisticUpdateRequest",
                        "Loaded games for session ${session.id}: ${sessionGames.count()} games"
                    )

                    sessionGames.forEach { game ->
                        sessionController.getGameController().addGame(game)
                    }

                    listener.onReadComplete(sessionController)
                }

                override fun onReadFailed() {
                    listener.onReadFailed()
                }
            }
        )
    }
}
