package de.timseidel.doppelkopf.util

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.PlayerGameResult

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

        fun getAccumulatedTackenLosses(gameResults: List<PlayerGameResult>): List<Int> {
            val tackenHistory = mutableListOf(0)
            gameResults.forEach { gr ->
                tackenHistory.add(tackenHistory[tackenHistory.size - 1] + (if (gr.faction != Faction.NONE && gr.tacken < 0) -1 * gr.tacken else 0))
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