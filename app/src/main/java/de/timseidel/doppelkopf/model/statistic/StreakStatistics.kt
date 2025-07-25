package de.timseidel.doppelkopf.model.statistic

data class StreakStatistics(
    var longestLossStreak: Int = 0,
    var longestWinStreak: Int = 0,
    var streakHistory: MutableList<Int> = mutableListOf()
)