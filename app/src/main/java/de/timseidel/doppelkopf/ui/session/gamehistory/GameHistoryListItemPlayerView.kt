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
                        if (isWinner) R.color.teal else R.color.gray_400
                    )
                )
            }
            Faction.CONTRA -> {
                ivPlayerFaction.setImageResource(R.drawable.ic_cards_diamond_24)
                ivPlayerFaction.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        if (isWinner) R.color.deep_purple else R.color.gray_400
                    )
                )
            }
            Faction.NONE -> {
                ivPlayerFaction.setImageResource(R.drawable.ic_baseline_remove_black_24)
                ivPlayerFaction.setColorFilter(ContextCompat.getColor(context, R.color.gray_600))
            }
        }

        //background = if(isWinner) ContextCompat.getDrawable(context, R.drawable.border_background) else null
    }

}