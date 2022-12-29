package de.timseidel.doppelkopf.ui.session.gamehistory

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.util.Logging

class GameHistoryListPlayerHeaderAdapter(
    private val players: List<Player> = listOf(),
) :
    RecyclerView.Adapter<GameHistoryListPlayerHeaderAdapter.ViewHolder>() {

    class ViewHolder(val playerView: TextView) : RecyclerView.ViewHolder(playerView) {
        fun bind(player: Player) {
            playerView.text = player.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = createPlayerNameView(parent.context)

        return ViewHolder(view)
    }

    private fun createPlayerNameView(context: Context): TextView {
        val tv = TextView(context)
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        tv.layoutParams = layoutParams
        tv.setPadding(0,8, 0, 8)
        tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tv.textSize = 22f
        tv.setTypeface(tv.typeface, Typeface.BOLD);

        return tv
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = players[position]
        holder.bind(player)
    }

    override fun getItemCount(): Int {
        return players.size
    }
}