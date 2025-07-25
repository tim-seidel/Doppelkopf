package de.timseidel.doppelkopf.model.statistic

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.Ranking
import de.timseidel.doppelkopf.model.RankingItem
import de.timseidel.doppelkopf.model.statistic.group.GroupStatistics
import de.timseidel.doppelkopf.model.statistic.group.MemberStatistic
import de.timseidel.doppelkopf.model.statistic.session.SessionMemberStatistic

class RankingStatisticsProvider {

    fun getRankings(groupStatistics: GroupStatistics, isBockrundeEnabled: Boolean): List<Ranking> {
        val activeMemberStatistics = groupStatistics.memberStatistics.filter { it.member.isActive }

        val rankings = mutableListOf(
            getMostTackenRanking(activeMemberStatistics),
            getMostStrafTackenRanking(activeMemberStatistics),
            getMostTackenAtWinsRanking(activeMemberStatistics),
            getMostTackenAtLossRanking(activeMemberStatistics),
            getHighestReWinPercentageRanking(activeMemberStatistics),
            getHighestContraWinPercentageRanking(activeMemberStatistics),
            getHighestRePercentageRanking(activeMemberStatistics),
            getMostPlayedSoliRanking(activeMemberStatistics),
            getHighestSoliTackenGainRanking(activeMemberStatistics),
            getSoliWinPercentageRanking(activeMemberStatistics),
            getSchwarzVerlorenRanking(activeMemberStatistics),
            getMostGamesRanking(activeMemberStatistics),
            getLongestWinStreakRanking(activeMemberStatistics),
            getLongestLossStreakRanking(activeMemberStatistics),
            getMaxTackenWin(activeMemberStatistics),
            getMaxTackenLoss(activeMemberStatistics),
            getHighestSessionTackenGainRanking(activeMemberStatistics),
            getLowestSessionTackenGainRanking(activeMemberStatistics),
            getHighestSessionTackenRanking(activeMemberStatistics),
            getLowestSessionTackenRanking(activeMemberStatistics),
            getHighestOverallTacken(activeMemberStatistics),
            getLowestOverallTacken(activeMemberStatistics),
            getSessionWinsRanking(groupStatistics, activeMemberStatistics),
            getSessionLossRanking(groupStatistics, activeMemberStatistics)
        )

        if (isBockrundeEnabled) {
            rankings.add(getMostBockTackenGainRanking(activeMemberStatistics))
        }

        return rankings
    }

