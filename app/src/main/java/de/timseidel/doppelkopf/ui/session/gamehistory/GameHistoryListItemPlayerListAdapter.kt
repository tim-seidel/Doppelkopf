package de.timseidel.doppelkopf.ui.session.gamehistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.model.GameType

class GameHistoryListItemPlayerListAdapter(
    private val playerResults: MutableList<GameResult> = mutableListOf(),
) :
    RecyclerView.Adapter<GameHistoryListItemPlayerListAdapter.ViewHolder>() {

    class ViewHolder(private val playerView: GameHistoryListItemPlayerView) :
        RecyclerView.ViewHolder(playerView) {
        fun bind(playerResult: GameResult) {
            val faction = playerResult.faction

            playerView.setPlayerTacken(playerResult.tacken)
            playerView.setPlayerResult(faction, playerResult.isWinner, playerResult.gameType == GameType.SOLO && faction == Faction.RE)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(GameHistoryListItemPlayerView(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = playerResults[position]
        holder.bind(result)
    }

    override fun getItemCount(): Int {
        return playerResults.size
    }

    fun updatePlayerResults(pr: List<GameResult>) {
        this.playerResults.clear()
        this.playerResults.addAll(pr)
        this.notifyDataSetChanged()
    }
}