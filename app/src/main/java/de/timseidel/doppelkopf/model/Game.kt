package de.timseidel.doppelkopf.model

data class Game(
    val id: String,
    val timestamp: Long,
    val players: List<PlayerAndFaction>,
    var winningFaction: Faction,
    var winningPoints: Int,
    var tacken: Int,
    val isBockrunde: Boolean,
    var gameType: GameType = GameType.NORMAL,
    var soloType: SoloType = SoloType.NONE,
)