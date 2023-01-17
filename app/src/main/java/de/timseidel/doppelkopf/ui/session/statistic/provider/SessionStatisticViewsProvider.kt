package de.timseidel.doppelkopf.ui.session.statistic.provider

import de.timseidel.doppelkopf.model.statistic.PlayerStatisticsCalculator
import de.timseidel.doppelkopf.model.statistic.SessionStatisticsCalculator
import de.timseidel.doppelkopf.ui.session.statistic.IStatisticViewWrapper
import de.timseidel.doppelkopf.ui.session.statistic.views.ColumnChartViewWrapper
import de.timseidel.doppelkopf.ui.session.statistic.views.LineChartViewWrapper
import de.timseidel.doppelkopf.ui.session.statistic.views.SimpleTextStatisticViewWrapper
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.StatisticUtil

class SessionStatisticViewsProvider : IStatisticViewsProvider {

    override fun getStatisticItems(): List<IStatisticViewWrapper> {
        val playerStats = PlayerStatisticsCalculator().calculatePlayerStatistic(
            DokoShortAccess.getPlayerCtrl().getPlayers(),
            DokoShortAccess.getGameCtrl().getGames()
        )

        val playerTackenHistories = mutableListOf<LineChartViewWrapper.ChartLineData>()
        playerStats.forEach { p ->
            val history = StatisticUtil.getAccumulatedTackenHistory(p.gameResultHistory)
            val last = if (history.isNotEmpty()) history.last() else 0
            playerTackenHistories.add(
                LineChartViewWrapper.ChartLineData(
                    "${p.player.name} ($last)",
                    history
                )
            )
        }

        val playerTackenHistoriesWithoutBock = mutableListOf<LineChartViewWrapper.ChartLineData>()
        playerStats.forEach { p ->
            val history = StatisticUtil.getAccumulatedTackenHistoryWithoutBock(p.gameResultHistory)
            val last = if (history.isNotEmpty()) history.last() else 0
            playerTackenHistoriesWithoutBock.add(
                LineChartViewWrapper.ChartLineData(
                    "${p.player.name} ($last)",
                    history
                )
            )
        }

        val playerTackenLosses = mutableListOf<LineChartViewWrapper.ChartLineData>()
        playerStats.forEach { p ->
            val history = StatisticUtil.getAccumulatedTackenLosses(p.gameResultHistory)
            val last = if (history.isNotEmpty()) history.last() else 0
            playerTackenLosses.add(
                LineChartViewWrapper.ChartLineData(
                    "${p.player.name} ($last)",
                    history
                )
            )
        }

        val playerNames = playerStats.map { p -> p.player.name }

        val playerWinsRe = mutableListOf<Int>()
        val playerWinsContra = mutableListOf<Int>()
        val playerLossRe = mutableListOf<Int>()
        val playerLossContra = mutableListOf<Int>()
        val playerWinsSolo = mutableListOf<Int>()
        val playerLossSolo = mutableListOf<Int>()

        val playerTackenWinsRe = mutableListOf<Int>()
        val playerTackenWinsContra = mutableListOf<Int>()
        val playerTackenLossRe = mutableListOf<Int>()
        val playerTackenLossContra = mutableListOf<Int>()


        playerStats.forEach { p ->
            playerWinsRe.add(p.re.wins.games)
            playerWinsContra.add(p.contra.wins.games)
            playerLossRe.add(p.re.loss.games)
            playerLossContra.add(p.contra.loss.games)
            playerWinsSolo.add(p.solo.wins.games)
            playerLossSolo.add(p.solo.loss.games)

            playerTackenWinsRe.add(p.re.wins.tacken)
            playerTackenWinsContra.add(p.contra.wins.tacken)
            playerTackenLossRe.add(p.re.loss.tacken)
            playerTackenLossContra.add(p.contra.loss.tacken)
        }

        val sessionStats = SessionStatisticsCalculator().calculateStatistics(
            DokoShortAccess.getGameCtrl().getGames()
        )

        val tackenDistribution =
            StatisticUtil.getTackenDistribution(sessionStats.gameResultHistoryWinner)

        return listOf(
            SimpleTextStatisticViewWrapper(
                "Allgemeine Statistik",
                "Hier siehst du die Statistiken eures Doppelkopfabends. Ihr habt so viele Spiele gespielt:",
                DokoShortAccess.getGameCtrl().getGames().size.toString()
            ),
            LineChartViewWrapper(
                LineChartViewWrapper.LineChartData(
                    "Tackenverlauf", "Tacken", playerTackenHistories
                )
            ),
            LineChartViewWrapper(
                LineChartViewWrapper.LineChartData(
                    "Tackenverlauf ohne Bockrunden", "Tacken", playerTackenHistoriesWithoutBock
                )
            ),
            SimpleTextStatisticViewWrapper(
                "Tacken",
                "Durchschnittlich wurden bei einem Spiel so viele Tacken verteilt:",
                sessionStats.general.total.getTackenPerGame().toString()
            ),
            LineChartViewWrapper(
                LineChartViewWrapper.LineChartData(
                    "Straftacken", "Tacken", playerTackenLosses, 400f
                )
            ),
            ColumnChartViewWrapper(
                ColumnChartViewWrapper.ColumnChartData(
                    "Siege/Niederlagen", "", "Spiele",
                    listOf(
                        ColumnChartViewWrapper.ColumnSeriesData(
                            "Siege",
                            listOf(
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "Siege Re",
                                    IStatisticViewWrapper.COLOR_POSITIVE_DARK,
                                    playerWinsRe
                                ),
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "Siege Contra",
                                    IStatisticViewWrapper.COLOR_POSITIVE_LIGHT,
                                    playerWinsContra
                                )
                            )
                        ),
                        ColumnChartViewWrapper.ColumnSeriesData(
                            "Niederlagen",
                            listOf(
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "Ndl Re",
                                    IStatisticViewWrapper.COLOR_NEGATIVE_DARK,
                                    playerLossRe
                                ),
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "Ndl Contra",
                                    IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT,
                                    playerLossContra
                                )
                            )
                        )
                    ),
                    playerNames
                )
            ),
            ColumnChartViewWrapper(
                ColumnChartViewWrapper.ColumnChartData(
                    "Tacken bei S/N", "", "Tacken",
                    listOf(
                        ColumnChartViewWrapper.ColumnSeriesData(
                            "Siege",
                            listOf(
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "bei Sieg Re",
                                    IStatisticViewWrapper.COLOR_POSITIVE_DARK,
                                    playerTackenWinsRe
                                ),
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "bei Sieg Contra",
                                    IStatisticViewWrapper.COLOR_POSITIVE_LIGHT,
                                    playerTackenWinsContra
                                )
                            )
                        ),
                        ColumnChartViewWrapper.ColumnSeriesData(
                            "Niederlagen",
                            listOf(
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "bei Ndl Re",
                                    IStatisticViewWrapper.COLOR_NEGATIVE_DARK,
                                    playerTackenLossRe
                                ),
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "bei Ndl Contra",
                                    IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT,
                                    playerTackenLossContra
                                )
                            )
                        )
                    ),
                    playerNames
                )
            ),
            ColumnChartViewWrapper(
                ColumnChartViewWrapper.ColumnChartData(
                    "Tackenverteilung", "Tacken", "Spiele",
                    listOf(
                        ColumnChartViewWrapper.ColumnSeriesData(
                            "Tacken",
                            listOf(
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "Tackenanzahl",
                                    IStatisticViewWrapper.COLOR_NEURAL.replace("#", ""),
                                    tackenDistribution.values(),
                                )
                            )
                        )
                    ),
                    tackenDistribution.indices().map { i -> i.toString() }
                )
            ),
            SimpleTextStatisticViewWrapper(
                "Soli",
                "So viele Soli wurden gespielt:",
                sessionStats.solo.total.games.toString()
            ),
            ColumnChartViewWrapper(
                ColumnChartViewWrapper.ColumnChartData(
                    "Wer war heute solo?",
                    "Spiele",
                    "",
                    listOf(
                        ColumnChartViewWrapper.ColumnSeriesData(
                            "Siege",
                            listOf(
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "Siege",
                                    IStatisticViewWrapper.COLOR_POSITIVE_DARK.replace("#", ""),
                                    playerWinsSolo
                                )
                            )
                        ),
                        ColumnChartViewWrapper.ColumnSeriesData(
                            "Niederlagen",
                            listOf(
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "Niederlagen",
                                    IStatisticViewWrapper.COLOR_NEGATIVE_DARK.replace("#", ""),
                                    playerLossSolo
                                )
                            )
                        )
                    ),
                    playerNames,
                    250f
                )
            )
        )
    }
}