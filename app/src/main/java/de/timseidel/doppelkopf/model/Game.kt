package de.timseidel.doppelkopf.model

data class Game(
    val id: String,
    val timestamp: Long,
    val players: List<PlayerAndFaction>,
    val winningFaction: Faction,
    val winningPoints: Int,
    val tacken: Int,
    val isBockrunde: Boolean,
    val gameType: GameType = GameType.NORMAL,
    val soloType: SoloType = SoloType.NONE,
)