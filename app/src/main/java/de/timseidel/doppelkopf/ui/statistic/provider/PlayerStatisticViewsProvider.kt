package de.timseidel.doppelkopf.ui.statistic.provider

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.statistic.session.PlayerStatistic
import de.timseidel.doppelkopf.ui.statistic.views.IStatisticViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.ColumnChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.LineChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.PieChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.ScatterChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.SimpleTextStatisticViewWrapper
import de.timseidel.doppelkopf.model.statistic.StatisticUtil
import kotlin.math.abs

class PlayerStatisticViewsProvider(private var stats: PlayerStatistic) : IStatisticViewsProvider {
    override fun getStatisticItems(): List<IStatisticViewWrapper> {
        val tackenDistribution = StatisticUtil.getTackenDistribution(
            stats.gameResultHistory.filter { r -> r.faction != Faction.NONE },
            null
        )

        val partnerNames = mutableListOf<String>()
        val partnerNamesWithTacken = mutableListOf<String>()
        val partnerNamesWithGames = mutableListOf<String>()

        val partnerWinsRe = mutableListOf<Int>()
        val partnerWinsContra = mutableListOf<Int>()
        val partnerLossRe = mutableListOf<Int>()
        val partnerLossContra = mutableListOf<Int>()

        val partnerTackenWinsRe = mutableListOf<Int>()
        val partnerTackenWinsContra = mutableListOf<Int>()
        val partnerTackenLossRe = mutableListOf<Int>()
        val partnerTackenLossContra = mutableListOf<Int>()

        val opponentNames = mutableListOf<String>()
        val opponentNamesWithTacken = mutableListOf<String>()
        val opponentNamesWithGames = mutableListOf<String>()

        val opponentWinsRe = mutableListOf<Int>()
        val opponentWinsContra = mutableListOf<Int>()
        val opponentLossRe = mutableListOf<Int>()
        val opponentLossContra = mutableListOf<Int>()

        val opponentTackenWinsRe = mutableListOf<Int>()
        val opponentTackenWinsContra = mutableListOf<Int>()
        val opponentTackenLossRe = mutableListOf<Int>()
        val opponentTackenLossContra = mutableListOf<Int>()
        
        val winLossHistory = mutableListOf<Int>()
        val winLossBockMarker = mutableListOf<String>()
        var longestWinStreak = 0
        var longestLossStreak = 0
        var currentWinStreak = 0
        var currentLossStreak = 0
        stats.gameResultHistory.forEach { gr ->
            if (gr.isWinner && gr.faction != Faction.NONE) {
                if (currentLossStreak > longestLossStreak) {
                    longestLossStreak = currentLossStreak
                }
                currentLossStreak = 0
                currentWinStreak += 1
            }
            if (!gr.isWinner && gr.faction != Faction.NONE) {
                if (currentWinStreak > longestWinStreak) {
                    longestWinStreak = currentWinStreak
                }
                currentWinStreak = 0
                currentLossStreak += 1
            }

            winLossHistory.add(if (gr.faction != Faction.NONE) (if (gr.isWinner) 1 else -1) else 0)
            winLossBockMarker.add(
                if (gr.faction != Faction.NONE) {
                    if (gr.isBockrunde) "#".plus(IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT)
                    else "#".plus(
                        IStatisticViewWrapper.COLOR_NEURAL
                    )
                } else "#AFAFAF"
            )
        }
        if (currentWinStreak > longestWinStreak) longestWinStreak = currentWinStreak
        if (currentLossStreak > longestLossStreak) longestLossStreak = currentLossStreak

        var gamesBockrunde = 0
        var winsBockrunde = 0
        var gamesNoBockrunde = 0
        var winsNoBockrunde = 0
        stats.gameResultHistory.forEach { gr ->
            if (gr.faction != Faction.NONE) {
                if (gr.isBockrunde) {
                    gamesBockrunde += 1
                    if (gr.isWinner) {
                        winsBockrunde += 1
                    }
                } else {
                    gamesNoBockrunde += 1
                    if (gr.isWinner) {
                        winsNoBockrunde += 1
                    }
                }
            }
        }
        val winrateBockrunde =
            if (gamesBockrunde > 0) ((winsBockrunde / (gamesBockrunde * 1f)) * 100).toInt() else 0
        val winrateNoBockrunde =
            if (gamesNoBockrunde > 0) ((winsNoBockrunde / (gamesNoBockrunde * 1f)) * 100).toInt() else 0

        stats.partners.values.forEach { p ->
            partnerNames.add(p.player.name)
            partnerNamesWithTacken.add("${p.player.name} (${p.general.total.tacken})")
            partnerNamesWithGames.add("${p.player.name} (${p.general.total.games})")

            partnerWinsRe.add(p.re.wins.games)
            partnerWinsContra.add(p.contra.wins.games)
            partnerLossRe.add(p.re.loss.games)
            partnerLossContra.add(p.contra.loss.games)

            partnerTackenWinsRe.add(p.re.wins.tacken)
            partnerTackenWinsContra.add(p.contra.wins.tacken)
            partnerTackenLossRe.add(-1 * p.re.loss.tacken)
            partnerTackenLossContra.add(-1 * p.contra.loss.tacken)
        }

        stats.opponents.values.forEach { o ->
            opponentNames.add(o.player.name)
            opponentNamesWithTacken.add("${o.player.name} (${o.general.total.tacken})")
            opponentNamesWithGames.add("${o.player.name} (${o.general.total.games})")

            opponentWinsRe.add(o.re.wins.games)
            opponentWinsContra.add(o.contra.wins.games)
            opponentLossRe.add(o.re.loss.games)
            opponentLossContra.add(o.contra.loss.games)

            opponentTackenWinsRe.add(o.re.wins.tacken)
            opponentTackenWinsContra.add(o.contra.wins.tacken)
            opponentTackenLossRe.add(-1 * o.re.loss.tacken)
            opponentTackenLossContra.add(-1 * o.contra.loss.tacken)
        }

        return listOf(
            SimpleTextStatisticViewWrapper(
                "Statistik von ${stats.player.name}",
                "Hier siehst du die Statistiken von ${stats.player.name}. Er/Sie hat so viele Spiele gespielt:",
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
                            "Mit Bockrunden",
                            StatisticUtil.getAccumulatedTackenHistory(stats.gameResultHistory)
                        ),
                        LineChartViewWrapper.ChartLineData(
                            "Ohne Bockrunden",
                            StatisticUtil.getAccumulatedTackenHistoryWithoutBock(stats.gameResultHistory)
                        )
                    ),
                    350f
                )
            ),
            SimpleTextStatisticViewWrapper(
                "Siegesrate ohne Bockrunden",
                "In Spielen, die keine Bockrunde waren, hat ${stats.player.name} eine Siegesrate von:",
                "$winrateNoBockrunde%"
            ),
            SimpleTextStatisticViewWrapper(
                "Siegesrate in Bockrunden",
                "In Spielen, die Bockrunden waren, hat ${stats.player.name} eine Siegesrate von:",
                "$winrateBockrunde%"
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
                StatisticUtil.getAccumulatedStraftackenHistory(stats.gameResultHistory).last()
                    .toString()
            ),
            SimpleTextStatisticViewWrapper(
                "Siegesausbeute",
                "Wenn ${stats.player.name} gewinnt, bekommt er/sie im Schnitt diese Tacken:",
                "%.2f".format(stats.general.wins.getTackenPerGame())
            ),
            SimpleTextStatisticViewWrapper(
                "Schmerzliche Verluste",
                "Wenn ${stats.player.name} verliert, kostet das im Schnitt etwa diese Tacken:",
                "%.2f".format(stats.general.loss.getTackenPerGame())
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
                    tackenDistribution.indices().map { i -> i.toString() },
                    height = 250f
                )
            ),
            SimpleTextStatisticViewWrapper(
                "Begnadeter Solist?",
                if (stats.solo.total.games > 0) "${stats.player.name} hat ${stats.solo.total.games} Soli gespielt und dabei folgende Tacken gemacht:" else "${stats.player.name} hat keine Soli gespielt.",
                stats.solo.total.tacken.toString()
            ),
            //TODO: Evtl. wie bei der Kickerapp Horizonzal?
            ColumnChartViewWrapper(
                ColumnChartViewWrapper.ColumnChartData(
                    "Spiele mit ...", "", "Spiele",
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
                    partnerNamesWithGames
                )
            ),
            ColumnChartViewWrapper(
                ColumnChartViewWrapper.ColumnChartData(
                    "Tacken mit ...", "", "Tacken",
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
            ),
            ColumnChartViewWrapper(
                ColumnChartViewWrapper.ColumnChartData(
                    "Spiele gegen ...", "", "Spiele",
                    listOf(
                        ColumnChartViewWrapper.ColumnSeriesData(
                            "Siege",
                            listOf(
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "Siege Re",
                                    IStatisticViewWrapper.COLOR_POSITIVE_DARK,
                                    opponentWinsRe
                                ),
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "Siege Contra",
                                    IStatisticViewWrapper.COLOR_POSITIVE_LIGHT,
                                    opponentWinsContra
                                )
                            )
                        ),
                        ColumnChartViewWrapper.ColumnSeriesData(
                            "Niederlagen",
                            listOf(
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "Ndl Re",
                                    IStatisticViewWrapper.COLOR_NEGATIVE_DARK,
                                    opponentLossRe
                                ),
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "Ndl Contra",
                                    IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT,
                                    opponentLossContra
                                )
                            )
                        )
                    ),
                    opponentNamesWithGames
                )
            ),
            ColumnChartViewWrapper(
                ColumnChartViewWrapper.ColumnChartData(
                    "Tacken gegen ...", "", "Tacken",
                    listOf(
                        ColumnChartViewWrapper.ColumnSeriesData(
                            "Siege",
                            listOf(
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "bei Siegen Re",
                                    IStatisticViewWrapper.COLOR_POSITIVE_DARK,
                                    opponentTackenWinsRe
                                ),
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "bei Siegen Contra",
                                    IStatisticViewWrapper.COLOR_POSITIVE_LIGHT,
                                    opponentTackenWinsContra
                                )
                            )
                        ),
                        ColumnChartViewWrapper.ColumnSeriesData(
                            "Niederlagen",
                            listOf(
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "bei Ndl Re",
                                    IStatisticViewWrapper.COLOR_NEGATIVE_DARK,
                                    opponentTackenLossRe
                                ),
                                ColumnChartViewWrapper.ColumnSeriesStackData(
                                    "bei Ndl Contra",
                                    IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT,
                                    opponentTackenLossContra
                                )
                            )
                        )
                    ),
                    opponentNamesWithTacken
                )
            ),
            ScatterChartViewWrapper(
                ScatterChartViewWrapper.ScatterChartData(
                    "Ergebnisverlauf (Bockrunden hervorgehoben",
                    "Ndl. | Sieg",
                    listOf(
                        ScatterChartViewWrapper.ScatterLineData(
                            "Ergebnis (Bockrunden hervorgehoben)",
                            winLossHistory,
                            winLossBockMarker
                        )
                    ),
                    showYAxisValues = false,
                    showLegend = false,
                    height = 300f
                )
            ),
            SimpleTextStatisticViewWrapper(
                "Längste Siegesserie",
                "Die längste Siegesserie hielt folgende Anzahl von Spielen:",
                longestWinStreak.toString()
            ),
            SimpleTextStatisticViewWrapper(
                "Längste Niederlagenserie",
                "Die längste Niederlagenserie hielt folgende Anzahl von Spielen:",
                longestLossStreak.toString()
            )
        )
    }
}