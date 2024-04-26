package de.timseidel.doppelkopf.model

data class Game(
    val id: String,
    var timestamp: Long,
    var players: List<PlayerAndFaction>,
    var winningFaction: Faction,
    var winningPoints: Int,
    var tacken: Int,
    var isBockrunde: Boolean,
    var gameType: GameType = GameType.NORMAL,
    var soloType: SoloType = SoloType.NONE,
)