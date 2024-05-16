package de.timseidel.doppelkopf.db

import de.timseidel.doppelkopf.contracts.IPlayerController
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.Group
import de.timseidel.doppelkopf.model.GroupSettings
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.model.PlayerAndFaction
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
    var timeCreated: Long
) {
    constructor() : this("", "", 0)
}

data class SessionDto(
    var id: String,
    var name: String,
    var timeCreated: Long,
    var tackenPrice: Double
) {
    constructor() : this("", "", 0, 0.0)
}

data class PlayerDto(
    var id: String,
    var name: String
) {
    constructor() : this("", "")
}

data class PlayerAndFactionDto(
    var playerId: String,
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
    var factions: List<PlayerAndFactionDto>
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
                member.creationTime.toInstant(ZoneOffset.UTC).toEpochMilli()
            )
        }

        fun fromMemberDTOtoMember(dto: MemberDto): Member {
            return Member(
                dto.id,
                dto.name,
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(dto.timeCreated),
                    ZoneId.systemDefault()
                )
            )
        }

        fun fromSessionToSessionDTO(session: Session): SessionDto {
            return SessionDto(
                session.id,
                session.name,
                session.date.toInstant(ZoneOffset.UTC).toEpochMilli(),
                session.tackenPrice
            )
        }

        fun fromSessionDTOtoSession(dto: SessionDto): Session {
            return Session(
                dto.id,
                dto.name,
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(dto.timeCreated),
                    ZoneId.systemDefault()
                ),
                dto.tackenPrice
            )
        }

        fun fromPlayerToPlayerDTO(player: Player): PlayerDto {
            return PlayerDto(player.id, player.name)
        }

        fun fromPlayerDTOtoPlayer(dto: PlayerDto): Player {
            return Player(dto.id, dto.name)
        }

        fun fromGameToGameDTO(game: Game): GameDto {
            val resultDTOs = mutableListOf<PlayerAndFactionDto>()
            game.players.forEach { paf ->
                resultDTOs.add(PlayerAndFactionDto(paf.player.id, paf.faction))
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

        fun fromGameDTOtoGame(dto: GameDto, playerController: IPlayerController): Game {
            val factions = mutableListOf<PlayerAndFaction>()
            dto.factions.forEach { paf ->
                factions.add(
                    PlayerAndFaction(
                        playerController.getPlayerById(paf.playerId)
                            ?: throw Exception("Player with id [${paf.playerId}] not found."),
                        paf.faction
                    )
                )
            }

            factions.sortBy { f -> f.player.id }

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
