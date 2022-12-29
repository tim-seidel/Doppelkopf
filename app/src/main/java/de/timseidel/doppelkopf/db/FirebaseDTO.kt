package de.timseidel.doppelkopf.db

import de.timseidel.doppelkopf.contracts.IPlayerController
import de.timseidel.doppelkopf.model.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

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
    var faction: Faction,
    var tacken: Int,
    var points: Int,
    var factions: List<PlayerAndFactionDto>
) {
    constructor() : this("", Faction.NONE, 0, 0, emptyList())
}

class FirebaseDTO() {
    companion object {
        fun fromSessionToSessionDTO(session: DokoSession): SessionDto {
            return SessionDto(
                session.id,
                session.name,
                session.date.toInstant(ZoneOffset.UTC).toEpochMilli(),
                session.tackenPrice
            )
        }

        fun fromSessionDTOtoSession(dto: SessionDto): DokoSession {
            return DokoSession(
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
                game.winningFaction,
                game.tacken,
                game.winningPoints,
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

            return Game(
                dto.id,
                factions,
                dto.faction,
                dto.points,
                dto.tacken
            )
        }
    }
}
