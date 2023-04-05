package de.timseidel.doppelkopf.ui.group.statistic

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.RankingItem

class RankingListAdapter(private val rankingData: MutableList<RankingItem>) :
    RecyclerView.Adapter<RankingListAdapter.ViewHolder>() {

    class ViewHolder(val view: RankingItemView) : RecyclerView.ViewHolder(view) {
        fun bind(item: RankingItem, position: Int) {
            view.setName(item.name)
            view.setValue(item.value)
            view.setBackGroundColor(
                ContextCompat.getColor(
                    view.context,
                    when (position) {
                        0 -> R.color.gold
                        1 -> R.color.silver
                        2 -> R.color.bronce
                        else -> R.color.transparent
                    }
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RankingItemView(parent.context)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = rankingData[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int {
        return rankingData.size
    }

    fun updateRanking(ranking: List<RankingItem>) {
        rankingData.clear()
        rankingData.addAll(ranking)
        notifyDataSetChanged()
    }
}