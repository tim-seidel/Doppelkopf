package de.timseidel.doppelkopf.ui.session.gamehistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.model.Game

//TODO: Vllt. Gamenummer ausblenden lassen koennen
class GameHistoryListAdapter(
    private val games: List<Game>
) : RecyclerView.Adapter<GameHistoryListAdapter.ViewHolder>() {

    class ViewHolder(private val gameView: GameHistoryListItemView) :
        RecyclerView.ViewHolder(gameView) {
        fun bind(game: Game, number: Int) {
            gameView.setGameNumber(number)
            gameView.setGame(game)
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
        holder.bind(game, games.size - position)
    }

    override fun getItemCount(): Int {
        return games.size
    }
}