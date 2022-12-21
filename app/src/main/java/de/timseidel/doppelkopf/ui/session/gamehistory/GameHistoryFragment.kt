package de.timseidel.doppelkopf.ui.session.gamehistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.timseidel.doppelkopf.controller.DoppelkopfManager
import de.timseidel.doppelkopf.databinding.FragmentGameHistoryBinding
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.statistic.PlayerStatisticsCalculator
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.util.Converter
import de.timseidel.doppelkopf.util.DokoShortAccess

//TODO: Rang und Gesamtpunktzahl
class  GameHistoryFragment : Fragment() {

    private var _binding: FragmentGameHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var gameHistoryListAdapter: GameHistoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameHistoryBinding.inflate(inflater, container, false)

        if (DokoShortAccess.getGameCtrl().getGames().isEmpty()) {
            createSampleGames()
        }
        setupPlayerHeader()
        setupTackenFooter()
        setupGameHistoryList()

        return binding.root
    }

    private fun setupPlayerHeader() {
        binding.headerGameHistoryList.setPlayers(
            DokoShortAccess.getPlayerCtrl().getPlayers()
        )
    }

    private fun setupTackenFooter() {
        val stats = PlayerStatisticsCalculator().calculatePlayerStatistic(
            DokoShortAccess.getPlayerCtrl().getPlayers(),
            DokoShortAccess.getGameCtrl().getGames()
        )

        //binding.footerGameHistoryList.setPlayerStatistics(stats)
    }

    private fun setupGameHistoryList() {
        val games =
            DoppelkopfManager.getInstance().getSessionController().getGameController().getGames()
        gameHistoryListAdapter = GameHistoryListAdapter(games)

        val listView = binding.rvGameHistoryList
        listView.adapter = gameHistoryListAdapter
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
                Converter.convertDpToPixels(4f, listView.context)
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createSampleGames() {
        for (i in (1..40)) {
            DoppelkopfManager.getInstance().getSessionController().getGameController()
                .addGame(getSampleGame())
        }
    }

    private fun getSampleGame(): Game {
        val pafs = DoppelkopfManager.getInstance().getSessionController().getPlayerController()
            .getPlayersAsFaction()
        val reContra = listOf(Faction.RE, Faction.RE, Faction.CONTRA, Faction.CONTRA).shuffled()
        for (i in 0..3) pafs[i].faction = reContra[i]

        return DoppelkopfManager.getInstance().getSessionController().getGameController()
            .createGame(
                pafs,
                if ((0..100).random() <= 60) Faction.RE else Faction.CONTRA,
                (121..240).random(),
                (-1..8).random()
            )
    }
}