package de.timseidel.doppelkopf.db.request

import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.controller.SessionController
import de.timseidel.doppelkopf.db.request.base.BaseReadRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.util.DokoShortAccess
import java.time.ZoneOffset
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class SessionListRequest(private val sessionInfos: List<Session>) :
    BaseReadRequest<List<ISessionController>>() {

    override fun doExecute() {
        if (sessionInfos.isEmpty()) {
            onReadResult(emptyList())
            return
        }

        val sessions = mutableListOf<ISessionController>()
        val remainingLoadCounter = AtomicInteger(sessionInfos.size)
        val isCompleted = AtomicBoolean(false)

        for (sessionInfo in sessionInfos) {
            val sessionController = SessionController()
            sessionController.set(sessionInfo)

            SessionGameRequest(
                DokoShortAccess.getGroupCtrl().getGroup().id,
                sessionInfo.id,
                DokoShortAccess.getMemberCtrl()
            ).execute(
                object : ReadRequestListener<List<Game>> {
                    override fun onReadComplete(result: List<Game>) {
                        if (isCompleted.get()) {
                            return
                        }

                        result.forEach { game ->
                            sessionController.getGameController().addGame(game)
                        }
                        sessions.add(sessionController)

                        if (remainingLoadCounter.decrementAndGet() == 0 && isCompleted.compareAndSet(
                                false,
                                true
                            )
                        ) {
                            sessions.sortWith(compareBy { s ->
                                s.getSession().date.toInstant(ZoneOffset.UTC).toEpochMilli()
                            })
                            onReadResult(sessions)
                        }
                    }

                    override fun onReadFailed() {
                        if (isCompleted.compareAndSet(false, true)) {
                            this@SessionListRequest.onReadFailed()
                        }
                    }
                })
        }
    }
}
