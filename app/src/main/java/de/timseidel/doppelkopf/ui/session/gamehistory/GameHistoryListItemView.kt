package de.timseidel.doppelkopf.ui.session.gamehistory

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.controller.DoppelkopfManager
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.util.Converter
import de.timseidel.doppelkopf.util.PlayerGameResult

class GameHistoryListItemView constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private lateinit var rvPlayerTacken: RecyclerView
    private lateinit var playerTackenAdapter: GameHistoryListItemPlayerListAdapter

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_game_history_list_item, this)

        findViews()
        parseAttributes(attrs)
        setupRecyclerView()
    }

    private fun findViews() {
        rvPlayerTacken = findViewById(R.id.rv_game_history_item_player_tacken_list)
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GameHistoryListItemView)
        try {
            val gameScore = ta.getInteger(R.styleable.GameHistoryListItemView_gameScore, 0)
            val gameTacken = ta.getInteger(R.styleable.GameHistoryListItemView_gameTacken, 0)
        } finally {
            ta.recycle()
        }
    }

    private fun setupRecyclerView() {
        val dp4 = Converter.convertDpToPixels(4f, rvPlayerTacken.context)
        rvPlayerTacken.addItemDecoration(RecyclerViewMarginDecoration(dp4, dp4))

        playerTackenAdapter = GameHistoryListItemPlayerListAdapter()
        rvPlayerTacken.adapter = playerTackenAdapter
    }

    fun setGame(game: Game) {
        val playerResults =
            DoppelkopfManager.getInstance().getSessionController().getGameController()
                .getGameAsPlayerResults(game)
        setResultsPlayers(playerResults)
    }

    fun setResultsPlayers(playerResults: List<PlayerGameResult>) {
        rvPlayerTacken.apply {
            layoutManager = GridLayoutManager(rvPlayerTacken.context, playerResults.size)
        }
        playerTackenAdapter.updatePlayerResults(playerResults)
    }
}