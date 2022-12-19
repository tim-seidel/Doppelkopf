package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.IGroupController
import de.timseidel.doppelkopf.model.Group
import de.timseidel.doppelkopf.util.IdGenerator
import java.time.LocalDateTime

class GroupController : IGroupController {
    override fun createGroup(groupName: String): Group {
        return Group(
            IdGenerator.generateIdWithTimestamp("group"),
            LocalDateTime.now(),
            groupName,
            mutableListOf(),
            mutableListOf()
        )
    }
}