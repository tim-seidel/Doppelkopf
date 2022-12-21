package de.timseidel.doppelkopf.ui.session.gamehistory

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.Player

//TODO. Mit ClickListener und dann fuer Statistikauswahl verwendbar machen
class GameHistoryListHeaderView constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    private lateinit var rvPlayers: RecyclerView

    init {
        init()
    }

    private fun init() {
        View.inflate(context, R.layout.view_game_history_list_header, this)

        findViews()
    }

    private fun findViews() {
        rvPlayers = findViewById(R.id.rv_ghl_player_header)
    }

    fun setPlayers(players: List<Player>) {
        val adapter = GameHistoryListPlayerHeaderAdapter(players)
        rvPlayers.adapter = adapter
        rvPlayers.layoutManager = GridLayoutManager(rvPlayers.context, players.size)
    }
}