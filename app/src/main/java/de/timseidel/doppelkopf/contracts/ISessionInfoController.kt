package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.DokoSession

interface ISessionInfoController {

    fun addSessionInfo(sessionInfo: DokoSession)

    fun addSessionInfos(sessionInfos: List<DokoSession>)

    fun getSessionInfoById(id: String): DokoSession

    fun getSessionInfos(): List<DokoSession>
}