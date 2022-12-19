package de.timseidel.doppelkopf.model

data class Game(
    val id: String,
    val players: List<PlayerAndFaction>,
    var winningFaction: Faction,
    var winningPoints: Int,
    var tacken: Int,
    var gameType: GameType? = GameType.NORMAL,
    var soloType: SoloType? = SoloType.NONE,
)