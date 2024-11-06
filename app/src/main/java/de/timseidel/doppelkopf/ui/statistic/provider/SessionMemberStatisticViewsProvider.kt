package de.timseidel.doppelkopf.ui.statistic.provider

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.statistic.SimpleStatisticsCalculator
import de.timseidel.doppelkopf.model.statistic.StatisticUtil
import de.timseidel.doppelkopf.model.statistic.session.SessionMemberStatistic
import de.timseidel.doppelkopf.ui.statistic.views.ColumnChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.IStatisticViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.LineChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.PieChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.ScatterChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.SimpleTextStatisticViewWrapper
import de.timseidel.doppelkopf.util.RangeDistribution
import kotlin.math.abs
import kotlin.math.round

class SessionMemberStatisticViewsProvider(private var stats: SessionMemberStatistic) : IStatisticViewsProvider {
    override fun getStatisticItems(isBockrundeEnabled: Boolean): List<IStatisticViewWrapper> {
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

        stats.gameResultHistory.forEach { gr ->
            winLossHistory.add(if (gr.faction != Faction.NONE) (if (gr.isWinner) 1 else -1) else 0)
            winLossBockMarker.add(
                if (gr.faction != Faction.NONE) {
                    if (gr.isBockrunde && isBockrundeEnabled) "#".plus(IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT)
                    else "#".plus(
                        IStatisticViewWrapper.COLOR_NEURAL
                    )
                } else "#AFAFAF"
            )
        }

        val streakStatistics =
            SimpleStatisticsCalculator().calculateStreakStatistics(stats.gameResultHistory)
        val streakHistoryWinLossMarker = mutableListOf<String>()
        streakStatistics.streakHistory.forEach { streak ->
            streakHistoryWinLossMarker.add(
                if (streak > 0) "#".plus(IStatisticViewWrapper.COLOR_POSITIVE_DARK)
                else "#".plus(IStatisticViewWrapper.COLOR_NEGATIVE_DARK)
            )
        }

        val streakDistribution = RangeDistribution(
            -1 * streakStatistics.longestLossStreak,
            streakStatistics.longestWinStreak
        )
        streakStatistics.streakHistory.forEach { streak ->
            streakDistribution.increase(streak, 1)
        }

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

        val gameResultsWithoutBock =
            StatisticUtil.getAccumulatedTackenHistoryWithoutBock(stats.gameResultHistory)
        val currentTackenWithoutBock =
            if (gameResultsWithoutBock.isNotEmpty()) gameResultsWithoutBock.last() else 0
        val bockTackenDiff = stats.general.total.tacken - currentTackenWithoutBock

        stats.partners.values.forEach { p ->
            partnerNames.add(p.member.name)
            partnerNamesWithTacken.add("${p.member.name} (${p.general.total.tacken})")
            partnerNamesWithGames.add("${p.member.name} (${p.general.total.games})")

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
            opponentNames.add(o.member.name)
            opponentNamesWithTacken.add("${o.member.name} (${o.general.total.tacken})")
            opponentNamesWithGames.add("${o.member.name} (${o.general.total.games})")

            opponentWinsRe.add(o.re.wins.games)
            opponentWinsContra.add(o.contra.wins.games)
            opponentLossRe.add(o.re.loss.games)
            opponentLossContra.add(o.contra.loss.games)

            opponentTackenWinsRe.add(o.re.wins.tacken)
            opponentTackenWinsContra.add(o.contra.wins.tacken)
            opponentTackenLossRe.add(-1 * o.re.loss.tacken)
            opponentTackenLossContra.add(-1 * o.contra.loss.tacken)
        }

        var soloGamesAsContra = 0
        stats.gameResultHistory.forEach { gr ->
            if (gr.faction == Faction.CONTRA && gr.gameType == GameType.SOLO)
                soloGamesAsContra += 1
        }

        val percentReGames =
            if (stats.general.total.games > 0) round(stats.re.total.games / stats.general.total.games.toFloat() * 100).toInt() else 0

        val percentWins =
            if (stats.general.total.games > 0) round(stats.general.wins.games / stats.general.total.games.toFloat() * 100).toInt() else 0

        val totalGamesTextStat = SimpleTextStatisticViewWrapper(
            "Statistik von ${stats.member.name}",
            "Hier siehst du die Statistiken von ${stats.member.name}. Er/Sie hat so viele Spiele gespielt:",
            stats.general.total.games.toString()
        )

        val winLossPieChart = PieChartViewWrapper(
            PieChartViewWrapper.PieChartData(
                "Siege und Niederlagen",
                "Siege: ${stats.general.wins.games} ($percentWins%), Niederlagen: ${stats.general.loss.games}",
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
        )

        val parteiPieChart = PieChartViewWrapper(
            PieChartViewWrapper.PieChartData(
                "Parteistatistik",
                "Spiele Re: ${stats.re.total.games} ($percentReGames%), Spiele Contra: ${stats.contra.total.games}",
                "Spiele",
                listOf(
                    PieChartViewWrapper.PieSliceData(
                        "Re",
                        stats.re.total.games - stats.solo.total.games,
                        "#${IStatisticViewWrapper.COLOR_POSITIVE_DARK}"
                    ),
                    PieChartViewWrapper.PieSliceData(
                        "Re Solo",
                        abs(stats.solo.total.games),
                        "#${IStatisticViewWrapper.COLOR_POSITIVE_LIGHT}"
                    ),
                    PieChartViewWrapper.PieSliceData(
                        "Contra",
                        stats.contra.total.games - soloGamesAsContra,
                        "#${IStatisticViewWrapper.COLOR_NEGATIVE_DARK}"
                    ),
                    PieChartViewWrapper.PieSliceData(
                        "Contra Solo",
                        soloGamesAsContra,
                        "#${IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT}"
                    )
                )
            )
        )

        val currentTackenTextStat = SimpleTextStatisticViewWrapper(
            "Aktuelle Tacken",
            "${stats.member.name} steht aktuell bei dieser Tackenanzahl:",
            stats.general.total.tacken.toString()
        )

        val tackenLineChartBockEnabled = LineChartViewWrapper(
            LineChartViewWrapper.LineChartData(
                "Tackenverlauf", "Spiele","Tacken", listOf(
                    LineChartViewWrapper.ChartLineData(
                        "Mit Bockrunden (${stats.general.total.tacken})",
                        StatisticUtil.getAccumulatedTackenHistory(stats.gameResultHistory),
                        IStatisticViewWrapper.COLOR_NEGATIVE_DARK
                    ),
                    LineChartViewWrapper.ChartLineData(
                        "Ohne Bockrunden($currentTackenWithoutBock)",
                        StatisticUtil.getAccumulatedTackenHistoryWithoutBock(stats.gameResultHistory),
                        "000000"
                    )
                ),
                350f
            )
        )

        val tackenLineChartBockDisabled = LineChartViewWrapper(
            LineChartViewWrapper.LineChartData(
                "Tackenverlauf", "Spiele", "Tacken", listOf(
                    LineChartViewWrapper.ChartLineData(
                        "Tacken ($currentTackenWithoutBock)",
                        StatisticUtil.getAccumulatedTackenHistoryWithoutBock(stats.gameResultHistory)
                    )
                ),
                350f
            )
        )

        val tackenDiffWithBockrundenTextStat = SimpleTextStatisticViewWrapper(
            "Bock auf Bockrunden?",
            "Folgende Tackendifferenz hat ${stats.member.name} in Bockrunden gemacht:",
            (if (bockTackenDiff > 0) "+" else "") + bockTackenDiff.toString(),
        )

        val winRateOutsideOfBockTextStat = SimpleTextStatisticViewWrapper(
            "Siegesrate ohne Bockrunden",
            "In Spielen, die keine Bockrunde waren, hat ${stats.member.name} eine Siegesrate von:",
            "$winrateNoBockrunde%"
        )
        val winRateInBockTextStat = SimpleTextStatisticViewWrapper(
            "Siegesrate in Bockrunden",
            "In Spielen, die Bockrunden waren, hat ${stats.member.name} eine Siegesrate von:",
            "$winrateBockrunde%"
        )

        val tackenParteiPieChart = PieChartViewWrapper(
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
        )

        val totalTackenLossTextStat = SimpleTextStatisticViewWrapper(
            "Schuldschein",
            "${stats.member.name} muss so viele verlorene Tacken bezahlen:",
            StatisticUtil.getAccumulatedStraftackenHistory(stats.gameResultHistory).last()
                .toString()
        )

        val averageWinTackenTextStat = SimpleTextStatisticViewWrapper(
            "Siegesausbeute",
            "Wenn ${stats.member.name} gewinnt, bekommt er/sie im Schnitt diese Tacken:",
            "%.2f".format(stats.general.wins.getTackenPerGame())
        )
        val averageLossTackenTextStat = SimpleTextStatisticViewWrapper(
            "Schmerzliche Verluste",
            "Wenn ${stats.member.name} verliert, kostet das im Schnitt etwa diese Tacken:",
            "%.2f".format(stats.general.loss.getTackenPerGame())
        )

        val tackenDistributionBarchart = ColumnChartViewWrapper(
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
        )

        val memberSoloTextStat = SimpleTextStatisticViewWrapper(
            "Begnadeter Solist?",
            if (stats.solo.total.games > 0) "${stats.member.name} hat ${stats.solo.total.games} Soli gespielt und dabei folgende Tacken gemacht:" else "${stats.member.name} hat keine Soli gespielt.",
            stats.solo.total.tacken.toString()
        )

        val memberWithMemberGamesBarchart = ColumnChartViewWrapper(
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
        )

        val memberWithMemberTackenBarchart = ColumnChartViewWrapper(
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
        )

        val memberVsMemberGamesBarchart = ColumnChartViewWrapper(
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
        )

        val memberVsMemberTackenBarchart = ColumnChartViewWrapper(
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
        )

        val gameWinLossScatter = ScatterChartViewWrapper(
            ScatterChartViewWrapper.ScatterChartData(
                "Ergebnisverlauf",
                "Verlauf von Sieg, Aussetzen und Niederlage. Bockrunden sind rot hervorgehoben",
                "Ndl. | Aussetzen | Sieg",
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
        )

        val longestWinStreakTextStat = SimpleTextStatisticViewWrapper(
            "Längste Siegesserie",
            "Die längste Siegesserie hielt folgende Anzahl von Spielen:",
            streakStatistics.longestWinStreak.toString()
        )
        val longestLossStreakTextStat = SimpleTextStatisticViewWrapper(
            "Längste Niederlagenserie",
            "Die längste Niederlagenserie hielt folgende Anzahl von Spielen:",
            streakStatistics.longestLossStreak.toString()
        )

        val streakBarChart = ColumnChartViewWrapper(
            ColumnChartViewWrapper.ColumnChartData(
                "Serienverteilung", "Ndl. | Serienlänge | Sieg", "Serienhäufigkeit",
                listOf(
                    ColumnChartViewWrapper.ColumnSeriesData(
                        "Serienlänge",
                        listOf(
                            ColumnChartViewWrapper.ColumnSeriesStackData(
                                "Serienhäufigkeit",
                                IStatisticViewWrapper.COLOR_NEURAL.replace("#", ""),
                                streakDistribution.values(),
                            )
                        )
                    )
                ),
                streakDistribution.indices().map { i -> i.toString() },
                height = 250f
            )
        )

        if (isBockrundeEnabled) {
            return listOf(
                totalGamesTextStat,
                winLossPieChart,
                parteiPieChart,
                currentTackenTextStat,
                tackenLineChartBockEnabled,
                tackenDiffWithBockrundenTextStat,
                winRateOutsideOfBockTextStat,
                winRateInBockTextStat,
                tackenParteiPieChart,
                totalTackenLossTextStat,
                averageWinTackenTextStat,
                averageLossTackenTextStat,
                tackenDistributionBarchart,
                memberSoloTextStat,
                memberWithMemberGamesBarchart,
                memberWithMemberTackenBarchart,
                memberVsMemberGamesBarchart,
                memberVsMemberTackenBarchart,
                gameWinLossScatter,
                longestWinStreakTextStat,
                longestLossStreakTextStat,
                streakBarChart
            )
        } else {
            return listOf(
                totalGamesTextStat,
                winLossPieChart,
                parteiPieChart,
                currentTackenTextStat,
                tackenLineChartBockDisabled,
                tackenParteiPieChart,
                totalTackenLossTextStat,
                averageWinTackenTextStat,
                averageLossTackenTextStat,
                tackenDistributionBarchart,
                memberSoloTextStat,
                memberWithMemberGamesBarchart,
                memberWithMemberTackenBarchart,
                memberVsMemberGamesBarchart,
                memberVsMemberTackenBarchart,
                gameWinLossScatter,
                longestWinStreakTextStat,
                longestLossStreakTextStat,
                streakBarChart
            )
        }
    }
}