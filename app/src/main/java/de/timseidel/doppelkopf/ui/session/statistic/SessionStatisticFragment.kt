package de.timseidel.doppelkopf.ui.session.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import de.timseidel.doppelkopf.databinding.FragmentSessionStatisticBinding
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.statistic.session.SessionStatistics
import de.timseidel.doppelkopf.model.statistic.session.SessionStatisticsCalculator
import de.timseidel.doppelkopf.ui.MemberListHeaderAdapter.OnMemberClickListener
import de.timseidel.doppelkopf.ui.statistic.StatisticListAdapter
import de.timseidel.doppelkopf.ui.statistic.provider.EmptyStatisticViewProvider
import de.timseidel.doppelkopf.ui.statistic.provider.IStatisticViewsProvider
import de.timseidel.doppelkopf.ui.statistic.provider.SessionMemberStatisticViewsProvider
import de.timseidel.doppelkopf.ui.statistic.provider.SessionStatisticViewsProvider
import de.timseidel.doppelkopf.util.DokoShortAccess
import java.lang.Integer.max
import java.lang.Integer.min
import java.time.LocalDateTime

class SessionStatisticFragment : Fragment() {

    private val defaultSessionStatsMemberPlaceholderId = "__session_stats_all_placeholder_id"

    private var _binding: FragmentSessionStatisticBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionStatistics: SessionStatistics

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSessionStatisticBinding.inflate(inflater, container, false)

        setupToolbar()
        setupStatistics()
        setupMemberSelect()

        return binding.root
    }

    private fun setupToolbar(){
        (activity as AppCompatActivity).supportActionBar?.title =
            DokoShortAccess.getSessionCtrl().getSession().name

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupStatistics() {
        sessionStatistics = SessionStatisticsCalculator().calculateSessionStatistics(
            DokoShortAccess.getSessionCtrl().getSession().id,
            DokoShortAccess.getSessionCtrl().getSession().members,
            DokoShortAccess.getGameCtrl().getGames()
        )

        if (DokoShortAccess.getGameCtrl().getGames().isEmpty()) {
            setStatistics(EmptyStatisticViewProvider())
        } else {
            setStatistics(SessionStatisticViewsProvider(sessionStatistics))
        }
    }

    private fun setupMemberSelect() {
        binding.headerStatisticMemberSelect.setListener(object : OnMemberClickListener {
            override fun onMemberClicked(member: Member) {
                if (member.id == defaultSessionStatsMemberPlaceholderId) {
                    setStatistics(SessionStatisticViewsProvider(sessionStatistics))
                } else {
                    val memberStatistic =
                        sessionStatistics.sessionMemberStatistics.firstOrNull { memberStatistic -> memberStatistic.member.id == member.id }

                    if (memberStatistic != null && memberStatistic.general.total.games > 0) {
                        setStatistics(SessionMemberStatisticViewsProvider(memberStatistic))
                    } else {
                        setStatistics(EmptyStatisticViewProvider())
                    }
                }
            }
        })

        val memberDefaultSessionStatisticPlaceholder =
            Member(defaultSessionStatsMemberPlaceholderId, "Alle", LocalDateTime.now())
        val members = DokoShortAccess.getSessionCtrl().getSession().members
        members.add(0, memberDefaultSessionStatisticPlaceholder)

        binding.headerStatisticMemberSelect.setRowSize(max(1, min(4, members.size)))
        binding.headerStatisticMemberSelect.setMembers(members)
    }

    private fun setStatistics(provider: IStatisticViewsProvider) {
        val lvStatistic = binding.lvStatistic

        val withBockSettings = DokoShortAccess.getSettingsCtrl().getSettings().isBockrundeEnabled
        val statisticItems = provider.getStatisticItems(withBockSettings)
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
}