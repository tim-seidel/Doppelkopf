package de.timseidel.doppelkopf.ui.session.gamehistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.databinding.FragmentGameHistoryBinding
import de.timseidel.doppelkopf.model.statistic.StatisticUtil
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.util.Converter
import de.timseidel.doppelkopf.util.DokoShortAccess
import kotlin.math.max


class GameHistoryFragment : Fragment() {

    private var _binding: FragmentGameHistoryBinding? = null
    private val binding get() = _binding!!

    private var isGameHistoryAccumulated = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameHistoryBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.title =
            DokoShortAccess.getSessionCtrl().getSession().name

        setupPlayerHeader()
        setupGameHistoryList()

        setGameHistory(isGameHistoryAccumulated)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost = requireHost() as MenuHost
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_game_history, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                if (item.itemId == R.id.menu_item_change_game_history_mode) {
                    isGameHistoryAccumulated = !isGameHistoryAccumulated
                    setGameHistory(isGameHistoryAccumulated)
                    return true
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupPlayerHeader() {
        val players = DokoShortAccess.getPlayerCtrl().getPlayers()
        binding.headerGameHistoryList.setRowSize(max(1, players.size))
        binding.headerGameHistoryList.setPlayers(players)
    }

    private fun setupGameHistoryList() {
        val listView = binding.rvGameHistoryList
        listView.layoutManager = LinearLayoutManager(context)
        listView.addItemDecoration(
            DividerItemDecoration(
                listView.context,
                LinearLayoutManager.VERTICAL
            )
        )
        listView.addItemDecoration(
            RecyclerViewMarginDecoration(
                0,
                Converter.convertDpToPixels(12f, listView.context)
            )
        )
    }

    private fun setGameHistory(accumulated: Boolean) {
        val games = DokoShortAccess.getGameCtrl().getGames()

        if (accumulated) {
            val playerResults = DokoShortAccess.getGameCtrl().getGamesAsPlayerResults()
            val accumulatedGameResults =
                StatisticUtil.convertGameHistoriesToAccumulatedHistories(playerResults)

            binding.rvGameHistoryList.adapter =
                AccumulatedGameHistoryListAdapter(accumulatedGameResults.reversed())
        } else {
            binding.rvGameHistoryList.adapter = GameHistoryListAdapter(games.reversed())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}