package de.timseidel.doppelkopf.ui.group.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.databinding.FragmentGroupStatisticBinding
import de.timseidel.doppelkopf.db.request.StatisticUpdateRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.StatisticStatus
import de.timseidel.doppelkopf.ui.MemberListHeaderAdapter
import de.timseidel.doppelkopf.ui.statistic.StatisticListAdapter
import de.timseidel.doppelkopf.ui.statistic.provider.EmptyStatisticViewProvider
import de.timseidel.doppelkopf.ui.statistic.provider.GroupStatisticViewProvider
import de.timseidel.doppelkopf.ui.statistic.provider.IStatisticViewsProvider
import de.timseidel.doppelkopf.ui.statistic.provider.MemberStatisticViewProvider
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.Logging
import java.time.LocalDateTime
import kotlin.math.max
import kotlin.math.min

class GroupStatisticFragment : Fragment() {

    private val placeholderIdGroupStatistics = "__group_stats_all_placeholder_id"

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
                    loadDataForStatistics()
                    return true
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupStatistics() {
        if (DokoShortAccess.getStatsCtrl().isCachedStatisticsAvailable()) {
            setStatistics(
                GroupStatisticViewProvider(
                    DokoShortAccess.getStatsCtrl().getCachedGroupStatistics()
                )
            )
        } else {
            Logging.d(
                "GroupStatisticFragment | initStatistics",
                "Cached statistics not available. Loading..."
            )
            loadDataForStatistics()
        }
    }

    private fun loadDataForStatistics() {
        showSessionLoadingStart()

        StatisticUpdateRequest(DokoShortAccess.getGroupCtrl().getGroup().id, DokoShortAccess.getStatsCtrl().getSessionControllers()).execute(object :
            ReadRequestListener<List<ISessionController>> {
            override fun onReadComplete(result: List<ISessionController>) {
                Logging.d("GroupStatisticFragment | loadDataForStatistics", "Sessions loaded")
                calculateAndApplyGroupStatistics(result)
            }

            override fun onReadFailed() {
                showSessionLoadingError()
            }
        })
    }

    private fun calculateAndApplyGroupStatistics(sessions: List<ISessionController>) {
        if (!DokoShortAccess.getStatsCtrl().isCachedStatisticsAvailable()) {
            DokoShortAccess.getStatsCtrl().calculateGroupStatistics(
                DokoShortAccess.getMemberCtrl().getMembers(),
                sessions
            )
        }
        setStatistics(
            GroupStatisticViewProvider(
                DokoShortAccess.getStatsCtrl().getCachedGroupStatistics()
            )
        )
    }

    private fun setupMemberSelect() {
        binding.headerStatisticMemberSelect.setListener(object :
            MemberListHeaderAdapter.OnMemberClickListener {

            override fun onMemberClicked(member: Member) {
                if (DokoShortAccess.getStatsCtrl().getStatus() == StatisticStatus.EMPTY) {
                    setStatistics(EmptyStatisticViewProvider())
                } else {
                    val stats = DokoShortAccess.getStatsCtrl().getCachedGroupStatistics()
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
        // Loading of data might took so long that the binding is already null
        if(_binding == null){
            return
        }

        val withBockSettings = DokoShortAccess.getSettingsCtrl().getSettings().isBockrundeEnabled
        val statisticItems = provider.getStatisticItems(withBockSettings)
        val adapter = StatisticListAdapter(
            requireContext(),
            statisticItems
        )
        binding.lvGroupStatistic.adapter = adapter
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

    override fun onResume() {
        super.onResume()
        Logging.d("GroupStatisticFragment | onResume", "Resuming")

        calculateAndApplyGroupStatistics(
            DokoShortAccess.getStatsCtrl().getSessionControllers()
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
