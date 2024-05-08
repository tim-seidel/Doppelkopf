package de.timseidel.doppelkopf.ui.session.gamehistory

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.GameHistoryColumn
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.util.Converter
import kotlin.math.max

class GameHistoryListItemView constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    interface GameHistoryListItemEditListener {
        fun onGameEditClicked()
    }

    private lateinit var tvGameNumber: TextView
    private lateinit var rvPlayerTacken: RecyclerView
    private lateinit var playerTackenAdapter: GameHistoryListItemPlayerListAdapter
    private lateinit var gameId: String
    private var gameEditListener: GameHistoryListItemEditListener? = null

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
        val dp4 = Converter.convertDpToPixels(2f, rvPlayerTacken.context)
        rvPlayerTacken.addItemDecoration(RecyclerViewMarginDecoration(dp4, dp4))

        playerTackenAdapter = GameHistoryListItemPlayerListAdapter()
        rvPlayerTacken.adapter = playerTackenAdapter
    }

    fun setGameEditListener(gameEditListener: GameHistoryListItemEditListener?) {
        this.gameEditListener = gameEditListener
    }

    fun setGameId(gameId: String) {
        this.gameId = gameId
    }

    fun setGameNumber(number: Int) {
        tvGameNumber.text = if (number in 0..9) {
            "0$number"
        } else {
            number.toString()
        }
    }

    fun setIsEditable(isEditable: Boolean) {
        if (isEditable) {
            tvGameNumber.setOnClickListener {
                gameEditListener?.onGameEditClicked()
            }
        } else {
            tvGameNumber.setOnClickListener(null)
        }
    }

    fun setScores(scores: List<GameHistoryColumn>) {
        rvPlayerTacken.apply {
            layoutManager = GridLayoutManager(rvPlayerTacken.context, max(scores.size, 1))
        }
        playerTackenAdapter.updateScores(scores)
    }

    fun setBockStatus(isBockrunde: Boolean) {
        if (isBockrunde) {
            tvGameNumber.setTextColor(ContextCompat.getColor(context, R.color.error))
            tvGameNumber.setTypeface(null, Typeface.ITALIC)
        } else {
            tvGameNumber.setTextColor(ContextCompat.getColor(context, R.color.neural))
            tvGameNumber.setTypeface(null, Typeface.NORMAL)
        }
    }
}