package de.timseidel.doppelkopf.ui.session.gamehistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.util.DokoUtil
import de.timseidel.doppelkopf.util.PlayerGameResult

class GameHistoryListItemPlayerListAdapter(
    private val playerResults: MutableList<PlayerGameResult> = mutableListOf(),
) :
    RecyclerView.Adapter<GameHistoryListItemPlayerListAdapter.ViewHolder>() {

    class ViewHolder(val ghlipv: GameHistoryListItemPlayerView) : RecyclerView.ViewHolder(ghlipv) {
        fun bind(playerResult: PlayerGameResult) {
            ghlipv.setPlayerTacken(playerResult.tacken)
            val faction = playerResult.playerAndFaction.faction
            ghlipv.setPlayerFaction(faction, DokoUtil.isWinner(faction, playerResult.points))
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

    fun updatePlayerResults(pr: List<PlayerGameResult>){
        this.playerResults.clear()
        this.playerResults.addAll(pr)
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return playerResults.size
    }
}