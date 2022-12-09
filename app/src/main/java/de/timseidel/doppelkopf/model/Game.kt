package de.timseidel.doppelkopf.model

import java.util.UUID

class Game(
    val id: UUID = UUID.randomUUID(),
    val players: List<PlayerAndParty>,
    var winner: Party,
    var winningPoints: Int,
    var gameType: GameType = GameType.NORMAL,
    var soloType: SoloType = SoloType.NONE,
    var tacken: Int = 0
){}