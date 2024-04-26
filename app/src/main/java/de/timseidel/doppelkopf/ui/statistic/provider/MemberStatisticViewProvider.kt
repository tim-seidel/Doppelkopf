package de.timseidel.doppelkopf.ui.statistic.provider

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.statistic.SimpleStatisticsCalculator
import de.timseidel.doppelkopf.model.statistic.StatisticUtil
import de.timseidel.doppelkopf.model.statistic.group.MemberStatistic
import de.timseidel.doppelkopf.ui.statistic.views.ColumnChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.IStatisticViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.LineChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.PieChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.ScatterChartViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.SimpleTextStatisticViewWrapper
import de.timseidel.doppelkopf.util.RangeDistribution
import kotlin.math.abs
import kotlin.math.round

class MemberStatisticViewProvider(private val stats: MemberStatistic) : IStatisticViewsProvider {

    override fun getStatisticItems(): List<IStatisticViewWrapper> {
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

        stats.partners.values.forEach { ps ->
            partnerNames.add(ps.member.name)
            partnerNamesWithTacken.add("${ps.member.name} (${ps.general.total.tacken})")
            partnerNamesWithGames.add("${ps.member.name} (${ps.general.total.games})")

            partnerWinsRe.add(ps.re.wins.games)
            partnerWinsContra.add(ps.contra.wins.games)
            partnerLossRe.add(ps.re.loss.games)
            partnerLossContra.add(ps.contra.loss.games)

            partnerTackenWinsRe.add(ps.re.wins.tacken)
            partnerTackenWinsContra.add(ps.contra.wins.tacken)
            partnerTackenLossRe.add(-1 * ps.re.loss.tacken)
            partnerTackenLossContra.add(-1 * ps.contra.loss.tacken)
        }

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

        stats.opponents.values.forEach { os ->
            opponentNames.add(os.member.name)
            opponentNamesWithTacken.add("${os.member.name} (${os.general.total.tacken})")
            opponentNamesWithGames.add("${os.member.name} (${os.general.total.games})")

            opponentWinsRe.add(os.re.wins.games)
            opponentWinsContra.add(os.contra.wins.games)
            opponentLossRe.add(os.re.loss.games)
            opponentLossContra.add(os.contra.loss.games)

            opponentTackenWinsRe.add(os.re.wins.tacken)
            opponentTackenWinsContra.add(os.contra.wins.tacken)
            opponentTackenLossRe.add(-1 * os.re.loss.tacken)
            opponentTackenLossContra.add(-1 * os.contra.loss.tacken)
        }

        val winLossHistory = mutableListOf<Int>()
        val winLossBockMarker = mutableListOf<String>()
        stats.gameResultHistory.forEach { gr ->
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

        val strafTackenHistory =
            StatisticUtil.getAccumulatedStraftackenHistory(stats.gameResultHistory)
        val currentStrafTacken =
            if (strafTackenHistory.isNotEmpty()) strafTackenHistory.last() else 0

        var soloGamesAsContra = 0
        stats.gameResultHistory.forEach { gr ->
            if (gr.faction == Faction.CONTRA && gr.gameType == GameType.SOLO)
                soloGamesAsContra += 1
        }

        val percentReGames =
            if (stats.general.total.games > 0) round(stats.re.total.games / stats.general.total.games.toFloat() * 100).toInt() else 0

        val percentWins =
            if (stats.general.total.games > 0) round(stats.general.wins.games / stats.general.total.games.toFloat() * 100).toInt() else 0

        return listOf(
            SimpleTextStatisticViewWrapper(
                "Statistik von ${stats.member.name}",
                "Hier siehst du die Statistiken von ${stats.member.name} aus so vielen Abenden:",
                stats.sessionStatistics.size.toString()
            ),
            SimpleTextStatisticViewWrapper(
                "Vielspieler*in?",
                "${stats.member.name} hat so viele Spiele gespielt:",
                stats.general.total.games.toString()
            ),
            PieChartViewWrapper(
                PieChartViewWrapper.PieChartData(
                    "Siege und Niederlagen",
                    "Siege: ${stats.general.wins.games} ($percentWins%), Niederlagen: ${stats.general.loss.games}",
                    "Spiele",
                    listOf(
                        PieChartViewWrapper.PieSliceData(
                            "Sieg Re",
                            stats.re.wins.games,
                            "#${IStatisticViewWrapper.COLOR_POSITIVE_DARK}"
                        ),
                        PieChartViewWrapper.PieSliceData(
                            "Sieg Con",
                            stats.contra.wins.games,
                            "#${IStatisticViewWrapper.COLOR_POSITIVE_LIGHT}"
                        ),
                        PieChartViewWrapper.PieSliceData(
                            "Ndl Re",
                            abs(stats.re.loss.games),
                            "#${IStatisticViewWrapper.COLOR_NEGATIVE_DARK}"
                        ),
                        PieChartViewWrapper.PieSliceData(
                            "Ndl Con",
                            abs(stats.contra.loss.games),
                            "#${IStatisticViewWrapper.COLOR_NEGATIVE_LIGHT}"
                        )
                    )
                )
            ),
            PieChartViewWrapper(
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
            ),
            SimpleTextStatisticViewWrapper(
                "Aktuelle Tacken",
                "${stats.member.name} steht aktuell bei dieser Tackenanzahl:",
                stats.general.total.tacken.toString()
            ),
            SimpleTextStatisticViewWrapper(
                "Schuldschein",
                "${stats.member.name} muss insgesamt so viele Tacken bezahlen:",
                currentStrafTacken.toString()
            ),
            LineChartViewWrapper(
                LineChartViewWrapper.LineChartData(
                    "Tackenverlauf", "Tacken", listOf(
                        LineChartViewWrapper.ChartLineData(
                            "Mit Bockrunden (${stats.general.total.tacken})",
                            StatisticUtil.getAccumulatedTackenHistory(stats.gameResultHistory)
                        ),
                        LineChartViewWrapper.ChartLineData(
                            "Ohne Bockrunden($currentTackenWithoutBock)",
                            gameResultsWithoutBock
                        )
                    ),
                    350f
                ),
            ),
            SimpleTextStatisticViewWrapper(
                "Bock auf Bockrunden?",
                "Folgende Tackendifferenz hat ${stats.member.name} in Bockrunden gemacht:",
                (if (bockTackenDiff > 0) "+" else "") + bockTackenDiff.toString(),
            ),
            SimpleTextStatisticViewWrapper(
                "Siegesrate ohne Bockrunden",
                "In Spielen, die keine Bockrunde waren, hat ${stats.member.name} eine Siegesrate von (S: $winsNoBockrunde N: ${gamesNoBockrunde - winsNoBockrunde}):",
                "$winrateNoBockrunde%"
            ),
            SimpleTextStatisticViewWrapper(
                "Siegesrate in Bockrunden",
                "In Spielen, die Bockrunden waren, hat ${stats.member.name} eine Siegesrate von (S: $winsBockrunde N: ${gamesBockrunde - winsBockrunde}):",
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
                "Begnadeter Solist?",
                if (stats.solo.total.games > 0) "${stats.member.name} hat ${stats.solo.total.games} Soli gespielt und dabei folgende Tacken gemacht:" else "${stats.member.name} hat keine Soli gespielt.",
                stats.solo.total.tacken.toString()
            ),
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
            SimpleTextStatisticViewWrapper(
                "Längste Siegesserie",
                "Die längste Siegesserie hielt folgende Anzahl von Spielen:",
                streakStatistics.longestWinStreak.toString()
            ),
            SimpleTextStatisticViewWrapper(
                "Längste Niederlagenserie",
                "Die längste Niederlagenserie hielt folgende Anzahl von Spielen:",
                streakStatistics.longestLossStreak.toString()
            ),
            ColumnChartViewWrapper(
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
        )
    }
}