package de.timseidel.doppelkopf.ui.sessionhistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.model.Player

class SessionHistoryListItemPlayerListAdapter(
    private val players: List<Player> = emptyList(),
) :
    RecyclerView.Adapter<SessionHistoryListItemPlayerListAdapter.ViewHolder>() {

    class ViewHolder(private val playerView: SessionHistoryListItemPlayerView) :
        RecyclerView.ViewHolder(playerView) {
        fun bind(player: Player) {
            playerView.setPlayerName(player.name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = SessionHistoryListItemPlayerView(parent.context)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = players[position]
        holder.bind(result)
    }

    override fun getItemCount(): Int {
        return players.size
    }
}