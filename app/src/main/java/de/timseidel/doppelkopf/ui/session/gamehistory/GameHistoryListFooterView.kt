package de.timseidel.doppelkopf.ui.session.gamehistory

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.statistic.PlayerStatistic

class GameHistoryListFooterView constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    private lateinit var rvTotalTacken: RecyclerView

    init {
        init()
    }

    private fun init() {
        View.inflate(context, R.layout.view_game_history_list_footer, this)

        findViews()
    }

    private fun findViews() {
        rvTotalTacken = findViewById(R.id.rv_ghl_player_footer)
    }

    fun setPlayerStatistics(playerStats: List<PlayerStatistic>) {
        val adapter = GameHistoryListPlayerFooterAdapter(playerStats)
        rvTotalTacken.adapter = adapter
        rvTotalTacken.layoutManager = GridLayoutManager(rvTotalTacken.context, playerStats.size)
    }
}