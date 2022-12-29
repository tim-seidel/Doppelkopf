package de.timseidel.doppelkopf.model

data class PlayerGameResult(
    val faction: Faction,
    val isWinner: Boolean,
    val tacken: Int,
    val points: Int
)