package de.timseidel.doppelkopf.model

data class Game(
    val id: String,
    var timestamp: Long,
    var members: List<MemberAndFaction>,
    var winningFaction: Faction,
    var winningPoints: Int,
    var tacken: Int,
    var isBockrunde: Boolean,
    var gameType: GameType = GameType.NORMAL,
    var soloType: SoloType = SoloType.NONE,
)