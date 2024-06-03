package de.timseidel.doppelkopf.model.statistic

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.GameHistoryColumn
import de.timseidel.doppelkopf.model.GameHistoryItem
import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.util.GameUtil
import de.timseidel.doppelkopf.util.RangeDistribution

class StatisticUtil {
    companion object {
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
    }
}