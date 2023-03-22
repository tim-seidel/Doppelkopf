package de.timseidel.doppelkopf.ui.session.gamehistory

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.controller.DoppelkopfManager
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.util.Converter

class GameHistoryListItemView constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private lateinit var tvGameNumber: TextView
    private lateinit var rvPlayerTacken: RecyclerView
    private lateinit var playerTackenAdapter: GameHistoryListItemPlayerListAdapter

    init {
        init()
    }

    private fun init() {
        View.inflate(context, R.layout.view_game_history_list_item, this)

        findViews()
        setupRecyclerView()
    }

    private fun findViews() {
        tvGameNumber = findViewById(R.id.tv_game_history_number)
        rvPlayerTacken = findViewById(R.id.rv_game_history_item_player_tacken_list)
    }

    private fun setupRecyclerView() {
        val dp4 = Converter.convertDpToPixels(4f, rvPlayerTacken.context)
        rvPlayerTacken.addItemDecoration(RecyclerViewMarginDecoration(dp4, dp4))

        playerTackenAdapter = GameHistoryListItemPlayerListAdapter()
        rvPlayerTacken.adapter = playerTackenAdapter
    }

    fun setGameNumber(number: Int) {
        tvGameNumber.text = if (number in 0..9) "0$number" else number.toString()
    }

    fun setGame(game: Game) {
        val playerResults =
            DoppelkopfManager.getInstance().getSessionController().getGameController()
                .getGameAsPlayerResults(game)
        setResultsPlayers(playerResults)

        if (game.isBockrunde) {
            tvGameNumber.setTextColor(ContextCompat.getColor(context, R.color.error))
        } else {
            tvGameNumber.setTextColor(ContextCompat.getColor(context, R.color.neural))
        }
    }

    fun setResultsPlayers(playerResults: List<GameResult>) {
        rvPlayerTacken.apply {
            layoutManager = GridLayoutManager(rvPlayerTacken.context, playerResults.size)
        }
        playerTackenAdapter.updatePlayerResults(playerResults)
    }
}