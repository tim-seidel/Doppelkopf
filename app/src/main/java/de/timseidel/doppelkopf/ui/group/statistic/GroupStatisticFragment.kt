package de.timseidel.doppelkopf.ui.group.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.controller.SessionController
import de.timseidel.doppelkopf.databinding.FragmentGroupStatisticBinding
import de.timseidel.doppelkopf.db.request.ReadRequestListener
import de.timseidel.doppelkopf.db.request.SessionGamesRequest
import de.timseidel.doppelkopf.db.request.SessionPlayersRequest
import de.timseidel.doppelkopf.model.DokoSession
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.model.statistic.group.GroupStatistics
import de.timseidel.doppelkopf.model.statistic.group.GroupStatisticsCalculator
import de.timseidel.doppelkopf.model.statistic.session.SessionStatistics
import de.timseidel.doppelkopf.model.statistic.session.SessionStatisticsCalculator
import de.timseidel.doppelkopf.ui.statistic.StatisticListAdapter
import de.timseidel.doppelkopf.ui.statistic.provider.EmptyStatisticViewProvider
import de.timseidel.doppelkopf.ui.statistic.provider.GroupStatisticViewProvider
import de.timseidel.doppelkopf.ui.statistic.provider.IStatisticViewsProvider
import de.timseidel.doppelkopf.ui.statistic.provider.MemberStatisticViewProvider
import de.timseidel.doppelkopf.util.DokoShortAccess
import java.time.LocalDateTime
import java.time.ZoneOffset

class GroupStatisticFragment : Fragment() {

    private val placeholderIdGroupStatistics = "__default_group"

    private var _binding: FragmentGroupStatisticBinding? = null
    private val binding get() = _binding!!

    private lateinit var groupStatistics: GroupStatistics

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGroupStatisticBinding.inflate(inflater, container, false)

        setupStatistics()
        setupMemberSelect()

        return binding.root
    }

    private fun setupStatistics() {
        val sessionInfos = DokoShortAccess.getSessionInfoCtrl().getSessionInfos()
        if (sessionInfos.isEmpty()) {
            setStatistics(EmptyStatisticViewProvider())
        } else {
            loadAllSessions(sessionInfos)
        }
    }

    private fun setupMemberSelect() {
        binding.headerStatisticMemberSelect.setListener(object :
            MemberListHeaderAdapter.OnMemberClickListener {

            override fun onMemberClicked(member: Member) {
                if (member.id == placeholderIdGroupStatistics) {
                    setStatistics(GroupStatisticViewProvider(groupStatistics))
                } else {
                    val memberStatistic =
                        groupStatistics.memberStatistics.firstOrNull { memberStatistic -> memberStatistic.member.id == member.id }

                    if (memberStatistic != null) {
                        setStatistics(MemberStatisticViewProvider(memberStatistic))
                    } else {
                        setStatistics(EmptyStatisticViewProvider())
                    }
                }
            }
        })

        val memberDefaultGroupStatisticPlaceholder =
            Member(placeholderIdGroupStatistics, "Alle", LocalDateTime.now())
        val members = DokoShortAccess.getMemberCtrl().getMembers().toMutableList()
        members.add(0, memberDefaultGroupStatisticPlaceholder)
        binding.headerStatisticMemberSelect.setMembers(members)
    }

    private fun setStatistics(provider: IStatisticViewsProvider) {
        val lvStatistic = binding.lvGroupStatistic

        val statisticItems = provider.getStatisticItems()
        val adapter = StatisticListAdapter(
            requireContext(),
            statisticItems
        )
        lvStatistic.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadAllSessions(sessionInfos: List<DokoSession>) {
        val sessions = mutableListOf<ISessionController>()
        var remainingLoadCounter = sessionInfos.size

        for (sessionInfo in sessionInfos) {
            val sessionController = SessionController()
            sessionController.set(sessionInfo)

            SessionPlayersRequest(
                DokoShortAccess.getGroupCtrl().getGroup().id,
                sessionInfo.id
            ).execute(object :
                ReadRequestListener<List<Player>> {

                override fun onReadComplete(result: List<Player>) {
                    sessionController.getPlayerController().addPlayers(result)

                    SessionGamesRequest(
                        DokoShortAccess.getGroupCtrl().getGroup().id,
                        sessionInfo.id,
                        sessionController.getPlayerController()
                    ).execute(
                        object : ReadRequestListener<List<Game>> {
                            override fun onReadComplete(result: List<Game>) {
                                result.forEach { game ->
                                    sessionController.getGameController().addGame(game)
                                }

                                sessions.add(sessionController)
                                remainingLoadCounter -= 1

                                if (remainingLoadCounter == 0) {
                                    sessions.sortBy { s ->
                                        s.getSession().date.toInstant(ZoneOffset.UTC).toEpochMilli()
                                    }

                                    groupStatistics = calculateGroupStatistics(sessions)
                                    setStatistics(
                                        GroupStatisticViewProvider(
                                            groupStatistics
                                        )
                                    )
                                }
                            }

                            override fun onReadFailed() {}
                        })
                }

                override fun onReadFailed() {}
            })
        }
    }

    private fun calculateGroupStatistics(sessions: List<ISessionController>): GroupStatistics {
        val sessionStatistics = mutableListOf<SessionStatistics>()
        sessions.forEach { session ->
            val calculator = SessionStatisticsCalculator()

            val singleSessionStatistics =
                calculator.calculateSessionStatistics(session.getGameController().getGames())
            val singleSessionPlayerStatistics =
                calculator.calculatePlayerStatistics(
                    session.getPlayerController().getPlayers(),
                    session.getGameController().getGames()
                )
            singleSessionPlayerStatistics.sortedBy { playerStatistic ->
                playerStatistic.player.name
            }
            singleSessionPlayerStatistics.forEach { playerStatistic ->
                singleSessionStatistics.playerStatistics.add(playerStatistic)
            }

            sessionStatistics.add(singleSessionStatistics)
        }

        val groupStatistics =
            GroupStatisticsCalculator().calculateGroupStatistics(sessionStatistics)
        val memberStatistics = GroupStatisticsCalculator().calculateMemberStatistics(
            DokoShortAccess.getMemberCtrl().getMembers(), sessionStatistics
        ).sortedBy { memberStatistic -> memberStatistic.member.name }

        groupStatistics.memberStatistics.addAll(memberStatistics)

        return groupStatistics
    }
}
