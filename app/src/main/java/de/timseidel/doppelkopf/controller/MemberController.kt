package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.IMemberController
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.util.IdGenerator
import java.time.LocalDateTime

//TODO: Member und Player verknuepfen
class MemberController : IMemberController {

    private val members = mutableListOf<Member>()

    override fun createMember(name: String): Member {
        val trimmedName = name.trim()
        val nonEmptyName = trimmedName.ifEmpty { "Member" }
        return Member(
            IdGenerator.generateIdWithTimestamp("member") + "_$nonEmptyName",
            nonEmptyName,
            LocalDateTime.now()
        )
    }

    override fun createMembers(names: List<String>): List<Member> {
        if (!validateNames(names)) throw Exception("Invalid name list (check for duplicates or empty name)")

        val memberList = mutableListOf<Member>()
        for (name in names)
            memberList.add(createMember(name))

        return memberList
    }

    override fun addMember(member: Member) {
        members.add(member)
    }

    override fun addMembers(memberList: List<Member>) {
        members.addAll(memberList)
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

    override fun validateName(name: String): Boolean {
        if (name.isEmpty()) return false
        return getMemberByName(name) == null
    }

    override fun validateNames(names: List<String>): Boolean {
        if (names.size != names.distinct().count()) return false
        for (name in names) {
            if (name.trim().isEmpty()) return false
            if (getMemberByName(name) != null) return false
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