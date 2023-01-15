package de.timseidel.doppelkopf.ui.session.gamehistory

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.Faction

class GameHistoryListItemPlayerView constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    private lateinit var tvPlayerTacken: TextView
    private lateinit var ivPlayerFaction: ImageView

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_game_history_list_item_player, this)

        tvPlayerTacken = findViewById(R.id.tv_ghli_player_tacken)
        ivPlayerFaction = findViewById(R.id.iv_ghli_player_faction)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.GameHistoryListItemPlayerView)
        try {
            val playerTacken =
                ta.getInteger(R.styleable.GameHistoryListItemPlayerView_playerTacken, 0)
            //val factionAsString = ta.getString(R.styleable.GameHistoryListItemPlayerView_playerFaction)

            setPlayerTacken(playerTacken)
        } finally {
            ta.recycle()
        }
    }

    fun setPlayerTacken(tacken: Int) {
        tvPlayerTacken.text = tacken.toString()
    }

    fun setPlayerFaction(faction: Faction, isWinner: Boolean) {
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
                ivPlayerFaction.setImageResource(R.drawable.ic_cards_diamond_24)
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
            }
        }
    }

    fun setSolo(isSolist: Boolean){
        background = if(isSolist) ContextCompat.getDrawable(context, R.drawable.border_background) else null
    }
}