package de.timseidel.doppelkopf.model.statistic

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.PlayerGameResult
import de.timseidel.doppelkopf.util.GameUtil
import de.timseidel.doppelkopf.util.RangeDistribution

class StatisticUtil {
    companion object {
        fun getAccumulatedTackenHistory(gameResults: List<PlayerGameResult>): List<Int> {
            val tackenHistory = mutableListOf(0)
            gameResults.forEach { gr ->
                tackenHistory.add(tackenHistory[tackenHistory.size - 1] + gr.tacken)
            }

            return tackenHistory
        }

        fun getAccumulatedTackenHistoryWithoutBock(gameResults: List<PlayerGameResult>): List<Int> {
            val tackenHistory = mutableListOf(0)
            gameResults.forEach { gr ->
                tackenHistory.add(tackenHistory[tackenHistory.size - 1] + (if (gr.isBockrunde) gr.tacken / 2 else gr.tacken))
            }

            return tackenHistory
        }

        fun getAccumulatedStraftackenHistory(gameResults: List<PlayerGameResult>): List<Int> {
            val tackenHistory = mutableListOf(0)
            gameResults.forEach { gr ->
                tackenHistory.add(tackenHistory[tackenHistory.size - 1] + GameUtil.getStrafTacken(gr))
            }

            return tackenHistory
        }

        fun getTackenDistribution(
            gameResults: List<PlayerGameResult>,
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
    }
}