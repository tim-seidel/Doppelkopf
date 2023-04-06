package de.timseidel.doppelkopf.model.statistic

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
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

        fun convertGameHistoryToAccumulatedHistory(gameResults: List<GameResult>): List<GameResult> {
            val accumulatedGameHistory = mutableListOf<GameResult>()
            gameResults.forEach { gr ->
                accumulatedGameHistory.add(
                    GameResult(
                        gr.faction,
                        gr.isWinner,
                        gr.tacken + if (accumulatedGameHistory.isNotEmpty()) accumulatedGameHistory.last().tacken else 0,
                        gr.points,
                        gr.isBockrunde,
                        gr.gameType
                    )
                )
            }

            return accumulatedGameHistory
        }

        fun convertGameHistoriesToAccumulatedHistories(gameHistory: List<List<GameResult>>): List<List<GameResult>> {
            val accumulatedGameHistory = mutableListOf<List<GameResult>>()

            gameHistory.forEach { game ->
                val gamePlayerResults = mutableListOf<GameResult>()
                game.forEachIndexed { playerIndex, playerResult ->
                    gamePlayerResults.add(
                        GameResult(
                            playerResult.faction,
                            playerResult.isWinner,
                            playerResult.tacken + if (accumulatedGameHistory.isNotEmpty()) accumulatedGameHistory.last()[playerIndex].tacken else 0,
                            playerResult.points,
                            playerResult.isBockrunde,
                            playerResult.gameType
                        )
                    )
                }
                accumulatedGameHistory.add(gamePlayerResults)
            }

            return accumulatedGameHistory
        }
    }
}