package de.timseidel.doppelkopf.model.statistic

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.Ranking
import de.timseidel.doppelkopf.model.RankingItem
import de.timseidel.doppelkopf.model.statistic.group.GroupStatistics
import de.timseidel.doppelkopf.model.statistic.session.SessionMemberStatistic

class RankingStatisticsProvider {

    fun getRankings(groupStatistics: GroupStatistics, isBockrundeEnabled: Boolean): List<Ranking> {
        val rankings = mutableListOf(
            getMostTackenRanking(groupStatistics),
            getMostStrafTackenRanking(groupStatistics),
            getMostTackenAtWinsRanking(groupStatistics),
            getMostTackenAtLossRanking(groupStatistics),
            getHighestReWinPercentageRanking(groupStatistics),
            getHighestContraWinPercentageRanking(groupStatistics),
            getHighestRePercentageRanking(groupStatistics),
            getMostPlayedSoliRanking(groupStatistics),
            getMostGamesRanking(groupStatistics),
            getLongestWinStreakRanking(groupStatistics),
            getLongestLossStreakRanking(groupStatistics),
            getMaxTackenWin(groupStatistics),
            getMaxTackenLoss(groupStatistics),
            getHighestSessionTackenGainRanking(groupStatistics),
            getLowestSessionTackenGainRanking(groupStatistics),
            getHighestSessionTackenRanking(groupStatistics),
            getLowestSessionTackenRanking(groupStatistics)
        )

        if (isBockrundeEnabled) {
            rankings.add(getMostBockTackenGainRanking(groupStatistics))
        }

        return rankings
    }

