package de.timseidel.doppelkopf.ui.session.gamehistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.model.GameHistoryItem
import de.timseidel.doppelkopf.ui.session.gameedit.GameEditClickListener
import de.timseidel.doppelkopf.util.GameUtil

class GameHistoryListAdapter(
    private var rows: MutableList<GameHistoryItem>,
    var listener: GameEditClickListener? = null
) :
    RecyclerView.Adapter<GameHistoryListItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GameHistoryListItemViewHolder {
        val view = GameHistoryListItemView(parent.context)

        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return GameHistoryListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameHistoryListItemViewHolder, position: Int) {
        val row = rows[position]
        holder.bind(row, GameUtil.isGameEditEnabled(row.game))

        holder.gameView.setGameEditListener(object :
            GameHistoryListItemView.GameHistoryListItemEditListener {
            override fun onGameEditClicked() {
                listener?.onGameEditClicked(row.game.id)
            }
        })
    }

    override fun getItemCount(): Int {
        return rows.size
    }
}