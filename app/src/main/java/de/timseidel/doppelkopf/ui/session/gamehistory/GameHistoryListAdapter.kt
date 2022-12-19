package de.timseidel.doppelkopf.ui.session.gamehistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.model.Game

class GameHistoryListAdapter(
    private val games: List<Game>
) : RecyclerView.Adapter<GameHistoryListAdapter.ViewHolder>() {

    class ViewHolder(private val gameHistoryListItemView: GameHistoryListItemView) :
        RecyclerView.ViewHolder(gameHistoryListItemView) {
        fun bind(game: Game) {
            gameHistoryListItemView.setGame(game)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = GameHistoryListItemView(parent.context)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val game = games[position]
        holder.bind(game)
    }

    override fun getItemCount(): Int {
        return games.size
    }
}