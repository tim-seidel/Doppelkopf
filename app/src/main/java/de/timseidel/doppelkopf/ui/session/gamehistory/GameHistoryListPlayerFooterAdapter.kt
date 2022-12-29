package de.timseidel.doppelkopf.ui.session.gamehistory

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.statistic.PlayerStatistic

class GameHistoryListPlayerFooterAdapter(
    private val playerStatistics: List<PlayerStatistic> = listOf(),
) :
    RecyclerView.Adapter<GameHistoryListPlayerFooterAdapter.ViewHolder>() {

    class ViewHolder(private val tackenView: TextView) : RecyclerView.ViewHolder(tackenView) {
        fun bind(playerStatistic: PlayerStatistic) {
            tackenView.text = playerStatistic.general.total.tacken.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = createPlayerNameView(parent.context)

        return ViewHolder(view)
    }

    private fun createPlayerNameView(context: Context): TextView {
        val tv = TextView(context)
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        tv.layoutParams = layoutParams
        tv.setPadding(0, 8, 0, 8)
        tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tv.textSize = 24f
        tv.setTextColor(ContextCompat.getColor(context, R.color.neural_dark))
        tv.setTypeface(tv.typeface, Typeface.BOLD);

        return tv
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val statistic = playerStatistics[position]
        holder.bind(statistic)
    }

    override fun getItemCount(): Int {
        return playerStatistics.size
    }
}