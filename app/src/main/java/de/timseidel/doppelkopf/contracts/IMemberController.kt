package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.MemberAndFaction

interface IMemberController {

    fun createMember(name: String): Member

    fun createMembers(names: List<String>): List<Member>

    fun addMember(member: Member)

    fun addMembers(members: List<Member>)

    fun removeMember(member: Member)

    fun getMemberById(id: String): Member?

    fun getMemberByName(name: String): Member?

    fun getMembers(): List<Member>

    fun getMembersAsFaction(): List<MemberAndFaction>

    fun getMembersAsFaction(memberList: List<Member>): List<MemberAndFaction>

    fun validateName(name: String): Boolean

    fun validateNames(names: List<String>): Boolean

    fun getMemberNames(): List<String>

    fun reset()
}