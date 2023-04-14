package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.Session

interface ISessionInfoController {

    fun addSessionInfo(sessionInfo: Session)

    fun addSessionInfos(sessionInfos: List<Session>)

    fun getSessionInfoById(id: String): Session

    fun getSessionInfos(): List<Session>

    fun reset()
}