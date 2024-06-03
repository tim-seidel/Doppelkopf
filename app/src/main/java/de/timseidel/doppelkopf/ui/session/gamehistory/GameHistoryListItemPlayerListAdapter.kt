package de.timseidel.doppelkopf.ui.session.gamehistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.model.GameHistoryColumn

class GameHistoryListItemPlayerListAdapter(
    private val scores: MutableList<GameHistoryColumn> = mutableListOf(),
) :
    RecyclerView.Adapter<GameHistoryListItemPlayerListAdapter.ViewHolder>() {

    class ViewHolder(private val memberView: GameHistoryListItemPlayerView) :
        RecyclerView.ViewHolder(memberView) {
        fun bind(score: GameHistoryColumn) {
            val faction = score.faction

            memberView.setPlayerTacken(score.score)
            memberView.setPlayerResult(
                faction,
                score.isWinner,
                score.isSolo
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(GameHistoryListItemPlayerView(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = scores[position]
        holder.bind(result)
    }

    override fun getItemCount(): Int {
        return scores.size
    }

    fun updateScores(scores: List<GameHistoryColumn>) {
        this.scores.clear()
        this.scores.addAll(scores)
        this.notifyDataSetChanged()
    }
}