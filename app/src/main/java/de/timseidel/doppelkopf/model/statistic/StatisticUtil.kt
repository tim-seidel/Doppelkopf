package de.timseidel.doppelkopf.model.statistic

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.GameHistoryColumn
import de.timseidel.doppelkopf.model.GameHistoryItem
import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.statistic.group.GroupStatistics
import de.timseidel.doppelkopf.model.statistic.group.MemberStatistic
import de.timseidel.doppelkopf.util.GameUtil
import de.timseidel.doppelkopf.util.RangeDistribution

class StatisticUtil {
    companion object {
        // TODO: Called multiple times, maybe calculate it once in StatisticsCalculators
        fun getAccumulatedTackenHistory(gameResults: List<GameResult>): List<Int> {
            val tackenHistory = mutableListOf(0)
            gameResults.forEach { gr ->
                tackenHistory.add(tackenHistory[tackenHistory.size - 1] + gr.tacken)
            }

            return tackenHistory
        }

        fun getAccumulatedTackenHistoryWithoutBock(gameResults: List<GameResult>): List<Int> {
            val tackenHistory = mutableListOf(0)
            gameResults.forEach { gr ->
                tackenHistory.add(tackenHistory[tackenHistory.size - 1] + (if (gr.isBockrunde) gr.tacken / 2 else gr.tacken))
            }

            return tackenHistory
        }

        fun getAccumulatedStraftackenHistory(gameResults: List<GameResult>): List<Int> {
            val tackenHistory = mutableListOf(0)
            gameResults.forEach { gr ->
                tackenHistory.add(tackenHistory[tackenHistory.size - 1] + GameUtil.getStrafTacken(gr))
            }

            return tackenHistory
        }

        fun getTotalStrafTacken(gameResults: List<GameResult>): Int {
            val strafTackenHistory = getAccumulatedStraftackenHistory(gameResults)
            return if (strafTackenHistory.isNotEmpty()) strafTackenHistory.last() else 0
        }

        fun getTackenDistribution(
            gameResults: List<GameResult>,
            faction: Faction? = null
        ): RangeDistribution {
            val max =
                if (gameResults.isNotEmpty()) gameResults.maxWith(Comparator.comparingInt { it.tacken }).tacken else 0
            val min =
                if (gameResults.isNotEmpty()) gameResults.minWith(Comparator.comparingInt { it.tacken }).tacken else 0

            val distribution = RangeDistribution(min, max)

            gameResults.forEach { gr ->
                if (faction == null || gr.faction == faction) {
                    distribution.increase(gr.tacken, 1)
                }
            }

            return distribution
        }


        fun accumulateGameHistory(history: List<GameHistoryItem>): List<GameHistoryItem> {
            val accumulatedHistory = mutableListOf<GameHistoryItem>()

            history.forEach { item ->
                val accScores = mutableListOf<GameHistoryColumn>()
                item.scores.forEachIndexed { memberIndex, score ->
                    accScores.add(
                        GameHistoryColumn(
                            score.faction,
                            score.score + (if (accumulatedHistory.isNotEmpty()) accumulatedHistory.last().scores[memberIndex].score else 0),
                            score.isWinner,
                            score.isSolo
                        )
                    )
                }
                accumulatedHistory.add(
                    GameHistoryItem(
                        item.game,
                        item.number,
                        accScores,
                        item.isBockrunde
                    )
                )
            }

            return accumulatedHistory
        }

        fun getWinsOfMember(groupStatistics: GroupStatistics, member: Member): Int{
            var wins = 0
            groupStatistics.sessionStatistics.forEach { ss ->
                val winner = ss.sessionMemberStatistics.maxByOrNull { ms -> ms.general.total.tacken }
                if (winner != null && winner.member.id == member.id) {
                    wins++
                }
            }

            return wins
        }

        fun getLossesOfMember(groupStatistics: GroupStatistics, member: Member): Int{
            var losses = 0
            groupStatistics.sessionStatistics.forEach { sessionStatistics ->
                val loser = sessionStatistics.sessionMemberStatistics.minByOrNull { ms -> ms.general.total.tacken }
                if (loser != null && loser.member.id == member.id) {
                    losses++
                }
            }

            return losses
        }

        fun getSchwarzVerlorenCount(memberStatistic: MemberStatistic): Int {
            var schwarzVerlorenCount = 0
            memberStatistic.gameResultHistory.forEach { gameResult ->
                if (gameResult.gameType == GameType.SCHWARZVERLOREN && gameResult.faction == Faction.RE){
                    schwarzVerlorenCount++
                }
            }

            return schwarzVerlorenCount
        }

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

            if (currentWinStreak > 0) streakHistory.add(currentWinStreak)
            else if (currentLossStreak > 0) streakHistory.add(-1 * currentLossStreak)

            return StreakStatistics(longestLossStreak, longestWinStreak, streakHistory)
        }
    }
}