    private fun getMostGamesRanking(groupStatistics: GroupStatistics): Ranking {
        val ranking = Ranking(
            "Meiste Spiele",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    memberStatistic.general.total.games.toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getMostTackenRanking(groupStatistics: GroupStatistics): Ranking {
        val ranking = Ranking(
            "Aktuelle Tackenanzahl",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    memberStatistic.general.total.tacken.toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getMostStrafTackenRanking(groupStatistics: GroupStatistics): Ranking {
        val ranking = Ranking(
            "Meiste Straftacken",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    StatisticUtil.getTotalStrafTacken(memberStatistic.gameResultHistory).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getMostTackenAtWinsRanking(groupStatistics: GroupStatistics): Ranking {
        val ranking = Ranking(
            "Meiste Tacken bei Siegen",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    memberStatistic.general.wins.getTackenPerGame().toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toFloat() })

        return ranking
    }

    private fun getMostTackenAtLossRanking(groupStatistics: GroupStatistics): Ranking {
        val ranking = Ranking(
            "Niedrigste Straftacken bei Niederlagen",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    memberStatistic.general.loss.getTackenPerGame().toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toFloat() })

        return ranking
    }

    private fun getHighestReWinPercentageRanking(groupStatistics: GroupStatistics): Ranking {
        val ranking = Ranking(
            "Höchste Siegesquote | Re",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    (if (memberStatistic.re.total.games > 0) ((memberStatistic.re.wins.games / (memberStatistic.re.total.games * 1f)) * 100).toInt() else 0).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        ranking.items.forEach { rankingItem ->
            rankingItem.value = "${rankingItem.value}%"
        }

        return ranking
    }

    private fun getHighestContraWinPercentageRanking(groupStatistics: GroupStatistics): Ranking {
        val ranking = Ranking(
            "Höchste Siegesquote | Contra",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    (if (memberStatistic.contra.total.games > 0) ((memberStatistic.contra.wins.games / (memberStatistic.contra.total.games * 1f)) * 100).toInt() else 0).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        ranking.items.forEach { rankingItem ->
            rankingItem.value = "${rankingItem.value}%"
        }

        return ranking
    }

    private fun getHighestRePercentageRanking(groupStatistics: GroupStatistics): Ranking {
        val ranking = Ranking(
            "Höchste Requote (ohne Soli)",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    (if (memberStatistic.general.total.games - memberStatistic.solo.total.games > 0) (((memberStatistic.re.total.games - memberStatistic.gameResultHistory.filter { gr -> gr.faction == Faction.RE && gr.gameType == GameType.SOLO }.size) / ((memberStatistic.general.total.games - memberStatistic.solo.total.games) * 1f)) * 100).toInt() else 0).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        ranking.items.forEach { rankingItem ->
            rankingItem.value = "${rankingItem.value}%"
        }

        return ranking
    }

    private fun getMostPlayedSoliRanking(groupStatistics: GroupStatistics): Ranking {
        val ranking = Ranking(
            "Meiste gespielte Soli",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    memberStatistic.solo.total.games.toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getMostBockTackenGainRanking(groupStatistics: GroupStatistics): Ranking {
        val ranking = Ranking(
            "Größter Tackengewinn durch Bockrunden",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    (if (memberStatistic.gameResultHistory.isNotEmpty())
                        (StatisticUtil.getAccumulatedTackenHistory(memberStatistic.gameResultHistory)
                            .last() - StatisticUtil.getAccumulatedTackenHistoryWithoutBock(
                            memberStatistic.gameResultHistory
                        ).last()) else 0).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getLongestWinStreakRanking(groupStatistics: GroupStatistics): Ranking {
        val ranking = Ranking(
            "Längste Siegesserie",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    (if (memberStatistic.gameResultHistory.isNotEmpty())
                        SimpleStatisticsCalculator().calculateStreakStatistics(memberStatistic.gameResultHistory).longestWinStreak.toString() else 0).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getLongestLossStreakRanking(groupStatistics: GroupStatistics): Ranking {
        val ranking = Ranking(
            "Längste Niederlagenserie",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    (if (memberStatistic.gameResultHistory.isNotEmpty())
                        SimpleStatisticsCalculator().calculateStreakStatistics(memberStatistic.gameResultHistory).longestLossStreak.toString() else 0).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getMaxTackenWin(groupStatistics: GroupStatistics): Ranking {
        val ranking = Ranking(
            "Höchster Tackengewinn",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    (memberStatistic.gameResultHistory.filter { gr -> gr.isWinner && gr.faction != Faction.NONE }
                        .maxOfOrNull { it.tacken } ?: 0).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getMaxTackenLoss(groupStatistics: GroupStatistics): Ranking {
        val ranking = Ranking(
            "Höchster Tackenverlust",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    (memberStatistic.gameResultHistory.filter { gr -> !gr.isWinner && gr.faction != Faction.NONE }
                        .minOfOrNull { it.tacken } ?: 0).toString()
                )
            }.sortedBy { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getHighestSessionTackenGainRanking(groupStatistics: GroupStatistics): Ranking {

        val ranking = Ranking(
            "Höchstes Sessionergebnis (Tacken)",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    getHighestSessionEndTacken(memberStatistic.sessionStatistics).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getLowestSessionTackenGainRanking(groupStatistics: GroupStatistics): Ranking {

        val ranking = Ranking(
            "Niedrigstes Sessionergebnis (Tacken)",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    getLowestSessionEndTacken(memberStatistic.sessionStatistics).toString()
                )
            }.sortedBy { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getHighestSessionTackenRanking(groupStatistics: GroupStatistics): Ranking {

        val ranking = Ranking(
            "Höchster Tackenstand",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    getHighestSessionTacken(memberStatistic.sessionStatistics).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getLowestSessionTackenRanking(groupStatistics: GroupStatistics): Ranking {

        val ranking = Ranking(
            "Niedrigster Tackenstand",
            groupStatistics.memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    getLowestSessionTacken(memberStatistic.sessionStatistics).toString()
                )
            }.sortedBy { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getHighestSessionEndTacken(sessionStatistics: List<SessionMemberStatistic>): Int {
        val session = sessionStatistics.maxByOrNull { it.general.total.tacken }
        if (session != null) {
            return session.general.total.tacken
        }
        return 0
    }

    private fun getLowestSessionEndTacken(sessionStatistics: List<SessionMemberStatistic>): Int {
        val session = sessionStatistics.minByOrNull { it.general.total.tacken }
        if (session != null) {
            return session.general.total.tacken
        }
        return 0
    }

    private fun getHighestSessionTacken(sessionStatistics: List<SessionMemberStatistic>): Int {
        var maxTacken = 0
        sessionStatistics.forEach { memberStatistic ->
            val accumulated =
                StatisticUtil.getAccumulatedTackenHistory(memberStatistic.gameResultHistory)
            if (accumulated.isNotEmpty()) {
                val max = accumulated.max()
                if (max > maxTacken) {
                    maxTacken = max
                }
            }
        }

        return maxTacken
    }

    private fun getLowestSessionTacken(sessionStatistics: List<SessionMemberStatistic>): Int {
        var minTacken = 0
        sessionStatistics.forEach { memberStatistic ->
            val accumulated =
                StatisticUtil.getAccumulatedTackenHistory(memberStatistic.gameResultHistory)
            if (accumulated.isNotEmpty()) {
                val min = accumulated.min()
                if (min < minTacken) {
                    minTacken = min
                }
            }
        }

        return minTacken
    }
}
