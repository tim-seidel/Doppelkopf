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

class GameHistoryListItemMemberView constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private lateinit var tvMemberTacken: TextView
    private lateinit var ivMemberFaction: ImageView
    private lateinit var clRoot: ConstraintLayout

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_game_history_list_item_member, this)
        findViews()

        applyAttributes(attrs)
    }

    private fun findViews() {
        tvMemberTacken = findViewById(R.id.tv_ghli_member_tacken)
        ivMemberFaction = findViewById(R.id.iv_ghli_member_faction)
        clRoot = findViewById(R.id.root_game_history_list_item_member)
    }

    private fun applyAttributes(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GameHistoryListItemMemberView)
        try {
            val memberTacken =
                ta.getInteger(R.styleable.GameHistoryListItemMemberView_memberTacken, 0)
            setMemberTacken(memberTacken)
        } finally {
            ta.recycle()
        }
    }

    fun setMemberTacken(tacken: Int) {
        tvMemberTacken.text = tacken.toString()
    }

    fun setMemberResult(faction: Faction, isWinner: Boolean, isSolo: Boolean) {
        when (faction) {
            Faction.RE -> {
                ivMemberFaction.setImageResource(R.drawable.ic_card_clubs_24)
                ivMemberFaction.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        if (isWinner) R.color.primary else R.color.gray_400
                    )
                )
                tvMemberTacken.visibility = VISIBLE
            }

            Faction.CONTRA -> {
                ivMemberFaction.setImageResource(R.drawable.ic_card_diamonds_24)
                ivMemberFaction.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        if (isWinner) R.color.secondary else R.color.gray_400
                    )
                )
                tvMemberTacken.visibility = VISIBLE
            }

            Faction.NONE -> {
                ivMemberFaction.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
                ivMemberFaction.setColorFilter(ContextCompat.getColor(context, R.color.gray_400))
                tvMemberTacken.visibility = INVISIBLE
                tvMemberTacken.text = "0"
                tvMemberTacken.setTextColor(ContextCompat.getColor(context, R.color.black))
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