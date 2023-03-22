package de.timseidel.doppelkopf.ui.session.gamehistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.timseidel.doppelkopf.databinding.FragmentGameHistoryBinding
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.util.Converter
import de.timseidel.doppelkopf.util.DokoShortAccess

//TODO: Rang und Gesamtpunktzahl
class GameHistoryFragment : Fragment() {

    private var _binding: FragmentGameHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var gameHistoryListAdapter: GameHistoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameHistoryBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = DokoShortAccess.getSessionCtrl().getSession().name

        setupPlayerHeader()
        setupGameHistoryList()

        return binding.root
    }

    private fun setupPlayerHeader() {
        binding.headerGameHistoryList.setPlayers(
            DokoShortAccess.getPlayerCtrl().getPlayers()
        )
    }

    private fun setupGameHistoryList() {
        val games = DokoShortAccess.getGameCtrl().getGames().asReversed()
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
}