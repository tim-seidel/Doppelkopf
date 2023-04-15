package de.timseidel.doppelkopf.ui.statistic.provider

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.statistic.StatisticUtil
import de.timseidel.doppelkopf.model.statistic.session.SessionStatistics
import de.timseidel.doppelkopf.ui.statistic.views.ColumnChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.IStatisticViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.LineChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.PieChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.SimpleTextStatisticViewWrapper

class SessionStatisticViewsProvider(private val sessionStatistics: SessionStatistics) :
    IStatisticViewsProvider {

    override fun getStatisticItems(): List<IStatisticViewWrapper> {
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

        val tMin = listOf(
            tackenDistributionNoBockRe.min,
            tackenDistributionBockRe.min,
            tackenDistributionNoBockContra.min,
            tackenDistributionBockContra.min
        ).min()
        val tMax = listOf(
            tackenDistributionNoBockRe.min,
            tackenDistributionBockRe.min,
            tackenDistributionNoBockContra.min,
            tackenDistributionBockContra.min
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

        return listOf(
            SimpleTextStatisticViewWrapper(
                "Allgemeine Statistik",
                "Hier siehst du die Statistiken eures Doppelkopfabends. Ihr habt so viele Spiele gespielt:",
                sessionStatistics.general.total.games.toString()
            ),
            PieChartViewWrapper(
                PieChartViewWrapper.PieChartData
                    (
                    "Re oder Contra?",
                    "Verteilung der Siege von Re und Contra",
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
                            "Ndl Contra",
                            sessionStatistics.contra.wins.games - sessionStatistics.solo.loss.games,
                            "#${IStatisticViewWrapper.COLOR_NEGATIVE_DARK}"
                        ), PieChartViewWrapper.PieSliceData(
                            "Ndl Solo",
                            sessionStatistics.solo.loss.games,
                            "#${IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT}"
                        )
                    )
                )
            ),
            LineChartViewWrapper(
                LineChartViewWrapper.LineChartData(
                    "Tackenverlauf", "Tacken", playerTackenHistories,
                    height = 400f
                )
            ),
            LineChartViewWrapper(
                LineChartViewWrapper.LineChartData(
                    "Tackenverlauf ohne Bockrunden", "Tacken", playerTackenHistoriesWithoutBock,
                    height = 400f
                )
            ),
            SimpleTextStatisticViewWrapper(
                "Tacken",
                "Durchschnittlich wurden bei einem Spiel so viele Tacken verteilt:",
                sessionStatistics.general.total.getTackenPerGame().toString()
            ),
            LineChartViewWrapper(
                LineChartViewWrapper.LineChartData(
                    "Straftacken", "Straftacken", playerTackenLosses, 400f
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
            ),
            SimpleTextStatisticViewWrapper(
                "Soli",
                "So viele Soli wurden gespielt:",
                sessionStatistics.solo.total.games.toString()
            ),
            ColumnChartViewWrapper(
                ColumnChartViewWrapper.ColumnChartData(
                    "Wer ist solo?",
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
            ),

            )
    }
}