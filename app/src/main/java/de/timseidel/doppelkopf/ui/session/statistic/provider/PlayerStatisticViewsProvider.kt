package de.timseidel.doppelkopf.ui.session.statistic.provider

import de.timseidel.doppelkopf.model.statistic.PlayerStatistic
import de.timseidel.doppelkopf.ui.session.statistic.IStatisticViewWrapper
import de.timseidel.doppelkopf.ui.session.statistic.views.ColumnChartViewWrapper
import de.timseidel.doppelkopf.ui.session.statistic.views.LineChartViewWrapper
import de.timseidel.doppelkopf.ui.session.statistic.views.PieChartViewWrapper
import de.timseidel.doppelkopf.ui.session.statistic.views.SimpleTextStatisticViewWrapper
import de.timseidel.doppelkopf.util.StatisticUtil
import kotlin.math.abs

class PlayerStatisticViewsProvider(private var stats: PlayerStatistic) : IStatisticViewsProvider {
    override fun getStatisticItems(): List<IStatisticViewWrapper> {
        val tackenDistribution = StatisticUtil.getTackenDistribution(stats.gameResultHistory, null)

        val partnerNames = mutableListOf<String>()
        val partnerNamesWithTacken = mutableListOf<String>()

        val partnerWinsRe = mutableListOf<Int>()
        val partnerWinsContra = mutableListOf<Int>()
        val partnerLossRe = mutableListOf<Int>()
        val partnerLossContra = mutableListOf<Int>()

        val partnerTackenWinsRe = mutableListOf<Int>()
        val partnerTackenWinsContra = mutableListOf<Int>()
        val partnerTackenLossRe = mutableListOf<Int>()
        val partnerTackenLossContra = mutableListOf<Int>()

        stats.partners.values.forEach { p ->
            partnerNames.add(p.player.name)
            partnerNamesWithTacken.add("${p.player.name} (${p.general.total.tacken})")

            partnerWinsRe.add(p.re.wins.games)
            partnerWinsContra.add(p.contra.wins.games)
            partnerLossRe.add(p.re.loss.games)
            partnerLossContra.add(p.contra.loss.games)

            partnerTackenWinsRe.add(p.re.wins.tacken)
            partnerTackenWinsContra.add(p.contra.wins.tacken)
            partnerTackenLossRe.add(-1*p.re.loss.tacken)
            partnerTackenLossContra.add(-1*p.contra.loss.tacken)
        }

        return listOf(
            SimpleTextStatisticViewWrapper(
                "Allgemeine Statistik",
                "Hier siehst du die Statistiken von ${stats.player.name}. Er/Sie hab so viele Spiele gespielt:",
                stats.general.total.games.toString()
            ),
            PieChartViewWrapper(
                PieChartViewWrapper.PieChartData(
                    "Siege und Niederlagen",
                    "Siege: ${stats.general.wins.games}, Niederlagen: ${stats.general.loss.games}",
                    "Spiele",
                    listOf(
                        PieChartViewWrapper.PieSliceData(
                            "S Re",
                            stats.re.wins.games,
                            "#${IStatisticViewWrapper.COLOR_POSITIVE_DARK}"
                        ),
                        PieChartViewWrapper.PieSliceData(
                            "S Con",
                            stats.contra.wins.games,
                            "#${IStatisticViewWrapper.COLOR_POSITIVE_LIGHT}"
                        ),
                        PieChartViewWrapper.PieSliceData(
                            "N Re",
                            abs(stats.re.loss.games),
                            "#${IStatisticViewWrapper.COLOR_NEGATIVE_DARK}"
                        ),
                        PieChartViewWrapper.PieSliceData(
                            "N Con",
                            abs(stats.contra.loss.games),
                            "#${IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT}"
                        )
                    )
                )
            ),
            SimpleTextStatisticViewWrapper(
                "Aktuelle Tacken",
                "${stats.player.name} steht aktuell bei dieser Tackenanzahl:",
                stats.general.total.tacken.toString()
            ),
            LineChartViewWrapper(
                LineChartViewWrapper.LineChartData(
                    "Tackenverlauf", "Tacken", listOf(
                        LineChartViewWrapper.ChartLineData(
                            stats.player.name,
                            StatisticUtil.getAccumulatedTackenHistory(stats.gameResultHistory)
                        )
                    )
                )
            ),
            PieChartViewWrapper(
                PieChartViewWrapper.PieChartData(
                    "Tacken bei S/N",
                    "S: ${stats.general.wins.tacken}, N: ${stats.general.loss.tacken}: Gesamt: ${stats.general.total.tacken}",
                    "Tacken",
                    listOf(
                        PieChartViewWrapper.PieSliceData(
                            "Sieg Re",
                            stats.re.wins.tacken,
                            "#${IStatisticViewWrapper.COLOR_POSITIVE_DARK}"
                        ),
                        PieChartViewWrapper.PieSliceData(
                            "Sieg Con",
                            stats.contra.wins.tacken,
                            "#${IStatisticViewWrapper.COLOR_POSITIVE_LIGHT}"
                        ),
                        PieChartViewWrapper.PieSliceData(
                            "Ndl Re",
                            abs(stats.re.loss.tacken),
                            "#${IStatisticViewWrapper.COLOR_NEGATIVE_DARK}"
                        ),
                        PieChartViewWrapper.PieSliceData(
                            "Ndl Con",
                            abs(stats.contra.loss.tacken),
                            "#${IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT}"
                        )
                    )
                )
            ),
            SimpleTextStatisticViewWrapper(
                "Schuldschein",
                "${stats.player.name} muss so viele verlorene Tacken bezahlen:",
                abs(stats.general.loss.tacken).toString()
            ),
            ColumnChartViewWrapper(
                ColumnChartViewWrapper.ColumnChartData(
                    "Tackenverteilung", "Tacken", "Spiele",
                    listOf(
                        ColumnChartViewWrapper.ColumnSeriesData(
                            "Tacken",
                            listOf(
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "Tackenanzahl", IStatisticViewWrapper.COLOR_NEURAL.replace("#", ""),
                                    tackenDistribution.values(),
                                )
                            )
                        )
                    ),
                    tackenDistribution.indices().map { i -> i.toString() }
                )
            ),
            SimpleTextStatisticViewWrapper(
                "Siegerausbeute",
                "Wenn ${stats.player.name} gewinnt, bekommt er im Schnitt diese Tacken:",
                "%.2f".format(stats.general.wins.getTackenPerGame())
            ),
            SimpleTextStatisticViewWrapper(
                "Abgabe",
                "Wenn ${stats.player.name} verliert, kostet das im Schnitt etwa diese Tacken:",
                "%.2f".format(stats.general.loss.getTackenPerGame())
            ),
            ColumnChartViewWrapper(
                ColumnChartViewWrapper.ColumnChartData(
                    "Durchschnittliche Tacken", "", "Tacken pro Spiel",
                    listOf(
                        ColumnChartViewWrapper.ColumnSeriesData(
                            "Siege",
                            listOf(
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "Tacken",
                                    IStatisticViewWrapper.COLOR_NEURAL.replace("#", ""),
                                    listOf(
                                        stats.re.wins.getTackenPerGame(),
                                        stats.contra.wins.getTackenPerGame(),
                                        stats.re.loss.getTackenPerGame(),
                                        stats.contra.loss.getTackenPerGame()
                                    )
                                )
                            )
                        )
                    ),
                    listOf("Sieg Re", "Sieg Con", "Ndl Re", "Ndl Con")
                )
            ),
            //TODO: Evtl. wie bei der Kickerapp Horizonzal?
            ColumnChartViewWrapper(
                ColumnChartViewWrapper.ColumnChartData(
                    "Spiele mit Partnern", "", "Spiele",
                    listOf(
                        ColumnChartViewWrapper.ColumnSeriesData(
                            "Siege",
                            listOf(
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "Siege Re",
                                    IStatisticViewWrapper.COLOR_POSITIVE_DARK,
                                    partnerWinsRe
                                ),
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "Siege Contra",
                                    IStatisticViewWrapper.COLOR_POSITIVE_LIGHT,
                                    partnerWinsContra
                                )
                            )
                        ),
                        ColumnChartViewWrapper.ColumnSeriesData(
                            "Niederlagen",
                            listOf(
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "Ndl Re",
                                    IStatisticViewWrapper.COLOR_NEGATIVE_DARK,
                                    partnerLossRe
                                ),
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "Ndl Contra",
                                    IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT,
                                    partnerLossContra
                                )
                            )
                        )
                    ),
                    partnerNames
                )
            ),
            ColumnChartViewWrapper(
                ColumnChartViewWrapper.ColumnChartData(
                    "Tacken mit Partnern", "", "Tacken",
                    listOf(
                        ColumnChartViewWrapper.ColumnSeriesData(
                            "Siege",
                            listOf(
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "bei Siegen Re",
                                    IStatisticViewWrapper.COLOR_POSITIVE_DARK,
                                    partnerTackenWinsRe
                                ),
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "bei Siegen Contra",
                                    IStatisticViewWrapper.COLOR_POSITIVE_LIGHT,
                                    partnerTackenWinsContra
                                )
                            )
                        ),
                        ColumnChartViewWrapper.ColumnSeriesData(
                            "Niederlagen",
                            listOf(
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "bei Ndl Re",
                                    IStatisticViewWrapper.COLOR_NEGATIVE_DARK,
                                    partnerTackenLossRe
                                ),
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "bei Ndl Contra",
                                    IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT,
                                    partnerTackenLossContra
                                )
                            )
                        )
                    ),
                    partnerNamesWithTacken
                )
            )
        )
    }
}