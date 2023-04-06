package de.timseidel.doppelkopf.ui.session.gamehistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.model.GameResult

class AccumulatedGameHistoryListAdapter(
    private val games: List<List<GameResult>>
) : RecyclerView.Adapter<AccumulatedGameHistoryListAdapter.ViewHolder>() {

    class ViewHolder(private val gameView: GameHistoryListItemView) :
        RecyclerView.ViewHolder(gameView) {
        fun bind(number: Int, playerResults: List<GameResult>) {
            gameView.setGameNumber(number)
            gameView.setPlayerResults(playerResults)
            gameView.setBockStatus(if (playerResults.isNotEmpty()) playerResults.first().isBockrunde else false)
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
        val playerResults = games[position]
        holder.bind(games.size - position, playerResults)
    }

    override fun getItemCount(): Int {
        return games.size
    }
}