package de.timseidel.doppelkopf.ui.group.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.databinding.FragmentRankingBinding
import de.timseidel.doppelkopf.db.request.StatisticUpdateRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.model.Ranking
import de.timseidel.doppelkopf.model.RankingItem
import de.timseidel.doppelkopf.model.statistic.RankingStatisticsCalculator
import de.timseidel.doppelkopf.model.statistic.group.GroupStatistics
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.util.Converter
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.Logging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RankingFragment : Fragment() {

    private enum class RankingUiState {
        IDLE,
        LOADING_SESSIONS,
        CALCULATING,
        ERROR
    }

    private var _binding: FragmentRankingBinding? = null
    private var currentUiState: RankingUiState = RankingUiState.IDLE
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
        // Loading of data might took so long that the binding is already null
        if(_binding == null){
            return
        }

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
            calculateAndSetRankings(DokoShortAccess.getStatsCtrl().getCachedGroupStatistics())
            renderState(RankingUiState.IDLE)
        } else {
            Logging.d(
                "RankingFragment | setupStatistics",
                "Cached statistics not available. Loading..."
            )
            loadDataForStatistics()
        }
    }

    private fun loadDataForStatistics() {
        if (currentUiState == RankingUiState.LOADING_SESSIONS || currentUiState == RankingUiState.CALCULATING) {
            return
        }
        renderState(RankingUiState.LOADING_SESSIONS)

        StatisticUpdateRequest(DokoShortAccess.getGroupCtrl().getGroup().id, DokoShortAccess.getStatsCtrl().getSessionControllers()).execute(object :
            ReadRequestListener<List<ISessionController>> {
            override fun onReadComplete(result: List<ISessionController>) {
                calculateAndApplyGroupStatistics(result, forceRecalculation = true)
            }

            override fun onReadFailed() {
                renderState(RankingUiState.ERROR, getString(R.string.group_statistic_loading_error))
            }
        })
    }

    private fun calculateAndApplyGroupStatistics(
        sessions: List<ISessionController>,
        forceRecalculation: Boolean = false
    ) {
        val shouldCalculate = forceRecalculation || !DokoShortAccess.getStatsCtrl().isCachedStatisticsAvailable()
        if (shouldCalculate) {
            renderState(RankingUiState.CALCULATING)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.Default) {
                if (shouldCalculate) {
                    DokoShortAccess.getStatsCtrl().calculateGroupStatistics(
                        DokoShortAccess.getMemberCtrl().getMembers(),
                        sessions
                    )
                }
            }

            if (_binding == null) {
                return@launch
            }

            calculateAndSetRankings(DokoShortAccess.getStatsCtrl().getCachedGroupStatistics())
            renderState(RankingUiState.IDLE)
        }
    }

    private fun calculateAndSetRankings(groupStatistics: GroupStatistics) {
        val withBockSettings = DokoShortAccess.getSettingsCtrl().getSettings().isBockrundeEnabled
        rankings = RankingStatisticsCalculator().getRankings(groupStatistics, withBockSettings)
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

    private fun renderState(state: RankingUiState, errorMessage: String? = null) {
        if (_binding == null) {
            return
        }
        currentUiState = state

        val isBusy = state == RankingUiState.LOADING_SESSIONS || state == RankingUiState.CALCULATING
        binding.rvRanking.isEnabled = !isBusy
        binding.btnRankingNext.isEnabled = !isBusy
        binding.btnRankingPrevious.isEnabled = !isBusy

        when (state) {
            RankingUiState.IDLE -> {
                binding.layoutRankingStateOverlay.visibility = View.GONE
                binding.pbRankingLoading.visibility = View.GONE
            }

            RankingUiState.LOADING_SESSIONS -> {
                binding.layoutRankingStateOverlay.visibility = View.VISIBLE
                binding.pbRankingLoading.visibility = View.VISIBLE
                binding.tvRankingStateMessage.text =
                    getString(R.string.group_statistic_loading_sessions)
            }

            RankingUiState.CALCULATING -> {
                binding.layoutRankingStateOverlay.visibility = View.VISIBLE
                binding.pbRankingLoading.visibility = View.VISIBLE
                binding.tvRankingStateMessage.text =
                    getString(R.string.group_statistic_calculating)
            }

            RankingUiState.ERROR -> {
                binding.layoutRankingStateOverlay.visibility = View.VISIBLE
                binding.pbRankingLoading.visibility = View.GONE
                binding.tvRankingStateMessage.text =
                    errorMessage ?: getString(R.string.group_statistic_loading_error)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Logging.d("RankingFragment | onResume", "Resuming")

        if (currentUiState == RankingUiState.LOADING_SESSIONS || currentUiState == RankingUiState.CALCULATING) {
            return
        }

        if (DokoShortAccess.getStatsCtrl().isCachedStatisticsAvailable()) {
            calculateAndSetRankings(DokoShortAccess.getStatsCtrl().getCachedGroupStatistics())
            renderState(RankingUiState.IDLE)
        } else {
            loadDataForStatistics()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.btnRankingNext.setOnClickListener(null)
        binding.btnRankingPrevious.setOnClickListener(null)
        currentUiState = RankingUiState.IDLE
        _binding = null
    }
}
