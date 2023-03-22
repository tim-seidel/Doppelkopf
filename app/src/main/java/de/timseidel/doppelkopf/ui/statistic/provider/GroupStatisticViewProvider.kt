package de.timseidel.doppelkopf.ui.statistic.provider

import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.model.statistic.group.GroupStatistics
import de.timseidel.doppelkopf.ui.statistic.views.IStatisticViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.ColumnChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.LineChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.PieChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.SimpleTextStatisticViewWrapper
import de.timseidel.doppelkopf.model.statistic.StatisticUtil

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

        groupStatistics.memberStatistics.forEach { ms ->
            memberWinsRe.add(ms.re.wins.games)
            memberWinsContra.add(ms.contra.wins.games)
            memberLossRe.add(ms.re.loss.games)
            memberLossContra.add(ms.contra.loss.games)
            memberWinsSolo.add(ms.solo.wins.games)
            memberLossSolo.add(ms.solo.loss.games)
        }

        val historyWinner = mutableListOf<GameResult>()
        groupStatistics.sessionStatistics.forEach { ss ->
            historyWinner.addAll(ss.gameResultHistoryWinner)
        }
        val tackenDistribution =
            StatisticUtil.getTackenDistribution(historyWinner)

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
                            "Re",
                            groupStatistics.re.wins.games - groupStatistics.solo.wins.games,
                            "#${IStatisticViewWrapper.COLOR_POSITIVE_DARK}"
                        ),
                        PieChartViewWrapper.PieSliceData(
                            "Solo",
                            groupStatistics.solo.wins.games,
                            "#${IStatisticViewWrapper.COLOR_POSITIVE_LIGHT}"
                        ),
                        PieChartViewWrapper.PieSliceData(
                            "Contra",
                            groupStatistics.contra.wins.games - groupStatistics.solo.loss.games,
                            "#${IStatisticViewWrapper.COLOR_NEGATIVE_DARK}"
                        ), PieChartViewWrapper.PieSliceData(
                            "Solo",
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
                    250f
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
            )
        )
    }
}