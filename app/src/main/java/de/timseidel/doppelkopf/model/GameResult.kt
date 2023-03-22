package de.timseidel.doppelkopf.model

data class GameResult(
    val faction: Faction,
    val isWinner: Boolean,
    val tacken: Int,
    val points: Int,
    val isBockrunde: Boolean,
    val gameType: GameType
)