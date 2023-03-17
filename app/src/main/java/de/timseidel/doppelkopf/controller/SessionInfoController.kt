package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.ISessionInfoController
import de.timseidel.doppelkopf.model.DokoSession
import java.time.ZoneOffset

class SessionInfoController : ISessionInfoController {

    private val sessionInfos: MutableList<DokoSession> = mutableListOf()

    override fun addSessionInfo(sessionInfo: DokoSession) {
        sessionInfos.add(sessionInfo)
        sessionInfos.sortBy { si -> si.date.toInstant(ZoneOffset.UTC).toEpochMilli() }
    }

    override fun addSessionInfos(sessionInfos: List<DokoSession>) {
        this.sessionInfos.addAll(sessionInfos)
        this.sessionInfos.sortBy { si -> si.date.toInstant(ZoneOffset.UTC).toEpochMilli() }
    }

    override fun getSessionInfoById(id: String): DokoSession {
        return sessionInfos.first { si -> si.id == id }
    }

    override fun getSessionInfos(): List<DokoSession> {
        return sessionInfos
    }
}