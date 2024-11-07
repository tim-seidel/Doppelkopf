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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.databinding.FragmentRankingBinding
import de.timseidel.doppelkopf.db.request.SessionListRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.model.Ranking
import de.timseidel.doppelkopf.model.RankingItem
import de.timseidel.doppelkopf.model.statistic.RankingStatisticsProvider
import de.timseidel.doppelkopf.model.statistic.group.GroupStatistics
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.util.Converter
import de.timseidel.doppelkopf.util.DokoShortAccess

class RankingFragment : Fragment() {

    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!

    private val rankingListAdapter: RankingListAdapter = RankingListAdapter(mutableListOf())
    private var rankings = mutableListOf<Ranking>()
    private var currentRankingIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)

        setupRankingTitle()
        setupButtons()
        setupRankingList()

        setupStatistics()

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

    private fun setupRankingTitle() {
        setRankingTitle("Hier werden gleich die Rankings angezeigt :)")
    }

    private fun setupButtons() {
        binding.btnRankingNext.setOnClickListener { showNextRanking() }
        binding.btnRankingPrevious.setOnClickListener { showPreviousRanking() }
    }

    private fun showNextRanking() {
        if (rankings.size > 0) {
            currentRankingIndex = Math.floorMod(currentRankingIndex + 1, rankings.size)
            setRanking(rankings[currentRankingIndex])
        }
    }

    private fun showPreviousRanking() {
        if (rankings.size > 0) {
            currentRankingIndex = Math.floorMod(currentRankingIndex - 1, rankings.size)
            setRanking(rankings[currentRankingIndex])
        }
    }

    private fun setRanking(ranking: Ranking) {
        setRankingTitle(ranking.title)
        setRankingList(ranking.items)
        setRankingDescription(ranking.description)
    }

    private fun setRankingTitle(title: String) {
        binding.tvRankingTitle.text = title
    }

    private fun setRankingDescription(description: String) {
        binding.tvRankingDescription.text = description
        if (description.trim().isEmpty()) {
            binding.tvRankingDescription.visibility = View.GONE
        } else {
            binding.tvRankingDescription.visibility = View.VISIBLE
        }
    }

    private fun setRankingList(rankings: List<RankingItem>) {
        rankingListAdapter.updateRanking(rankings)
    }

    private fun setupRankingList() {
        binding.rvRanking.adapter = rankingListAdapter
        binding.rvRanking.layoutManager = LinearLayoutManager(context)
        binding.rvRanking.addItemDecoration(
            DividerItemDecoration(
                binding.rvRanking.context, LinearLayoutManager.VERTICAL
            )
        )
        binding.rvRanking.addItemDecoration(
            RecyclerViewMarginDecoration(
                0, Converter.convertDpToPixels(4f, binding.rvRanking.context)
            )
        )
    }

    private fun setupStatistics() {
        if (DokoShortAccess.getStatsCtrl().isCachedStatisticsAvailable()) {
            calculateRankings(DokoShortAccess.getStatsCtrl().getCachedGroupStatistics())
        } else {
            loadDataForStatistics()
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
        if (!DokoShortAccess.getStatsCtrl().isCachedStatisticsAvailable()) {
            DokoShortAccess.getStatsCtrl().calculateGroupStatistics(
                DokoShortAccess.getMemberCtrl().getMembers(),
                sessions
            )
        }
        calculateRankings(DokoShortAccess.getStatsCtrl().getCachedGroupStatistics())
    }

    private fun calculateRankings(groupStatistics: GroupStatistics) {
        val withBockSettings = DokoShortAccess.getSettingsCtrl().getSettings().isBockrundeEnabled
        rankings = RankingStatisticsProvider().getRankings(groupStatistics, withBockSettings)
            .toMutableList()

        if (rankings.isNotEmpty()) {
            setRanking(rankings.first())
        } else {
            setRanking(
                Ranking(
                    "Keine Statistiken vorhanden",
                    "Sobald Spiele vorhanden und Statistiken berechnet wurden, werden sie hier angezeigt.",
                    listOf()
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.btnRankingNext.setOnClickListener(null)
        binding.btnRankingPrevious.setOnClickListener(null)
        _binding = null
    }
}