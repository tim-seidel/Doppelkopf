package de.timseidel.doppelkopf.ui.session.gamehistory

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.databinding.FragmentGameHistoryBinding
import de.timseidel.doppelkopf.model.statistic.StatisticUtil
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.session.gameedit.GameEditActivity
import de.timseidel.doppelkopf.ui.session.gameedit.GameEditClickListener
import de.timseidel.doppelkopf.ui.util.Converter
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.GameUtil
import de.timseidel.doppelkopf.util.Logging
import kotlin.math.max


class GameHistoryFragment : Fragment() {

    private var _binding: FragmentGameHistoryBinding? = null
    private val binding get() = _binding!!

    private var isGameHistoryAccumulated = true
    private lateinit var gameHistoryListAdapter: GameHistoryListAdapter
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private val gameEditClickListener = object : GameEditClickListener {
        override fun onGameEditClicked(gameId: String) {
            val intent = Intent(context, GameEditActivity::class.java)
            intent.putExtra(GameEditActivity.KEY_GAME_EDIT_ID, gameId)
            activityResultLauncher.launch(intent)
        }
    }

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
        setupEditResultLauncher()

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

        setupToolbar()
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
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
            RecyclerViewMarginDecoration(
                0,
                Converter.convertDpToPixels(1f, listView.context)
            )
        )
    }

    private fun setGameHistory(accumulated: Boolean) {
        gameHistoryListAdapter = createAdapter(accumulated, gameEditClickListener)
        binding.rvGameHistoryList.adapter = gameHistoryListAdapter
    }

    private fun createAdapter(
        accumulated: Boolean,
        listener: GameEditClickListener
    ): GameHistoryListAdapter {
        val games = DokoShortAccess.getGameCtrl().getGames()
        val history = GameUtil.getGameHistory(games)

        Logging.d("GameHistoryFragment", "History: Updating history")
        return if (accumulated) {
            val accumulatedHistory = StatisticUtil.accumulateGameHistory(history)
            return GameHistoryListAdapter(accumulatedHistory.reversed().toMutableList(), listener)
        } else {
            GameHistoryListAdapter(history.reversed().toMutableList(), listener)
        }
    }

    private fun setupEditResultLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    Logging.d("GameHistoryFragment", "Result ok")
                    setGameHistory(isGameHistoryAccumulated)
                    //gameHistoryListAdapter.notifyItemChanged(0)
                } else {
                    Logging.d("GameHistoryFragment", "Result not ok")
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}