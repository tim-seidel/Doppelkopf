package de.timseidel.doppelkopf.ui.session

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.Player
import java.lang.Integer.min

class PlayerListHeaderView constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    private lateinit var adapter: PlayerListHeaderAdapter
    private lateinit var rvPlayers: RecyclerView

    private var playerClickListener: PlayerListHeaderAdapter.OnPlayerClickListener? = null

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

    fun setListener(listener: PlayerListHeaderAdapter.OnPlayerClickListener) {
        playerClickListener = listener
    }

    fun setPlayers(players: List<Player>) {
        adapter = PlayerListHeaderAdapter(players)
        adapter.playerClickListener = playerClickListener
        rvPlayers.adapter = adapter
        rvPlayers.layoutManager = GridLayoutManager(rvPlayers.context, min(players.size, 4))
    }
}