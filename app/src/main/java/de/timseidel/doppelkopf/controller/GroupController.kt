package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.IGroupController
import de.timseidel.doppelkopf.contracts.IMemberController
import de.timseidel.doppelkopf.contracts.ISessionInfoController
import de.timseidel.doppelkopf.model.Group
import de.timseidel.doppelkopf.util.IdGenerator
import java.time.LocalDateTime

class GroupController : IGroupController {

    private lateinit var group: Group

    private val sessionInfoController: ISessionInfoController = SessionInfoController()
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
        return group
    }

    override fun getMemberController(): IMemberController {
        return memberController
    }

    override fun getSessionInfoController(): ISessionInfoController {
        return sessionInfoController
    }

    override fun set(group: Group) {
        this.group = group
        memberController.reset()
        sessionInfoController.reset()
    }
}