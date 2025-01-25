package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.IMemberController
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.MemberAndFaction
import de.timseidel.doppelkopf.util.DoppelkopfException
import de.timseidel.doppelkopf.util.IdGenerator
import java.time.LocalDateTime

class MemberController : IMemberController {

    private val members = mutableListOf<Member>()

    override fun createMember(name: String): Member {
        val correctedName = name.trim().ifEmpty { "Member" }

        return Member(
            IdGenerator.generateIdWithTimestamp("member") + "_$correctedName",
            correctedName,
            LocalDateTime.now(),
            true
        )
    }

    override fun createMembers(names: List<String>): List<Member> {
        if (!validateNames(names)) {
            throw DoppelkopfException("Invalid name list (check for duplicates or empty name)")
        }

        val memberList = mutableListOf<Member>()
        for (name in names) {
            memberList.add(createMember(name))
        }

        return memberList
    }

    override fun addMember(member: Member) {
        members.add(member)
    }

    override fun addMembers(members: List<Member>) {
        this.members.addAll(members)
    }

    override fun removeMember(member: Member) {
        members.removeIf { p -> p.name == member.name }
    }

    override fun getMemberById(id: String): Member? {
        return members.firstOrNull { m -> m.id == id }
    }

    override fun getMemberByName(name: String): Member? {
        return members.firstOrNull { m -> m.name == name }
    }

    override fun getMembers(): List<Member> {
        return members.toList()
    }

    override fun getActiveMembers(): List<Member> {
        return members.filter { m -> m.isActive }.toList()
    }

    override fun getMembersAsFaction(): List<MemberAndFaction> {
        return members.map { m -> MemberAndFaction(m, Faction.NONE) }
    }

    override fun getMembersAsFaction(memberList: List<Member>): List<MemberAndFaction> {
        return memberList.map { m -> MemberAndFaction(m, Faction.NONE) }
    }

    override fun updateMember(memberId: String, updatedMember: Member) {
        val member = members.find { m -> m.id == memberId }
        if (member != null) {
            //member.name = updatedMember.name Not yet supported
            member.isActive = updatedMember.isActive
        }
    }

    override fun validateName(name: String): Boolean {
        return name.trim().isNotEmpty() && getMemberByName(name) == null
    }

    override fun validateNames(names: List<String>): Boolean {
        if (names.size != names.distinct().count()) {
            return false
        }

        for (name in names) {
            if (name.trim().isEmpty()) {
                return false
            }
            if (getMemberByName(name) != null) {
                return false
            }
        }
        return true
    }

    override fun getMemberNames(): List<String> {
        return members.map { m -> m.name }
    }

    override fun reset() {
        members.clear()
    }
}