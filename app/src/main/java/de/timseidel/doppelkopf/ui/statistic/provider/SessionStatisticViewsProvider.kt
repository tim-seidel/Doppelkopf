package de.timseidel.doppelkopf.ui.statistic.provider

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.statistic.StatisticUtil
import de.timseidel.doppelkopf.model.statistic.session.SessionStatistics
import de.timseidel.doppelkopf.ui.statistic.views.ColumnChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.IStatisticViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.LineChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.PieChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.SimpleTextStatisticViewWrapper
import kotlin.math.round

class SessionStatisticViewsProvider(private val sessionStatistics: SessionStatistics) :
    IStatisticViewsProvider {

    override fun getStatisticItems(isBockrundeEnabled: Boolean): List<IStatisticViewWrapper> {
        val playerStats = sessionStatistics.playerStatistics

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
            val history = StatisticUtil.getAccumulatedStraftackenHistory(p.gameResultHistory)
            val last = if (history.isNotEmpty()) history.last() else 0
            playerTackenLosses.add(
                LineChartViewWrapper.ChartLineData(
                    "${p.player.name} ($last | ${last * 5 / 100f}€)",
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
            playerTackenLossRe.add(-1 * p.re.loss.tacken)
            playerTackenLossContra.add(-1 * p.contra.loss.tacken)
        }

        val tackenDistributionNoBockRe =
            StatisticUtil.getTackenDistribution(sessionStatistics.gameResultHistoryWinner.filter { gr -> !gr.isBockrunde && gr.faction == Faction.RE })
        val tackenDistributionBockRe =
            StatisticUtil.getTackenDistribution(sessionStatistics.gameResultHistoryWinner.filter { gr -> gr.isBockrunde && gr.faction == Faction.RE })
        val tackenDistributionNoBockContra =
            StatisticUtil.getTackenDistribution(sessionStatistics.gameResultHistoryWinner.filter { gr -> !gr.isBockrunde && gr.faction == Faction.CONTRA })
        val tackenDistributionBockContra =
            StatisticUtil.getTackenDistribution(sessionStatistics.gameResultHistoryWinner.filter { gr -> gr.isBockrunde && gr.faction == Faction.CONTRA })
        val tackenDistributionGeneralRe =
            StatisticUtil.getTackenDistribution(sessionStatistics.gameResultHistoryWinner.filter { gr -> gr.faction == Faction.RE })
        val tackenDistributionGeneralContra =
            StatisticUtil.getTackenDistribution(sessionStatistics.gameResultHistoryWinner.filter { gr -> gr.faction == Faction.CONTRA })

        val tMin = listOf(
            tackenDistributionGeneralRe.min,
            tackenDistributionGeneralContra.min
        ).min()
        val tMax = listOf(
            tackenDistributionGeneralRe.max,
            tackenDistributionGeneralContra.max
        ).max()

        val tackenDistributionIndices = (tMin..tMax).toList()

        val tackenDistributionNoBockReValuesOffsetCorrected =
            if (tackenDistributionNoBockRe.min > tMin) {
                (List(tackenDistributionNoBockRe.min - tMin) { 0 } + tackenDistributionNoBockRe.values())
            } else {
                tackenDistributionNoBockRe.values()
            }
        val tackenDistributionBockReValuesOffsetCorrected =
            if (tackenDistributionBockRe.min > tMin) {
                (List(tackenDistributionBockRe.min - tMin) { 0 } + tackenDistributionBockRe.values())
            } else {
                tackenDistributionBockRe.values()
            }
        val tackenDistributionNoBockContraValuesOffsetCorrected =
            if (tackenDistributionNoBockContra.min > tMin) {
                (List(tackenDistributionNoBockContra.min - tMin) { 0 } + tackenDistributionNoBockContra.values())
            } else {
                tackenDistributionNoBockContra.values()
            }
        val tackenDistributionBockContraValuesOffsetCorrected =
            if (tackenDistributionBockContra.min > tMin) {
                (List(tackenDistributionBockContra.min - tMin) { 0 } + tackenDistributionBockContra.values())
            } else {
                tackenDistributionBockContra.values()
            }
        val tackenDistributionGeneralReValuesOffsetCorrected =
            if (tackenDistributionGeneralRe.min > tMin) {
                (List(tackenDistributionGeneralRe.min - tMin) { 0 } + tackenDistributionGeneralRe.values())
            } else {
                tackenDistributionGeneralRe.values()
            }
        val tackenDistributionGeneralContraValuesOffsetCorrected =
            if (tackenDistributionGeneralContra.min > tMin) {
                (List(tackenDistributionGeneralContra.min - tMin) { 0 } + tackenDistributionGeneralContra.values())
            } else {
                tackenDistributionGeneralContra.values()
            }

        val percentReWin =
            if (sessionStatistics.general.total.games > 0) round(sessionStatistics.re.wins.games / sessionStatistics.general.total.games.toFloat() * 100).toInt() else 0

        val totalGamesTextStat = SimpleTextStatisticViewWrapper(
            "Allgemeine Statistik",
            "Hier siehst du die Statistiken eures Doppelkopfabends. Ihr habt so viele Spiele gespielt:",
            sessionStatistics.general.total.games.toString()
        )

        val parteiPieChart = PieChartViewWrapper(
            PieChartViewWrapper.PieChartData
                (
                "Re oder Contra?",
                "Verteilung der Siege von Re ($percentReWin%) und Contra",
                "Spiele",
                listOf(
                    PieChartViewWrapper.PieSliceData(
                        "Sieg Re",
                        sessionStatistics.re.wins.games - sessionStatistics.solo.wins.games,
                        "#${IStatisticViewWrapper.COLOR_POSITIVE_DARK}"
                    ),
                    PieChartViewWrapper.PieSliceData(
                        "Sieg Solo",
                        sessionStatistics.solo.wins.games,
                        "#${IStatisticViewWrapper.COLOR_POSITIVE_LIGHT}"
                    ),
                    PieChartViewWrapper.PieSliceData(
                        "Sieg Contra",
                        sessionStatistics.contra.wins.games - sessionStatistics.solo.loss.games,
                        "#${IStatisticViewWrapper.COLOR_NEGATIVE_DARK}"
                    ), PieChartViewWrapper.PieSliceData(
                        "Ndl Solo",
                        sessionStatistics.solo.loss.games,
                        "#${IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT}"
                    )
                )
            )
        )

        val tackenLineChart = LineChartViewWrapper(
            LineChartViewWrapper.LineChartData(
                "Tackenverlauf", "Tacken", playerTackenHistories,
                height = 400f
            )
        )

        val tackenIgnoringBockLineChart = LineChartViewWrapper(
            LineChartViewWrapper.LineChartData(
                "Tackenverlauf ohne Bockrunden", "Tacken", playerTackenHistoriesWithoutBock,
                height = 400f
            )
        )

        val averageTackenPerGameTextStat = SimpleTextStatisticViewWrapper(
            "Tacken",
            "Durchschnittlich wurden bei einem Spiel so viele Tacken verteilt:",
            sessionStatistics.general.total.getTackenPerGame().toString()
        )

        val straftackenLineChart = LineChartViewWrapper(
            LineChartViewWrapper.LineChartData(
                "Straftacken", "Straftacken", playerTackenLosses, 400f
            )
        )

        val winLossPlayerBarChart = ColumnChartViewWrapper(
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
        )

        val tackenWinLossPlayerBarChart = ColumnChartViewWrapper(
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
        )

        val tackenBarChartBockEnabled = ColumnChartViewWrapper(
            ColumnChartViewWrapper.ColumnChartData(
                "Tackenverteilung", "Tacken", "Spiele",
                listOf(
                    ColumnChartViewWrapper.ColumnSeriesData(
                        "Tacken Re",
                        listOf(
                            ColumnChartViewWrapper.ColumnSeriesStackData(
                                "Normal | Re",
                                IStatisticViewWrapper.COLOR_POSITIVE_LIGHT.replace("#", ""),
                                tackenDistributionNoBockReValuesOffsetCorrected,
                            ),
                            ColumnChartViewWrapper.ColumnSeriesStackData(
                                "Bock | Re",
                                IStatisticViewWrapper.COLOR_POSITIVE_DARK.replace("#", ""),
                                tackenDistributionBockReValuesOffsetCorrected,
                            ),
                        )
                    ),
                    ColumnChartViewWrapper.ColumnSeriesData(
                        "Tacken Contra",
                        listOf(
                            ColumnChartViewWrapper.ColumnSeriesStackData(
                                "Normal | Con",
                                IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT.replace("#", ""),
                                tackenDistributionNoBockContraValuesOffsetCorrected,
                            ),
                            ColumnChartViewWrapper.ColumnSeriesStackData(
                                "Bock | Con",
                                IStatisticViewWrapper.COLOR_NEGATIVE_DARK.replace("#", ""),
                                tackenDistributionBockContraValuesOffsetCorrected,
                            )
                        )
                    )
                ),
                tackenDistributionIndices.map { i -> i.toString() },
                true,
                350f
            )
        )

        val tackenBarChartBockDisabled = ColumnChartViewWrapper(
            ColumnChartViewWrapper.ColumnChartData(
                "Tackenverteilung", "Tacken", "Spiele",
                listOf(
                    ColumnChartViewWrapper.ColumnSeriesData(
                        "Tacken Re",
                        listOf(
                            ColumnChartViewWrapper.ColumnSeriesStackData(
                                "Sieg Re",
                                IStatisticViewWrapper.COLOR_POSITIVE_LIGHT.replace("#", ""),
                                tackenDistributionGeneralReValuesOffsetCorrected,
                            )
                        )
                    ),
                    ColumnChartViewWrapper.ColumnSeriesData(
                        "Tacken Contra",
                        listOf(
                            ColumnChartViewWrapper.ColumnSeriesStackData(
                                "Sieg Con",
                                IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT.replace("#", ""),
                                tackenDistributionGeneralContraValuesOffsetCorrected,
                            )
                        )
                    )
                ),
                tackenDistributionIndices.map { i -> i.toString() },
                true,
                350f
            )
        )

        val soloTextStat = SimpleTextStatisticViewWrapper(
            "Soli",
            "So viele Soli wurden gespielt:",
            sessionStatistics.solo.total.games.toString()
        )

        val soloPlayerBarChart = ColumnChartViewWrapper(
            ColumnChartViewWrapper.ColumnChartData(
                "Solostatistiken",
                "",
                "Spiele",
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
                height = 250f
            )
        )

        if (isBockrundeEnabled) {
            return listOf(
                totalGamesTextStat,
                parteiPieChart,
                tackenLineChart,
                tackenIgnoringBockLineChart,
                straftackenLineChart,
                winLossPlayerBarChart,
                averageTackenPerGameTextStat,
                tackenWinLossPlayerBarChart,
                tackenBarChartBockEnabled,
                soloTextStat,
                soloPlayerBarChart
            )
        } else {
            return listOf(
                totalGamesTextStat,
                parteiPieChart,
                tackenLineChart,
                straftackenLineChart,
                winLossPlayerBarChart,
                averageTackenPerGameTextStat,
                tackenWinLossPlayerBarChart,
                tackenBarChartBockDisabled,
                soloTextStat,
                soloPlayerBarChart
            )
        }
    }
}