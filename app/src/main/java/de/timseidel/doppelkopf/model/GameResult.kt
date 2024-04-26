package de.timseidel.doppelkopf.model

data class GameResult(
    val gameId: String,
    val faction: Faction,
    val isWinner: Boolean,
    val tacken: Int,
    val points: Int,
    val isBockrunde: Boolean,
    val gameType: GameType
)