package de.timseidel.doppelkopf.ui.sessionhistory

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import de.timseidel.doppelkopf.R

class SessionHistoryListItemMemberView constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    private lateinit var tvMemberName: TextView

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_session_history_list_item_player, this)

        findViews()
        parseAttributes(attrs)
    }

    private fun findViews() {
        tvMemberName = findViewById(R.id.tv_session_history_item_player_name)
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SessionHistoryListItemPlayerView)
        try {
            val memberName = ta.getString(R.styleable.SessionHistoryListItemPlayerView_name) ?: ""
            setMemberName(memberName)
        } finally {
            ta.recycle()
        }
    }


    fun setMemberName(name: String) {
        tvMemberName.text = name
    }

}