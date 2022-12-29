package de.timseidel.doppelkopf.ui.sessionhistory

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import de.timseidel.doppelkopf.R

class SessionHistoryListItemPlayerView constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    private lateinit var tvPlayerName: TextView

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_session_history_list_item_player, this)

        findViews()
        parseAttributes(attrs)
    }

    private fun findViews() {
        tvPlayerName = findViewById(R.id.tv_session_history_item_player_name)
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SessionHistoryListItemPlayerView)
        try {
            val playerName = ta.getString(R.styleable.SessionHistoryListItemPlayerView_name) ?: ""
            setPlayerName(playerName)
        } finally {
            ta.recycle()
        }
    }


    fun setPlayerName(name: String) {
        tvPlayerName.text = name
    }

}