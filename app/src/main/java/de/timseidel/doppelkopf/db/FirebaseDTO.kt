package de.timseidel.doppelkopf.db

import de.timseidel.doppelkopf.contracts.IMemberController
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.Group
import de.timseidel.doppelkopf.model.GroupSettings
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.MemberAndFaction
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.util.Logging
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

data class GroupDto(
    var id: String,
    var code: String,
    var name: String,
    var timeCreated: Long,
    var settingIsBockrundeEnabled: Boolean
) {
    constructor() : this("", "", "", 0, true)
}

data class MemberDto(
    var id: String,
    var name: String,
    var timeCreated: Long,
    var isActive: Boolean
) {
    constructor() : this("", "", 0, true)
}

data class SessionDto(
    var id: String,
    var name: String,
    var timeCreated: Long,
    var tackenPrice: Double,
    var memberIds: List<String>
) {
    constructor() : this("", "", 0, 0.0, emptyList())
}

data class MemberAndFactionDto(
    var memberId: String,
    var faction: Faction
) {
    constructor() : this("", Faction.NONE)
}

data class GameDto(
    var id: String,
    var timestamp: Long,
    var faction: Faction,
    var tacken: Int,
    var points: Int,
    var isBockrunde: Boolean,
    var gameType: GameType,
    var factions: List<MemberAndFactionDto>
) {
    constructor() : this("", 0, Faction.NONE, 0, 0, false, GameType.NORMAL, emptyList())
}

class FirebaseDTO {
    companion object {
        fun fromGroupToGroupDTO(group: Group, groupSettings: GroupSettings): GroupDto {
            return GroupDto(
                group.id,
                group.code,
                group.name,
                group.date.toInstant(ZoneOffset.UTC).toEpochMilli(),
                groupSettings.isBockrundeEnabled
            )
        }

        fun fromGroupDTOtoGroup(dto: GroupDto): Group {
            return Group(
                dto.id,
                dto.code,
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(dto.timeCreated),
                    ZoneId.systemDefault()
                ),
                dto.name
            )
        }

        fun fromGroupDTOtoGroupSettings(dto: GroupDto): GroupSettings {
            return GroupSettings(dto.settingIsBockrundeEnabled)
        }

        fun fromMemberToMemberDTO(member: Member): MemberDto {
            return MemberDto(
                member.id,
                member.name,
                member.creationTime.toInstant(ZoneOffset.UTC).toEpochMilli(),
                member.isActive
            )
        }

        fun fromMemberDTOtoMember(dto: MemberDto): Member {
            return Member(
                dto.id,
                dto.name,
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(dto.timeCreated),
                    ZoneId.systemDefault()
                ),
                dto.isActive
            )
        }

        fun fromSessionToSessionDTO(session: Session): SessionDto {
            val memberIds: MutableList<String> = mutableListOf()
            session.members.forEach { m -> memberIds.add(m.id) }

            return SessionDto(
                session.id,
                session.name,
                session.date.toInstant(ZoneOffset.UTC).toEpochMilli(),
                session.tackenPrice,
                memberIds
            )
        }

        fun fromSessionDTOtoSession(dto: SessionDto, memberController: IMemberController): Session {
            val members = mutableListOf<Member>()

            dto.memberIds.forEach { mDto ->
                val member = memberController.getMemberById(mDto)

                if (member != null) {
                    members.add(member)
                } else {
                    Logging.e("Creating session from DTO: Member with id [$mDto] not found.")
                }
            }

            return Session(
                dto.id,
                dto.name,
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(dto.timeCreated),
                    ZoneId.systemDefault()
                ),
                dto.tackenPrice,
                members
            )
        }

        fun fromGameToGameDTO(game: Game): GameDto {
            val resultDTOs = mutableListOf<MemberAndFactionDto>()
            game.members.forEach { paf ->
                resultDTOs.add(MemberAndFactionDto(paf.member.id, paf.faction))
            }

            return GameDto(
                game.id,
                game.timestamp,
                game.winningFaction,
                game.tacken,
                game.winningPoints,
                game.isBockrunde,
                game.gameType,
                resultDTOs
            )
        }

        fun fromGameDTOtoGame(dto: GameDto, memberController: IMemberController): Game {
            val factions = mutableListOf<MemberAndFaction>()
            dto.factions.forEach { maf ->
                val member = memberController.getMemberById(maf.memberId)
                if (member != null) {
                    factions.add(MemberAndFaction(member, maf.faction))
                } else {
                    Logging.e("Creating game from DTO: Member with id [${maf.memberId}] not found.")
                }
            }

            factions.sortBy { f -> f.member.id }

            return Game(
                dto.id,
                dto.timestamp,
                factions,
                dto.faction,
                dto.points,
                dto.tacken,
                dto.isBockrunde,
                dto.gameType
            )
        }
    }
}
