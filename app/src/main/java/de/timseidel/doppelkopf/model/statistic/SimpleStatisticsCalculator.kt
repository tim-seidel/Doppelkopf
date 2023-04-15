package de.timseidel.doppelkopf.model.statistic

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.GameResult

data class StreakStatistics(
    var longestLossStreak: Int = 0,
    var longestWinStreak: Int = 0,
    var streakHistory: MutableList<Int> = mutableListOf()
)

class SimpleStatisticsCalculator {

    fun calculateStreakStatistics(gameResults: List<GameResult>): StreakStatistics {
        val streakHistory = mutableListOf<Int>()

        var longestWinStreak = 0
        var longestLossStreak = 0
        var currentWinStreak = 0
        var currentLossStreak = 0

        gameResults.forEach { gr ->
            if (gr.isWinner && gr.faction != Faction.NONE) {
                if (currentLossStreak > longestLossStreak) {
                    longestLossStreak = currentLossStreak
                }
                if (currentLossStreak > 0) {
                    streakHistory.add(-1 * currentLossStreak)
                }
                currentLossStreak = 0
                currentWinStreak += 1
            }
            if (!gr.isWinner && gr.faction != Faction.NONE) {
                if (currentWinStreak > longestWinStreak) {
                    longestWinStreak = currentWinStreak
                }
                if (currentWinStreak > 0) {
                    streakHistory.add(currentWinStreak)
                }
                currentWinStreak = 0
                currentLossStreak += 1
            }
        }

        if (currentWinStreak > longestWinStreak) longestWinStreak = currentWinStreak
        if (currentLossStreak > longestLossStreak) longestLossStreak = currentLossStreak

        return StreakStatistics(longestLossStreak, longestWinStreak, streakHistory)
    }
}