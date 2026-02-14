package de.timseidel.doppelkopf.db.request

import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.controller.SessionController
import de.timseidel.doppelkopf.db.request.base.BaseReadRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.Logging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class StatisticUpdateRequest(
    private val groupId: String,
    private val currentSessions: List<ISessionController>
) : BaseReadRequest<List<ISessionController>>() {

    override fun execute(listener: ReadRequestListener<List<ISessionController>>) {
        readRequestListener = listener

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sessionInfoListResult = readSessionInfoList()
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

                val loadedSessionsById = sessionsToLoad
                    .distinctBy { it.id }
                    .map { session ->
                        async {
                            session.id to loadSessionController(session)
                        }
                    }
                    .awaitAll()
                    .toMap()

                val currentSessionsById = currentSessions.associateBy { it.getSession().id }
                val allSessions = mutableListOf<ISessionController>()
                sessionInfoListResult.forEach { sessionInfo ->
                    val sessionController =
                        loadedSessionsById[sessionInfo.id] ?: currentSessionsById[sessionInfo.id]

                    if (sessionController != null) {
                        allSessions.add(sessionController)
                    } else {
                        throw IllegalStateException("Missing session controller for session ${sessionInfo.id}")
                    }
                }

                onReadResult(allSessions)
            } catch (e: Exception) {
                failWithLog("StatisticUpdateRequest failed with ", e)
            }
        }
    }

    private suspend fun readSessionInfoList(): List<Session> =
        suspendCancellableCoroutine { continuation ->
            SessionInfoListRequest(groupId).execute(object : ReadRequestListener<List<Session>> {
                override fun onReadComplete(result: List<Session>) {
                    if (continuation.isActive) {
                        continuation.resume(result)
                    }
                }

                override fun onReadFailed() {
                    if (continuation.isActive) {
                        continuation.resumeWithException(IllegalStateException("SessionInfoListRequest failed"))
                    }
                }
            })
        }

    private suspend fun loadSessionController(session: Session): ISessionController {
        val sessionController = SessionController()
        sessionController.set(session)

        val sessionGames = suspendCancellableCoroutine<List<Game>> { continuation ->
            SessionGameRequest(
                groupId,
                session.id,
                DokoShortAccess.getMemberCtrl()
            ).execute(
                object : ReadRequestListener<List<Game>> {
                    override fun onReadComplete(result: List<Game>) {
                        if (continuation.isActive) {
                            continuation.resume(result)
                        }
                    }

                    override fun onReadFailed() {
                        if (continuation.isActive) {
                            continuation.resumeWithException(
                                IllegalStateException("SessionGameRequest failed for session ${session.id}")
                            )
                        }
                    }
                }
            )
        }

        Logging.d(
            "StatisticUpdateRequest",
            "Loaded games for session ${session.id}: ${sessionGames.count()} games"
        )

        sessionGames.forEach { game ->
            sessionController.getGameController().addGame(game)
        }
        return sessionController
    }
}
