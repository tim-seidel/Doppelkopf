package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.IGroupController
import de.timseidel.doppelkopf.contracts.IMemberController
import de.timseidel.doppelkopf.model.Group
import de.timseidel.doppelkopf.util.IdGenerator
import java.time.LocalDateTime

class GroupController : IGroupController {

    private lateinit var _group: Group

    private val memberController: IMemberController = MemberController()

    override fun createGroup(groupName: String): Group {
        return Group(
            IdGenerator.generateIdWithTimestamp("group"),
            IdGenerator.generateGroupCode(),
            LocalDateTime.now(),
            groupName,
        )
    }

    override fun getGroup(): Group {
        return _group
    }

    override fun getMemberController(): IMemberController {
        return memberController
    }

    override fun set(group: Group) {
        _group = group
        memberController.reset()
    }
}