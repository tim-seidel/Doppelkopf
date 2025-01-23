package de.timseidel.doppelkopf.ui.session.gamehistory

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.GameHistoryColumn
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.util.Converter
import de.timseidel.doppelkopf.util.DokoShortAccess
import kotlin.math.max

class GameHistoryListItemView(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    interface GameHistoryListItemEditListener {
        fun onGameEditClicked()
    }

    private lateinit var ivBockIndicator: ImageView
    private lateinit var rvMemberTacken: RecyclerView
    private lateinit var tvGameNumber: TextView
    private lateinit var memberTackenAdapter: GameHistoryListItemMemberListAdapter
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
        ivBockIndicator = findViewById(R.id.iv_game_bock_indicator)
        rvMemberTacken = findViewById(R.id.rv_game_history_item_member_tacken_list)
        tvGameNumber = findViewById(R.id.tv_game_history_number)
    }

    private fun setupRecyclerView() {
        val dp4 = Converter.convertDpToPixels(2f, rvMemberTacken.context)
        rvMemberTacken.addItemDecoration(RecyclerViewMarginDecoration(dp4, dp4))

        memberTackenAdapter = GameHistoryListItemMemberListAdapter()
        rvMemberTacken.adapter = memberTackenAdapter
    }

    fun setGameEditListener(gameEditListener: GameHistoryListItemEditListener?) {
        this.gameEditListener = gameEditListener
    }

    fun setGameId(gameId: String) {
        this.gameId = gameId
    }

    fun setGameNumber(number: Int, gameType: GameType) {
        when (gameType) {
            GameType.NORMAL -> tvGameNumber.text = if (number in 0..9) {
                "0$number"
            } else {
                number.toString()
            }

            GameType.SCHWARZVERLOREN -> tvGameNumber.text = " V "
            GameType.HOCHZEIT -> tvGameNumber.text = " H "
            GameType.SOLO -> tvGameNumber.text = " S "
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
        rvMemberTacken.apply {
            layoutManager = GridLayoutManager(rvMemberTacken.context, max(scores.size, 1))
        }
        memberTackenAdapter.updateScores(scores)
    }

    fun setBockStatus(isBockrunde: Boolean) {
        val bockEnabled = DokoShortAccess.getSettingsCtrl().getSettings().isBockrundeEnabled
        if (isBockrunde && bockEnabled) {
            tvGameNumber.setTextColor(ContextCompat.getColor(context, R.color.error))
            ivBockIndicator.visibility = VISIBLE
            ivBockIndicator.setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.error
                )
            )
        } else {
            tvGameNumber.setTextColor(ContextCompat.getColor(context, R.color.neural))
            ivBockIndicator.visibility = GONE
            ivBockIndicator.setColorFilter(
                ContextCompat.getColor(context, R.color.neural)
            )
        }
    }
}