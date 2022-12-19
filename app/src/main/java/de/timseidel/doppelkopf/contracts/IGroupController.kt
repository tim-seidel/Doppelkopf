package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.Group

interface IGroupController {

    fun createGroup(groupName: String): Group
}