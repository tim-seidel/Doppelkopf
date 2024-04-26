package de.timseidel.doppelkopf.model

class GameHistoryItem(
    val game: Game,
    val number: Int,
    val scores: List<GameHistoryColumn>,
    val isBockrunde: Boolean
)

class GameHistoryColumn(
    var faction: Faction,
    var score: Int,
    var isWinner: Boolean,
    var isSolo: Boolean
)