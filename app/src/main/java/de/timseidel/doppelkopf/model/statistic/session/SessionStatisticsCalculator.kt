package de.timseidel.doppelkopf.model.statistic.session

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.MemberAndFaction
import de.timseidel.doppelkopf.model.statistic.SimpleStatisticEntry
import de.timseidel.doppelkopf.model.statistic.StatisticEntry
import de.timseidel.doppelkopf.util.GameUtil

class SessionStatisticsCalculator {

    fun calculateSessionStatistics(sessionId: String, members: List<Member>, games: List<Game>): SessionStatistics {
        val stats = calculateGeneralSessionStatistics(sessionId, games)

        val memberStatistics = calculateAllMemberSessionStatistics(members, games)
            .sortedBy { memberStatistic -> memberStatistic.member.name }

        stats.sessionMemberStatistics.addAll(memberStatistics)

        return stats
    }

    fun calculateGeneralSessionStatistics(sessionId: String, games: List<Game>): SessionStatistics {
        val stats = SessionStatistics(sessionId)

        games.forEach { g ->
            if (g.winningFaction != Faction.NONE) {
                val winnerResult =
                    GameResult(
                        g.id,
                        g.winningFaction,
                        true,
                        g.tacken,
                        g.winningPoints,
                        g.isBockrunde,
                        g.gameType
                    )
                val loserResult =
                    GameResult(
                        g.id,
                        if (g.winningFaction == Faction.RE) Faction.CONTRA else Faction.RE,
                        false,
                        -1 * g.tacken,
                        240 - g.winningPoints,
                        g.isBockrunde,
                        g.gameType
                    )

                addGame(stats.general.total, winnerResult) //TODO abs(tacken)?
                addGame(stats.general.wins, winnerResult)
                addGame(stats.general.loss, loserResult)

                stats.gameResultHistoryWinner.add(winnerResult)
                stats.gameResultHistoryLoser.add(loserResult)

                if (winnerResult.faction == Faction.RE) {
                    addGame(stats.re.total, winnerResult)
                    addGame(stats.re.wins, winnerResult)

                    addGame(stats.contra.total, loserResult)
                    addGame(stats.contra.loss, loserResult)
                } else {
                    addGame(stats.contra.total, winnerResult)
                    addGame(stats.contra.wins, winnerResult)

                    addGame(stats.re.total, loserResult)
                    addGame(stats.re.loss, loserResult)
                }

                if (GameUtil.isGameTypeSoloType(g.gameType)) {
                    if (g.winningFaction == Faction.RE) {
                        addGame(stats.solo.wins, winnerResult)
                        addGame(stats.solo.total, winnerResult)
                    } else {
                        addGame(stats.solo.total, loserResult)
                        addGame(stats.solo.loss, loserResult)
                    }
                }
            }
        }

        return stats
    }

    fun calculateSingleMemberSessionStatistics(
        member: Member,
        allMembers: List<Member>,
        games: List<Game>
    ): SessionMemberStatistic {
        val stats = SessionMemberStatistic(
            member = member,
            partners = getOtherMembers(member, allMembers).associateBy(
                { it.id },
                { MemberToMemberSessionStatistic(it) }),
            opponents = getOtherMembers(member, allMembers).associateBy(
                { it.id },
                { MemberToMemberSessionStatistic(it) }
            )
        )

        calculateOwnMemberStatistics(stats, games)
        calculateMemberPartnerStatistics(stats, games)

        return stats
    }

    // TODO: Ob das auslagern wirklich sinnvoll ist...
    fun calculateAllMemberSessionStatistics(
        members: List<Member>,
        games: List<Game>
    ): List<SessionMemberStatistic> {
        val stats = mutableListOf<SessionMemberStatistic>()

        members.forEach { member ->
            stats.add(calculateSingleMemberSessionStatistics(member, members, games))
        }

        return stats
    }

    private fun getOtherMembers(member: Member, allMembers: List<Member>): List<Member> {
        return allMembers.filter { m -> m.id != member.id }
    }

    private fun calculateOwnMemberStatistics(stats: SessionMemberStatistic, games: List<Game>) {
        games.forEach { g ->
            val result = GameUtil.getMemberResult(stats.member, g)

            when (result.faction) {
                Faction.RE -> {
                    addGameResult(stats.general, result)
                    addGameResult(stats.re, result)

                    if (GameUtil.isMemberPlayingSoloType(stats.member, g)) {
                        addGameResult(stats.solo, result)
                    }
                }

                Faction.CONTRA -> {
                    addGameResult(stats.general, result)
                    addGameResult(stats.contra, result)
                }

                else -> {}
            }

            stats.gameResultHistory.add(result)
        }
    }

    private fun calculateMemberPartnerStatistics(stats: SessionMemberStatistic, games: List<Game>) {
        games.forEach { g ->
            val result = GameUtil.getMemberResult(stats.member, g)

            if (result.faction != Faction.NONE) {
                val participants =
                    g.members.filter { paf -> paf.faction != Faction.NONE && paf.member.id != stats.member.id }
                addMemberPartnerStatisticForGame(stats, result, participants)
            }
        }
    }

    private fun addMemberPartnerStatisticForGame(
        stats: SessionMemberStatistic,
        result: GameResult,
        participants: List<MemberAndFaction>
    ) {
        participants.forEach { partner ->
            if (partner.faction == result.faction) {
                addGameResult(stats.partners[partner.member.id]?.general, result)
                if (partner.faction == Faction.RE) {
                    addGameResult(stats.partners[partner.member.id]?.re, result)
                } else if (partner.faction == Faction.CONTRA) {
                    addGameResult(stats.partners[partner.member.id]?.contra, result)
                }
            } else {
                addGameResult(stats.opponents[partner.member.id]?.general, result)
                if (partner.faction == Faction.RE) addGameResult(
                    stats.opponents[partner.member.id]?.contra, result
                )
                else if (partner.faction == Faction.CONTRA) addGameResult(
                    stats.opponents[partner.member.id]?.re, result
                )
            }
        }
    }

    private fun addGameResult(
        stats: StatisticEntry?,
        gameResult: GameResult
    ) {
        if (stats != null && gameResult.faction != Faction.NONE) {
            addGame(stats.total, gameResult)
            if (gameResult.isWinner) {
                addGame(stats.wins, gameResult)

            } else {
                addGame(stats.loss, gameResult)
            }
        }
    }

    private fun addGame(stats: SimpleStatisticEntry, game: GameResult) {
        stats.games += 1
        stats.tacken += game.tacken
        stats.points += game.points
    }
}
