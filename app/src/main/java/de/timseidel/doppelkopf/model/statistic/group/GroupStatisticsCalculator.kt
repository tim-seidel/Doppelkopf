package de.timseidel.doppelkopf.model.statistic.group

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.statistic.SimpleStatisticEntry
import de.timseidel.doppelkopf.model.statistic.StatisticEntry
import de.timseidel.doppelkopf.model.statistic.session.PlayerStatistic
import de.timseidel.doppelkopf.model.statistic.session.SessionStatistics

class GroupStatisticsCalculator {

    fun calculateGroupStatistics(
        members: List<Member>,
        sessionStatistics: List<SessionStatistics>
    ): GroupStatistics {
        val gStats = GroupStatistics()
        sessionStatistics.forEach { sStats ->
            gStats.sessionStatistics.add(sStats)
            addSessionStatisticsToGroupStatistics(gStats, sStats)
        }

        val memberStatistics = calculateMemberStatistics(
            members,
            sessionStatistics
        ).sortedBy { memberStatistic -> memberStatistic.member.name }
        gStats.memberStatistics.addAll(memberStatistics)

        return gStats
    }

    private fun addSessionStatisticsToGroupStatistics(
        groupStatistics: GroupStatistics,
        sessionStatistics: SessionStatistics
    ) {
        addStatisticEntry(groupStatistics.general, sessionStatistics.general)
        addStatisticEntry(groupStatistics.re, sessionStatistics.re)
        addStatisticEntry(groupStatistics.contra, sessionStatistics.contra)
        addStatisticEntry(groupStatistics.solo, sessionStatistics.solo)
    }

    private fun addStatisticEntry(target: StatisticEntry, toBeAdded: StatisticEntry) {
        addSimpleStatisticEntry(target.total, toBeAdded.total)
        addSimpleStatisticEntry(target.wins, toBeAdded.wins)
        addSimpleStatisticEntry(target.loss, toBeAdded.loss)
    }

    private fun addSimpleStatisticEntry(
        target: SimpleStatisticEntry,
        toBeAdded: SimpleStatisticEntry
    ) {
        target.games += toBeAdded.games
        target.points += toBeAdded.points
        target.tacken += toBeAdded.tacken
    }

    private fun calculateMemberStatistics(
        members: List<Member>,
        sessionStatistics: List<SessionStatistics>
    ): List<MemberStatistic> {
        val memberStatistics = mutableListOf<MemberStatistic>()

        members.forEach { member ->
            memberStatistics.add(
                MemberStatistic(
                    member,
                    partners = getOtherMembers(member, members).associateBy(
                        { it.name },
                        { MemberToMemberStatistic(it) }
                    ),
                    opponents = getOtherMembers(member, members).associateBy(
                        { it.name },
                        { MemberToMemberStatistic(it) }
                    ),
                )
            )
        }

        sessionStatistics.forEach { sessionStat ->
            memberStatistics.forEach { memberStat ->
                val playerStat =
                    sessionStat.playerStatistics.firstOrNull { p -> p.player.name == memberStat.member.name }
                if (playerStat != null) {
                    addSessionPlayerStatisticsToMemberStatistics(memberStat, playerStat)
                } else {
                    // Add filler games to history
                    for (i in 1..sessionStat.general.total.games) {
                        memberStat.gameResultHistory.add(
                            GameResult("", Faction.NONE, false, 0, 0, false, GameType.NORMAL)
                        )
                    }
                }
            }
        }

        return memberStatistics
    }

    private fun addSessionPlayerStatisticsToMemberStatistics(
        memberStatistic: MemberStatistic,
        playerStatistic: PlayerStatistic
    ) {
        addStatisticEntry(memberStatistic.general, playerStatistic.general)
        addStatisticEntry(memberStatistic.re, playerStatistic.re)
        addStatisticEntry(memberStatistic.contra, playerStatistic.contra)
        addStatisticEntry(memberStatistic.solo, playerStatistic.solo)

        playerStatistic.partners.forEach { partner ->
            val partnerPlayerStat = partner.value
            val partnerMemberStat = memberStatistic.partners[partner.value.player.name]

            if (partnerMemberStat != null) {
                addStatisticEntry(partnerMemberStat.general, partnerPlayerStat.general)
                addStatisticEntry(partnerMemberStat.re, partnerPlayerStat.re)
                addStatisticEntry(partnerMemberStat.contra, partnerPlayerStat.contra)
            }
        }

        playerStatistic.opponents.forEach { opponent ->
            val opponentPlayerStat = opponent.value
            val opponentMemberStat = memberStatistic.opponents[opponent.value.player.name]

            if (opponentMemberStat != null) {
                addStatisticEntry(opponentMemberStat.general, opponentPlayerStat.general)
                addStatisticEntry(opponentMemberStat.re, opponentPlayerStat.re)
                addStatisticEntry(opponentMemberStat.contra, opponentPlayerStat.contra)
            }
        }

        memberStatistic.gameResultHistory.addAll(playerStatistic.gameResultHistory)
        memberStatistic.sessionStatistics.add(playerStatistic)
    }

    private fun getOtherMembers(member: Member, allMembers: List<Member>): List<Member> {
        return allMembers.filter { m -> m.id != member.id }
    }
}