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
        val memberSessionStats = sessionStatistics.memberSessionStatistics

        val memberTackenHistories = mutableListOf<LineChartViewWrapper.ChartLineData>()
        memberSessionStats.forEach { p ->
            val history = StatisticUtil.getAccumulatedTackenHistory(p.gameResultHistory)
            val last = if (history.isNotEmpty()) history.last() else 0
            memberTackenHistories.add(
                LineChartViewWrapper.ChartLineData(
                    "${p.member.name} ($last)",
                    history
                )
            )
        }

        val memberTackenHistoriesWithoutBock = mutableListOf<LineChartViewWrapper.ChartLineData>()
        memberSessionStats.forEach { p ->
            val history = StatisticUtil.getAccumulatedTackenHistoryWithoutBock(p.gameResultHistory)
            val last = if (history.isNotEmpty()) history.last() else 0
            memberTackenHistoriesWithoutBock.add(
                LineChartViewWrapper.ChartLineData(
                    "${p.member.name} ($last)",
                    history
                )
            )
        }

        val memberTackenLosses = mutableListOf<LineChartViewWrapper.ChartLineData>()
        memberSessionStats.forEach { p ->
            val history = StatisticUtil.getAccumulatedStraftackenHistory(p.gameResultHistory)
            val last = if (history.isNotEmpty()) history.last() else 0
            memberTackenLosses.add(
                LineChartViewWrapper.ChartLineData(
                    "${p.member.name} ($last | ${last * 5 / 100f}€)",
                    history
                )
            )
        }

        val totalNegativeTacken =
            2 * (sessionStatistics.gameResultHistoryLoser.sumOf { gr -> if (gr.tacken < 0) -1 * gr.tacken else 0 } + sessionStatistics.gameResultHistoryWinner.sumOf { gr -> if (gr.tacken < 0) -1 * gr.tacken else 0 })

        val memberNames = memberSessionStats.map { p -> p.member.name }

        val memberWinsRe = mutableListOf<Int>()
        val memberWinsContra = mutableListOf<Int>()
        val memberLossRe = mutableListOf<Int>()
        val memberLossContra = mutableListOf<Int>()
        val memberWinsSolo = mutableListOf<Int>()
        val memberLossSolo = mutableListOf<Int>()

        val memberTackenWinsRe = mutableListOf<Int>()
        val memberTackenWinsContra = mutableListOf<Int>()
        val memberTackenLossRe = mutableListOf<Int>()
        val memberTackenLossContra = mutableListOf<Int>()

        memberSessionStats.forEach { p ->
            memberWinsRe.add(p.re.wins.games)
            memberWinsContra.add(p.contra.wins.games)
            memberLossRe.add(p.re.loss.games)
            memberLossContra.add(p.contra.loss.games)
            memberWinsSolo.add(p.solo.wins.games)
            memberLossSolo.add(p.solo.loss.games)

            memberTackenWinsRe.add(p.re.wins.tacken)
            memberTackenWinsContra.add(p.contra.wins.tacken)
            memberTackenLossRe.add(-1 * p.re.loss.tacken)
            memberTackenLossContra.add(-1 * p.contra.loss.tacken)
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
                "Tackenverlauf", "Tacken", memberTackenHistories,
                height = 400f
            )
        )

        val tackenIgnoringBockLineChart = LineChartViewWrapper(
            LineChartViewWrapper.LineChartData(
                "Tackenverlauf ohne Bockrunden", "Tacken", memberTackenHistoriesWithoutBock,
                height = 400f
            )
        )

        val averageTackenPerGameTextStat = SimpleTextStatisticViewWrapper(
            "Tacken",
            "Durchschnittlich wurden bei einem Spiel so viele Tacken verteilt:",
            sessionStatistics.general.total.getTackenPerGame().toString()
        )

        val totalStraftackenTextStat = SimpleTextStatisticViewWrapper(
            "Straftacken",
            "Insgesamt wurden so viele Straftacken verteilt:",
            totalNegativeTacken.toString()
        )

        val straftackenLineChart = LineChartViewWrapper(
            LineChartViewWrapper.LineChartData(
                "Straftackensammlung", "Straftacken", memberTackenLosses, 400f
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
                                memberWinsRe
                            ),
                            ColumnChartViewWrapper.ColumnSeriesStackData(
                                "Siege Contra",
                                IStatisticViewWrapper.COLOR_POSITIVE_LIGHT,
                                memberWinsContra
                            )
                        )
                    ),
                    ColumnChartViewWrapper.ColumnSeriesData(
                        "Niederlagen",
                        listOf(
                            ColumnChartViewWrapper.ColumnSeriesStackData(
                                "Ndl Re",
                                IStatisticViewWrapper.COLOR_NEGATIVE_DARK,
                                memberLossRe
                            ),
                            ColumnChartViewWrapper.ColumnSeriesStackData(
                                "Ndl Contra",
                                IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT,
                                memberLossContra
                            )
                        )
                    )
                ),
                memberNames
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
                                memberTackenWinsRe
                            ),
                            ColumnChartViewWrapper.ColumnSeriesStackData(
                                "bei Sieg Contra",
                                IStatisticViewWrapper.COLOR_POSITIVE_LIGHT,
                                memberTackenWinsContra
                            )
                        )
                    ),
                    ColumnChartViewWrapper.ColumnSeriesData(
                        "Niederlagen",
                        listOf(
                            ColumnChartViewWrapper.ColumnSeriesStackData(
                                "bei Ndl Re",
                                IStatisticViewWrapper.COLOR_NEGATIVE_DARK,
                                memberTackenLossRe
                            ),
                            ColumnChartViewWrapper.ColumnSeriesStackData(
                                "bei Ndl Contra",
                                IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT,
                                memberTackenLossContra
                            )
                        )
                    )
                ),
                memberNames
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
                                memberWinsSolo
                            )
                        )
                    ),
                    ColumnChartViewWrapper.ColumnSeriesData(
                        "Niederlagen",
                        listOf(
                            ColumnChartViewWrapper.ColumnSeriesStackData(
                                "Niederlagen",
                                IStatisticViewWrapper.COLOR_NEGATIVE_DARK.replace("#", ""),
                                memberLossSolo
                            )
                        )
                    )
                ),
                memberNames,
                height = 250f
            )
        )

        if (isBockrundeEnabled) {
            return listOf(
                totalGamesTextStat,
                parteiPieChart,
                tackenLineChart,
                tackenIgnoringBockLineChart,
                totalStraftackenTextStat,
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
                totalStraftackenTextStat,
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