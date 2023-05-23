package de.timseidel.doppelkopf.ui.statistic.provider

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.model.statistic.StatisticUtil
import de.timseidel.doppelkopf.model.statistic.group.GroupStatistics
import de.timseidel.doppelkopf.ui.statistic.views.ColumnChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.IStatisticViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.LineChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.PieChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.SimpleTextStatisticViewWrapper

class GroupStatisticViewProvider(private val groupStatistics: GroupStatistics) :
    IStatisticViewsProvider {
    override fun getStatisticItems(): List<IStatisticViewWrapper> {

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
                "Hier siehst du die Statistiken eurer Doppelkopfgruppe. An allen Abenden zusammen habt ihr so viele Spiele gespielt:",
                groupStatistics.general.total.games.toString()
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
            ),
            LineChartViewWrapper(
                LineChartViewWrapper.LineChartData(
                    "Tackenverlauf", "Tacken", memberTackenHistories,
                    height = 400f
                )
            ),
            LineChartViewWrapper(
                LineChartViewWrapper.LineChartData(
                    "Tackenverlauf ohne Bockrunden", "Tacken", memberTackenHistoriesWithoutBock,
                    height = 400f
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
            ColumnChartViewWrapper(
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
        )
    }
}