package de.timseidel.doppelkopf.ui.session.gamehistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.GameResult

class GameHistoryListItemPlayerListAdapter(
    private val playerResults: MutableList<GameResult> = mutableListOf(),
) :
    RecyclerView.Adapter<GameHistoryListItemPlayerListAdapter.ViewHolder>() {

    class ViewHolder(val ghlipv: GameHistoryListItemPlayerView) : RecyclerView.ViewHolder(ghlipv) {
        fun bind(playerResult: GameResult) {
            val faction = playerResult.faction

            ghlipv.setPlayerTacken(playerResult.tacken)
            ghlipv.setPlayerFaction(faction, playerResult.isWinner)
            ghlipv.setSolo(playerResult.gameType == GameType.SOLO && faction == Faction.RE)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = GameHistoryListItemPlayerView(parent.context)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = playerResults[position]
        holder.bind(result)
    }

    fun updatePlayerResults(pr: List<GameResult>) {
        this.playerResults.clear()
        this.playerResults.addAll(pr)
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return playerResults.size
    }
}