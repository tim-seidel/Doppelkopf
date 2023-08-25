package de.timseidel.doppelkopf.ui.session.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import de.timseidel.doppelkopf.databinding.FragmentSessionStatisticBinding
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.model.statistic.session.SessionStatistics
import de.timseidel.doppelkopf.model.statistic.session.SessionStatisticsCalculator
import de.timseidel.doppelkopf.ui.session.PlayerListHeaderAdapter.OnPlayerClickListener
import de.timseidel.doppelkopf.ui.statistic.StatisticListAdapter
import de.timseidel.doppelkopf.ui.statistic.provider.EmptyStatisticViewProvider
import de.timseidel.doppelkopf.ui.statistic.provider.IStatisticViewsProvider
import de.timseidel.doppelkopf.ui.statistic.provider.PlayerStatisticViewsProvider
import de.timseidel.doppelkopf.ui.statistic.provider.SessionStatisticViewsProvider
import de.timseidel.doppelkopf.util.DokoShortAccess
import java.lang.Integer.max
import java.lang.Integer.min

class SessionStatisticFragment : Fragment() {

    private val defaultSessionStatsPlayerPlaceholderId = "__default_session"

    private var _binding: FragmentSessionStatisticBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionStatistics: SessionStatistics

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSessionStatisticBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.title =
            DokoShortAccess.getSessionCtrl().getSession().name

        setupStatistics()
        setupPlayerSelect()

        return binding.root
    }

    private fun setupStatistics() {
        sessionStatistics = SessionStatisticsCalculator().calculateSessionStatistics(
            DokoShortAccess.getPlayerCtrl().getPlayers(),
            DokoShortAccess.getGameCtrl().getGames()
        )

        if (DokoShortAccess.getGameCtrl().getGames().isEmpty()) {
            setStatistics(EmptyStatisticViewProvider())
        } else {
            setStatistics(SessionStatisticViewsProvider(sessionStatistics))
        }
    }

    private fun setupPlayerSelect() {
        binding.headerStatisticPlayerSelect.setListener(object : OnPlayerClickListener {
            override fun onPlayerClicked(player: Player) {
                if (player.id == defaultSessionStatsPlayerPlaceholderId) {
                    setStatistics(SessionStatisticViewsProvider(sessionStatistics))
                } else {
                    val playerStatistic =
                        sessionStatistics.playerStatistics.firstOrNull { playerStatistic -> playerStatistic.player.id == player.id }

                    if (playerStatistic != null && playerStatistic.general.total.games > 0) {
                        setStatistics(PlayerStatisticViewsProvider(playerStatistic))
                    } else {
                        setStatistics(EmptyStatisticViewProvider())
                    }
                }
            }
        })

        val playerDefaultGroupStatisticPlaceholder =
            Player(defaultSessionStatsPlayerPlaceholderId, "Alle")
        val players = DokoShortAccess.getPlayerCtrl().getPlayers().toMutableList()
        players.add(0, playerDefaultGroupStatisticPlaceholder)

        binding.headerStatisticPlayerSelect.setRowSize(max(1, min(4, players.size)))
        binding.headerStatisticPlayerSelect.setPlayers(players)
    }

    private fun setStatistics(provider: IStatisticViewsProvider) {
        val lvStatistic = binding.lvStatistic

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
}