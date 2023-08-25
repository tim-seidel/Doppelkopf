package de.timseidel.doppelkopf.ui.group.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.databinding.FragmentGroupStatisticBinding
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.db.request.SessionListRequest
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.ui.statistic.StatisticListAdapter
import de.timseidel.doppelkopf.ui.statistic.provider.EmptyStatisticViewProvider
import de.timseidel.doppelkopf.ui.statistic.provider.GroupStatisticViewProvider
import de.timseidel.doppelkopf.ui.statistic.provider.IStatisticViewsProvider
import de.timseidel.doppelkopf.ui.statistic.provider.MemberStatisticViewProvider
import de.timseidel.doppelkopf.util.DokoShortAccess
import kotlin.math.max
import kotlin.math.min
import java.time.LocalDateTime

class GroupStatisticFragment : Fragment() {

    private val placeholderIdGroupStatistics = "__default_group"

    private var _binding: FragmentGroupStatisticBinding? = null
    private val binding get() = _binding!!

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost = requireHost() as MenuHost

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_update_statistics, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                if (item.itemId == R.id.menu_item_reset_group_statistics) {
                    DokoShortAccess.getStatsCtrl().reset()
                    setupStatistics()
                    return true
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupStatistics() {
        val sessionInfos = DokoShortAccess.getSessionInfoCtrl().getSessionInfos()
        if (sessionInfos.isEmpty()) {
            setStatistics(EmptyStatisticViewProvider())
        } else {
            val groupStatistics = DokoShortAccess.getStatsCtrl().getCachedGroupStatistics()
            if (groupStatistics != null) {
                setStatistics(GroupStatisticViewProvider(groupStatistics))
            } else {
                loadDataForStatistics()
            }
        }
    }

    private fun loadDataForStatistics() {
        val sessionInfos = DokoShortAccess.getSessionInfoCtrl().getSessionInfos()

        showSessionLoadingStart()

        SessionListRequest(sessionInfos).execute(object :
            ReadRequestListener<List<ISessionController>> {
            override fun onReadComplete(result: List<ISessionController>) {
                calculateAndApplyGroupStatistics(result)
            }

            override fun onReadFailed() {
                showSessionLoadingError()
            }
        })
    }

    private fun setupMemberSelect() {
        binding.headerStatisticMemberSelect.setListener(object :
            MemberListHeaderAdapter.OnMemberClickListener {

            override fun onMemberClicked(member: Member) {
                val stats = DokoShortAccess.getStatsCtrl().getCachedGroupStatistics()
                if (stats == null) {
                    setStatistics(EmptyStatisticViewProvider())
                } else {
                    if (member.id == placeholderIdGroupStatistics) {
                        setStatistics(GroupStatisticViewProvider(stats))
                    } else {
                        val memberStatistic =
                            stats.memberStatistics.firstOrNull { memberStatistic -> memberStatistic.member.id == member.id }

                        if (memberStatistic != null && memberStatistic.general.total.games > 0) {
                            setStatistics(MemberStatisticViewProvider(memberStatistic))
                        } else {
                            setStatistics(EmptyStatisticViewProvider())
                        }
                    }
                }
            }
        })

        val memberDefaultGroupStatisticPlaceholder =
            Member(placeholderIdGroupStatistics, "Alle", LocalDateTime.now())
        val members = DokoShortAccess.getMemberCtrl().getMembers().toMutableList()
        members.add(0, memberDefaultGroupStatisticPlaceholder)

        binding.headerStatisticMemberSelect.setRowSize(max(1, min(members.size, 4)))
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

    private fun showSessionLoadingStart() {
        Toast.makeText(
            requireContext(),
            "Alle Sessions werden zur Statistikberechnung geladen...",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showSessionLoadingError() {
        Toast.makeText(
            requireContext(),
            "Fehler beim Laden der Sessions",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun calculateAndApplyGroupStatistics(sessions: List<ISessionController>) {
        var stats = DokoShortAccess.getStatsCtrl().getCachedGroupStatistics()

        if (stats == null) {
            stats = DokoShortAccess.getStatsCtrl().calculateGroupStatistics(
                DokoShortAccess.getMemberCtrl().getMembers(),
                sessions
            )
        }

        setStatistics(GroupStatisticViewProvider(stats))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
