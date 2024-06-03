package de.timseidel.doppelkopf.ui.session.gamehistory

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.Faction

class GameHistoryListItemPlayerView constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private lateinit var tvPlayerTacken: TextView
    private lateinit var ivPlayerFaction: ImageView
    private lateinit var clRoot: ConstraintLayout

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_game_history_list_item_player, this)
        findViews()

        applyAttributes(attrs)
    }

    private fun findViews() {
        tvPlayerTacken = findViewById(R.id.tv_ghli_player_tacken)
        ivPlayerFaction = findViewById(R.id.iv_ghli_player_faction)
        clRoot = findViewById(R.id.root_game_history_list_item_player)
    }

    private fun applyAttributes(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GameHistoryListItemPlayerView)
        try {
            val memberTacken =
                ta.getInteger(R.styleable.GameHistoryListItemPlayerView_playerTacken, 0)
            setPlayerTacken(memberTacken)
        } finally {
            ta.recycle()
        }
    }

    fun setPlayerTacken(tacken: Int) {
        tvPlayerTacken.text = tacken.toString()
    }

    fun setPlayerResult(faction: Faction, isWinner: Boolean, isSolo: Boolean) {
        when (faction) {
            Faction.RE -> {
                ivPlayerFaction.setImageResource(R.drawable.ic_card_clubs_24)
                ivPlayerFaction.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        if (isWinner) R.color.primary else R.color.gray_400
                    )
                )
                tvPlayerTacken.visibility = VISIBLE
            }

            Faction.CONTRA -> {
                ivPlayerFaction.setImageResource(R.drawable.ic_card_diamonds_24)
                ivPlayerFaction.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        if (isWinner) R.color.secondary else R.color.gray_400
                    )
                )
                tvPlayerTacken.visibility = VISIBLE
            }

            Faction.NONE -> {
                ivPlayerFaction.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
                ivPlayerFaction.setColorFilter(ContextCompat.getColor(context, R.color.gray_400))
                tvPlayerTacken.visibility = INVISIBLE
                tvPlayerTacken.text = "0"
                tvPlayerTacken.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        }

        clRoot.backgroundTintList =
            ContextCompat.getColorStateList(
                context,
                if (isWinner && isSolo) R.color.gray_400
                else if (isWinner) R.color.gray_300
                else R.color.gray_200
            )
    }
}