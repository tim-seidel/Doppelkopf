package de.timseidel.doppelkopf.model.statistic

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.PlayerGameResult

class SessionStatisticsCalculator {

    //TODO: Sollte bei Soli Contra -3* gerechnet bekommen?
    fun calculateStatistics(games: List<Game>): SessionStatistics {
        val stats = SessionStatistics()

        games.forEach { g ->
            if (g.winningFaction != Faction.NONE) {
                val winnerResult =
                    PlayerGameResult(
                        g.winningFaction,
                        true,
                        g.tacken,
                        g.winningPoints
                    )
                val loserResult =
                    PlayerGameResult(
                        if (g.winningFaction == Faction.RE) Faction.CONTRA else Faction.CONTRA,
                        false,
                        -1 * g.tacken,
                        240 - g.winningPoints
                    )

                addGameResult(winnerResult, stats.general.total) //TODO abs(tacken)
                addGameResult(winnerResult, stats.general.wins)
                addGameResult(loserResult, stats.general.loss)

                stats.gameResultHistoryWinner.add(winnerResult)
                stats.gameResultHistoryLoser.add(loserResult)

                if (winnerResult.faction == Faction.RE) {
                    addGameResult(winnerResult, stats.re.total)
                    addGameResult(winnerResult, stats.re.wins)

                    addGameResult(loserResult, stats.contra.total)
                    addGameResult(loserResult, stats.contra.loss)

                    stats.gameResultHistoryRe.add(winnerResult)
                    stats.gameResultHistoryContra.add(loserResult)
                } else {
                    addGameResult(winnerResult, stats.contra.total)
                    addGameResult(winnerResult, stats.contra.wins)

                    addGameResult(loserResult, stats.re.total)
                    addGameResult(loserResult, stats.re.loss)

                    stats.gameResultHistoryRe.add(loserResult)
                    stats.gameResultHistoryContra.add(winnerResult)
                }

                if(g.gameType == GameType.SOLO){
                    if(g.winningFaction == Faction.RE){
                        addGameResult(winnerResult, stats.solo.wins)
                        addGameResult(winnerResult, stats.solo.total)
                    }else{
                        addGameResult(loserResult, stats.solo.total)
                        addGameResult(loserResult, stats.solo.loss)
                    }
                }
            }
        }

        return stats
    }

    private fun addGameResult(result: PlayerGameResult, stats: SimpleStatisticEntry) {
        stats.games += 1
        stats.tacken += result.tacken
        stats.points += result.points

    }
}
