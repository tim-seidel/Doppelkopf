package de.timseidel.doppelkopf.ui.session.gamehistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.controller.DoppelkopfManager
import de.timseidel.doppelkopf.model.Game


class GameHistoryListAdapter(
    private val games: List<Game>
) : RecyclerView.Adapter<GameHistoryListAdapter.ViewHolder>() {

    class ViewHolder(private val gameView: GameHistoryListItemView) :
        RecyclerView.ViewHolder(gameView) {
        fun bind(game: Game, number: Int) {
            val playerResults =
                DoppelkopfManager.getInstance().getSessionController().getGameController()
                    .getGameAsPlayerResults(game)

            gameView.setGameNumber(number)
            gameView.setPlayerResults(playerResults)
            gameView.setBockStatus(game.isBockrunde)
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