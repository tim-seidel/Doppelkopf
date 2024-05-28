package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.Group

interface IGroupController {

    fun createGroup(groupName: String, groupCode: String): Group

    fun getGroup(): Group

    fun getMemberController(): IMemberController

    fun getSessionInfoController(): ISessionInfoController

    fun set(group: Group)
}