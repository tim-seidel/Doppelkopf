package de.timseidel.doppelkopf.ui.session.gamehistory

import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.model.GameHistoryColumn
import de.timseidel.doppelkopf.model.GameHistoryItem
import de.timseidel.doppelkopf.model.GameResult

class GameHistoryListItemViewHolder(val gameView: GameHistoryListItemView) :
    RecyclerView.ViewHolder(gameView) {
    fun bind(
        model: GameHistoryItem,
        isEditable: Boolean,
    ) {
        gameView.setGameId(model.game.id)
        gameView.setGameNumber(model.number, model.game.gameType)
        gameView.setScores(model.scores)
        gameView.setBockStatus(model.isBockrunde)
        gameView.setIsEditable(isEditable)
    }
}