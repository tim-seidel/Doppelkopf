package de.timseidel.doppelkopf.ui.statistic.provider

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.model.statistic.StatisticUtil
import de.timseidel.doppelkopf.model.statistic.group.GroupStatistics
import de.timseidel.doppelkopf.model.statistic.session.MemberSessionStatistic
import de.timseidel.doppelkopf.ui.statistic.views.ColumnChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.IStatisticViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.LineChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.PieChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.SimpleTextStatisticViewWrapper
import kotlin.math.round

class GroupStatisticViewProvider(private val groupStatistics: GroupStatistics) :
    IStatisticViewsProvider {
    override fun getStatisticItems(isBockrundeEnabled: Boolean): List<IStatisticViewWrapper> {

        val memberTackenHistories = mutableListOf<LineChartViewWrapper.ChartLineData>()
        groupStatistics.memberStatistics.forEach { ms ->
            val history = StatisticUtil.getAccumulatedTackenHistory(ms.gameResultHistory)
            val last = if (history.isNotEmpty()) history.last() else 0
            memberTackenHistories.add(
                LineChartViewWrapper.ChartLineData(
                    "${ms.member.name} ($last)",
                    history
                )
            )
        }

        val memberTackenHistoriesWithoutBock = mutableListOf<LineChartViewWrapper.ChartLineData>()
        groupStatistics.memberStatistics.forEach { ms ->
            val history = StatisticUtil.getAccumulatedTackenHistoryWithoutBock(ms.gameResultHistory)
            val last = if (history.isNotEmpty()) history.last() else 0
            memberTackenHistoriesWithoutBock.add(
                LineChartViewWrapper.ChartLineData(
                    "${ms.member.name} ($last)",
                    history
                )
            )
        }

        var totalNegativeTacken = 0
        groupStatistics.sessionStatistics.forEach { ss ->
            totalNegativeTacken += 2 * (ss.gameResultHistoryLoser.sumOf { gr -> if (gr.tacken < 0) -1 * gr.tacken else 0 } + ss.gameResultHistoryWinner.sumOf { gr -> if (gr.tacken < 0) -1 * gr.tacken else 0 })
        }

        val memberNames = groupStatistics.memberStatistics.map { m -> m.member.name }

        val memberWinsRe = mutableListOf<Int>()
        val memberWinsContra = mutableListOf<Int>()
        val memberLossRe = mutableListOf<Int>()
        val memberLossContra = mutableListOf<Int>()
        val memberWinsSolo = mutableListOf<Int>()
        val memberLossSolo = mutableListOf<Int>()

        val memberTackenWinsWe = mutableListOf<Int>()
        val memberTackenWinsContra = mutableListOf<Int>()
        val memberTackenLossRe = mutableListOf<Int>()
        val memberTackenLossContra = mutableListOf<Int>()

        groupStatistics.memberStatistics.forEach { ms ->
            memberWinsRe.add(ms.re.wins.games)
            memberWinsContra.add(ms.contra.wins.games)
            memberLossRe.add(ms.re.loss.games)
            memberLossContra.add(ms.contra.loss.games)
            memberWinsSolo.add(ms.solo.wins.games)
            memberLossSolo.add(ms.solo.loss.games)

            memberTackenWinsWe.add(ms.re.wins.tacken)
            memberTackenWinsContra.add(ms.contra.wins.tacken)
            memberTackenLossRe.add(-1 * ms.re.loss.tacken)
            memberTackenLossContra.add(-1 * ms.contra.loss.tacken)
        }

        val historyWinner = mutableListOf<GameResult>()
        groupStatistics.sessionStatistics.forEach { ss ->
            historyWinner.addAll(ss.gameResultHistoryWinner)
        }

        val tackenDistributionNoBockRe =
            StatisticUtil.getTackenDistribution(historyWinner.filter { gr -> !gr.isBockrunde && gr.faction == Faction.RE })
        val tackenDistributionBockRe =
            StatisticUtil.getTackenDistribution(historyWinner.filter { gr -> gr.isBockrunde && gr.faction == Faction.RE })
        val tackenDistributionNoBockContra =
            StatisticUtil.getTackenDistribution(historyWinner.filter { gr -> !gr.isBockrunde && gr.faction == Faction.CONTRA })
        val tackenDistributionBockContra =
            StatisticUtil.getTackenDistribution(historyWinner.filter { gr -> gr.isBockrunde && gr.faction == Faction.CONTRA })
        val tackenDistributionGeneralRe =
            StatisticUtil.getTackenDistribution(historyWinner.filter { gr -> gr.faction == Faction.RE })
        val tackenDistributionGeneralContra =
            StatisticUtil.getTackenDistribution(historyWinner.filter { gr -> gr.faction == Faction.CONTRA })

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
            if (groupStatistics.general.total.games > 0) round(groupStatistics.re.wins.games / groupStatistics.general.total.games.toFloat() * 100).toInt() else 0

        var maxTackenResult: MemberSessionStatistic? = null
        groupStatistics.sessionStatistics.forEach { ss ->
            val maxSessionTacken = ss.memberSessionStatistics.maxByOrNull { ps -> ps.general.total.tacken }
            if (maxSessionTacken != null) {
                if (maxTackenResult == null || maxSessionTacken.general.total.tacken > maxTackenResult!!.general.total.tacken) {
                    maxTackenResult = maxSessionTacken
                }
            }
        }

        var minTackenResult: MemberSessionStatistic? = null
        groupStatistics.sessionStatistics.forEach { ss ->
            val minSessionTacken = ss.memberSessionStatistics.minByOrNull { ps -> ps.general.total.tacken }
            if (minSessionTacken != null) {
                if (minTackenResult == null || minSessionTacken.general.total.tacken < minTackenResult!!.general.total.tacken) {
                    minTackenResult = minSessionTacken
                }
            }
        }

        val totalGamesTextStat = SimpleTextStatisticViewWrapper(
            "Allgemeine Statistik",
            "Hier siehst du die Statistiken eurer Doppelkopfgruppe. An allen Abenden zusammen habt ihr so viele Spiele gespielt:",
            groupStatistics.general.total.games.toString()
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
                        groupStatistics.re.wins.games - groupStatistics.solo.wins.games,
                        "#${IStatisticViewWrapper.COLOR_POSITIVE_DARK}"
                    ),
                    PieChartViewWrapper.PieSliceData(
                        "Sieg Solo",
                        groupStatistics.solo.wins.games,
                        "#${IStatisticViewWrapper.COLOR_POSITIVE_LIGHT}"
                    ),
                    PieChartViewWrapper.PieSliceData(
                        "Sieg Contra",
                        groupStatistics.contra.wins.games - groupStatistics.solo.loss.games,
                        "#${IStatisticViewWrapper.COLOR_NEGATIVE_DARK}"
                    ), PieChartViewWrapper.PieSliceData(
                        "Ndl Solo",
                        groupStatistics.solo.loss.games,
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

        val totalStraftackenTextStat = SimpleTextStatisticViewWrapper(
            "Straftacken",
            "Insgesamt wurden so viele Straftacken verteilt:",
            totalNegativeTacken.toString()
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

        val averageTackenPerGameTextStat = SimpleTextStatisticViewWrapper(
            "Durchschnittliche Tacken",
            "Die durchschnittliche Anzahl an Tacken, die pro Spiel erzielt wird:",
            "%.2f".format(groupStatistics.general.total.getTackenPerGame())
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
                                memberTackenWinsWe
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
                                IStatisticViewWrapper.COLOR_POSITIVE_DARK.replace("#", ""),
                                tackenDistributionGeneralReValuesOffsetCorrected,
                            )
                        )
                    ),
                    ColumnChartViewWrapper.ColumnSeriesData(
                        "Tacken Contra",
                        listOf(
                            ColumnChartViewWrapper.ColumnSeriesStackData(
                                "Sieg Contra",
                                IStatisticViewWrapper.COLOR_NEGATIVE_DARK.replace("#", ""),
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
            groupStatistics.solo.total.games.toString()
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

        val maxTackenResultTextStat = SimpleTextStatisticViewWrapper(
            "Bestes Ergebnis",
            "Das höchste erzielte Tackenergebnis hat ${maxTackenResult?.member?.name} erzielt:",
            "${maxTackenResult?.general?.total?.tacken}"
        )

        val minTackenResultTextStat = SimpleTextStatisticViewWrapper(
            "Schlechtestes Ergebnis",
            "Das niedrigste erzielte Tackenergebnis hat ${minTackenResult?.member?.name} erzielt:",
            "${minTackenResult?.general?.total?.tacken}"
        )

        if (isBockrundeEnabled) {
            return listOf(
                totalGamesTextStat,
                parteiPieChart,
                tackenLineChart,
                tackenIgnoringBockLineChart,
                maxTackenResultTextStat,
                minTackenResultTextStat,
                winLossPlayerBarChart,
                averageTackenPerGameTextStat,
                tackenWinLossPlayerBarChart,
                totalStraftackenTextStat,
                tackenBarChartBockEnabled,
                soloTextStat,
                soloPlayerBarChart
            )
        } else {
            return listOf(
                totalGamesTextStat,
                parteiPieChart,
                tackenLineChart,
                maxTackenResultTextStat,
                minTackenResultTextStat,
                winLossPlayerBarChart,
                averageTackenPerGameTextStat,
                tackenWinLossPlayerBarChart,
                totalStraftackenTextStat,
                tackenBarChartBockDisabled,
                soloTextStat,
                soloPlayerBarChart
            )
        }
    }
}