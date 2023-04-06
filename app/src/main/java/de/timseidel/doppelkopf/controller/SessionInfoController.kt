package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.ISessionInfoController
import de.timseidel.doppelkopf.model.Session
import java.time.ZoneOffset

class SessionInfoController : ISessionInfoController {

    private val sessionInfos: MutableList<Session> = mutableListOf()

    override fun addSessionInfo(sessionInfo: Session) {
        sessionInfos.add(sessionInfo)
        sessionInfos.sortBy { si -> si.date.toInstant(ZoneOffset.UTC).toEpochMilli() }
    }

    override fun addSessionInfos(sessionInfos: List<Session>) {
        this.sessionInfos.addAll(sessionInfos)
        this.sessionInfos.sortBy { si -> si.date.toInstant(ZoneOffset.UTC).toEpochMilli() }
    }

    override fun getSessionInfoById(id: String): Session {
        return sessionInfos.first { si -> si.id == id }
    }

    override fun getSessionInfos(): List<Session> {
        return sessionInfos
    }
}