    private fun getMostGamesRanking(memberStatistics: List<MemberStatistic>): Ranking {
        val ranking = Ranking(
            "Meiste Spiele",
            "Die Anzahl der insgesamt gespielten Spiele.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    memberStatistic.general.total.games.toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getMostTackenRanking(memberStatistics: List<MemberStatistic>): Ranking {
        val ranking = Ranking(
            "Aktuelle Tackenanzahl",
            "Die Tackenbilanz, die über die Aneinanderreihung aller Sessions und Spiele gebildet wird.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    memberStatistic.general.total.tacken.toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getMostStrafTackenRanking(memberStatistics: List<MemberStatistic>): Ranking {
        val ranking = Ranking(
            "Meiste Straftacken",
            "Die Gesamtzahl aller verlorenen Tacken.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    StatisticUtil.getTotalStrafTacken(memberStatistic.gameResultHistory).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getMostTackenAtWinsRanking(memberStatistics: List<MemberStatistic>): Ranking {
        val ranking = Ranking(
            "Tacken bei Siegen",
            "Die durchschnittliche Anzahl an Tacken bei einem Sieg.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    memberStatistic.general.wins.getTackenPerGame().toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toFloat() })

        return ranking
    }

    private fun getMostTackenAtLossRanking(memberStatistics: List<MemberStatistic>): Ranking {
        val ranking = Ranking(
            "Tacken bei Niederlagen",
            "Die durchschnittliche Anzahl an Tacken bei einer Niederlage.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    memberStatistic.general.loss.getTackenPerGame().toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toFloat() })

        return ranking
    }

    private fun getHighestReWinPercentageRanking(memberStatistics: List<MemberStatistic>): Ranking {
        val ranking = Ranking(
            "Höchste Siegesquote | Re",
            "Die Siegesquote in Prozent in allen Spielen als Re-Partei (inkl. Soli und Hochzeiten)",
            memberStatistics.map { memberStatistic ->
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

    private fun getHighestContraWinPercentageRanking(memberStatistics: List<MemberStatistic>): Ranking {
        val ranking = Ranking(
            "Höchste Siegesquote | Contra",
            "Die Siegesquote in Prozent in allen Spielen als Contra-Partei (inkl. Soli und Hochzeiten).",
            memberStatistics.map { memberStatistic ->
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

    private fun getHighestRePercentageRanking(memberStatistics: List<MemberStatistic>): Ranking {
        val ranking = Ranking(
            "Höchste Requote (ohne Soli)",
            "Die Prozentzahl aller Spiele als Re-Partei in Normalspielen.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    String.format("%.1f", if (memberStatistic.general.total.games - memberStatistic.solo.total.games > 0) (((memberStatistic.re.total.games - memberStatistic.gameResultHistory.filter { gr -> gr.faction == Faction.RE && gr.gameType == GameType.SOLO }.size) / ((memberStatistic.general.total.games - memberStatistic.gameResultHistory.filter { gr -> gr.faction != Faction.NONE && gr.gameType == GameType.SOLO }.size) * 1f)) * 100) else 0f)
                    //(if (memberStatistic.general.total.games - memberStatistic.solo.total.games > 0) (((memberStatistic.re.total.games - memberStatistic.gameResultHistory.filter { gr -> gr.faction == Faction.RE && gr.gameType == GameType.SOLO }.size) / ((memberStatistic.general.total.games - memberStatistic.solo.total.games) * 1f)) * 100).toInt() else 0).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toFloat() })

        ranking.items.forEach { rankingItem ->
            rankingItem.value = "${rankingItem.value}%"
        }

        return ranking
    }

    private fun getSoliWinPercentageRanking(memberStatistics: List<MemberStatistic>): Ranking {
        val ranking = Ranking(
            "Soli Siegesquote",
            "Die Siegesquote in Prozent in eigenen Soli.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    String.format("%.1f", if (memberStatistic.solo.total.games > 0) ((memberStatistic.solo.wins.games / (memberStatistic.solo.total.games * 1f)) * 100) else 0f)
                )

            }.sortedByDescending { rankingItem -> rankingItem.value.toFloat() })

        ranking.items.forEach { rankingItem ->
            rankingItem.value = "${rankingItem.value}%"
        }

        return ranking
    }

    private fun getMostPlayedSoliRanking(memberStatistics: List<MemberStatistic>): Ranking {
        val ranking = Ranking(
            "Meiste gespielte Soli",
            "",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    memberStatistic.solo.total.games.toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getHighestSoliTackenGainRanking(memberStatistics: List<MemberStatistic>): Ranking {
        val ranking = Ranking(
            "Tackengewinn durch Soli",
            "Die Summierte Zahl der Tacken, die durch eigene Soli gewonnen/verloren wurden.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    memberStatistic.solo.total.tacken.toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getMostBockTackenGainRanking(memberStatistics: List<MemberStatistic>): Ranking {
        val ranking = Ranking(
            "Größter Tackengewinn durch Bockrunden",
            "Die Gesamtanzahl der Tacken, die durch Bockrunden zusätzlich gewonnen/verloren wurde.",
            memberStatistics.map { memberStatistic ->
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

    private fun getLongestWinStreakRanking(memberStatistics: List<MemberStatistic>): Ranking {
        val ranking = Ranking(
            "Längste Siegesserie",
            "",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    (if (memberStatistic.gameResultHistory.isNotEmpty())
                        StatisticUtil.calculateStreakStatistics(memberStatistic.gameResultHistory).longestWinStreak.toString() else 0).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getLongestLossStreakRanking(memberStatistics: List<MemberStatistic>): Ranking {
        val ranking = Ranking(
            "Längste Niederlagenserie",
            "",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    (if (memberStatistic.gameResultHistory.isNotEmpty())
                        StatisticUtil.calculateStreakStatistics(memberStatistic.gameResultHistory).longestLossStreak.toString() else 0).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getMaxTackenWin(memberStatistics: List<MemberStatistic>): Ranking {
        val ranking = Ranking(
            "Höchster Tackengewinn",
            "Der höchste Tackengewinn bezogen auf ein einziges Spiel.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    (memberStatistic.gameResultHistory.filter { gr -> gr.isWinner && gr.faction != Faction.NONE }
                        .maxOfOrNull { it.tacken } ?: 0).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getMaxTackenLoss(memberStatistics: List<MemberStatistic>): Ranking {
        val ranking = Ranking(
            "Höchster Tackenverlust",
            "Der höchste Tackenverlust bezogen auf ein einziges Spiel.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    (memberStatistic.gameResultHistory.filter { gr -> !gr.isWinner && gr.faction != Faction.NONE }
                        .minOfOrNull { it.tacken } ?: 0).toString()
                )
            }.sortedBy { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getHighestSessionTackenGainRanking(memberStatistics: List<MemberStatistic>): Ranking {

        val ranking = Ranking(
            "Bestes Sessionergebnis",
            "Der höchste Tackenstand am Ende einer Session.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    getHighestSessionEndTacken(memberStatistic.sessionStatistics).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getLowestSessionTackenGainRanking(memberStatistics: List<MemberStatistic>): Ranking {

        val ranking = Ranking(
            "Schlechtestes Sessionergebnis",
            "Der niedrigste Tackenstand am Ende einer Session.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    getLowestSessionEndTacken(memberStatistic.sessionStatistics).toString()
                )
            }.sortedBy { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getHighestSessionTackenRanking(memberStatistics: List<MemberStatistic>): Ranking {

        val ranking = Ranking(
            "Bester Zwischenstand",
            "Der höchste Tackenstand insgesamt innerhalb einer Session.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    getHighestSessionTacken(memberStatistic.sessionStatistics).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getLowestSessionTackenRanking(memberStatistics: List<MemberStatistic>): Ranking {

        val ranking = Ranking(
            "Schlechtester Zwischenstand",
            "Der niedrigste Tackenstand insgesamt innerhalb einer Session.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    getLowestSessionTacken(memberStatistic.sessionStatistics).toString()
                )
            }.sortedBy { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getHighestOverallTacken(memberStatistics: List<MemberStatistic>): Ranking{
        val ranking = Ranking(
            "Positivrekord",
            "Der höchste Tackenstand, der insgesamt jemals erreicht wurde.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    StatisticUtil.getAccumulatedTackenHistory(memberStatistic.gameResultHistory).maxOrNull()?.toString() ?: "0"
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getLowestOverallTacken(memberStatistics: List<MemberStatistic>): Ranking{
        val ranking = Ranking(
            "Negativrekord",
            "Der niedrigste Tackenstand, der insgesamt jemals erreicht wurde.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    StatisticUtil.getAccumulatedTackenHistory(memberStatistic.gameResultHistory).minOrNull()?.toString() ?: "0"
                )
            }.sortedBy { rankingItem -> rankingItem.value.toInt() })

        return ranking
    }

    private fun getSessionWinsRanking(groupStatistics: GroupStatistics, memberStatistics: List<MemberStatistic>): Ranking{
        val ranking = Ranking(
            "Meiste gewonnene Abende",
            "Die Anzahl der Abende an denen jemand am Ende die höchsten Tacken hatte.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    StatisticUtil.getWinsOfMember(groupStatistics, memberStatistic.member).toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() }
        )

        return ranking
    }

    private fun getSessionLossRanking(groupStatistics: GroupStatistics, memberStatistics: List<MemberStatistic>): Ranking{
        val ranking = Ranking(
            "Meiste verlorenene Abende",
            "Die Anzahl der Abende an denen jemand am Ende die niedrigsten Tacken hatte.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    StatisticUtil.getLossesOfMember(groupStatistics, memberStatistic.member)
                        .toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() }
        )

        return ranking
    }

    private fun getSchwarzVerlorenRanking(memberStatistics: List<MemberStatistic>): Ranking {
        val ranking = Ranking(
            "Meiste Runden \"schwarz verloren\"",
            "Die Anzahl der Runden, die jemand schwarz verloren hat.",
            memberStatistics.map { memberStatistic ->
                RankingItem(
                    memberStatistic.member.name,
                    StatisticUtil.getSchwarzVerlorenCount(memberStatistic)
                        .toString()
                )
            }.sortedByDescending { rankingItem -> rankingItem.value.toInt() }
        )

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
