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
                val currentSessionIds = currentSessions.asSequence().map { it.getSession().id }.toHashSet()
                val newSessions = sessionInfoListResult.filter { it.id !in currentSessionIds }
                Logging.d(
                    "StatisticUpdateRequest",
                    "Current sessions: ${currentSessions.size}. New sessions: ${newSessions.size}"
                )

                val latestSession = sessionInfoListResult.maxByOrNull { it.date }
                val needsLatestSessionRefresh = latestSession != null && latestSession.id in currentSessionIds

                val sessionsToLoad = buildList {
                    addAll(newSessions)
                    if (needsLatestSessionRefresh && latestSession != null) {
                        add(latestSession)
                    }
                }

                val loadedSessionsById = sessionsToLoad
                    .distinctBy { it.id }
                    .map { session ->
                        async { session.id to loadSessionController(session) }
                    }
                    .awaitAll()
                    .toMap()

                val currentSessionsById = currentSessions.associateBy { it.getSession().id }
                val allSessions = sessionInfoListResult.map { sessionInfo ->
                    loadedSessionsById[sessionInfo.id] ?: currentSessionsById[sessionInfo.id]
                    ?: throw IllegalStateException(
                        "Missing session controller for session ${sessionInfo.id}"
                    )
                }

                onReadResult(allSessions)
            } catch (e: Exception) {
                failWithLog("StatisticUpdateRequest failed for groupId=$groupId", e)
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
                        continuation.resumeWithException(
                            IllegalStateException(
                                "SessionInfoListRequest failed for groupId=$groupId"
                            )
                        )
                    }
                }
            })
        }

    private suspend fun loadSessionController(session: Session): ISessionController {
        val sessionController = SessionController()
        sessionController.set(session)

        val sessionGames = readSessionGames(session.id)
        Logging.d(
            "StatisticUpdateRequest",
            "Loaded games for session ${session.id}: ${sessionGames.size} games"
        )

        sessionGames.forEach { game ->
            sessionController.getGameController().addGame(game)
        }
        return sessionController
    }

    private suspend fun readSessionGames(sessionId: String): List<Game> =
        suspendCancellableCoroutine { continuation ->
            SessionGameRequest(
                groupId,
                sessionId,
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
                                IllegalStateException(
                                    "SessionGameRequest failed for groupId=$groupId, sessionId=$sessionId"
                                )
                            )
                        }
                    }
                }
            )
        }
}
