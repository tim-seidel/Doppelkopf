package de.timseidel.doppelkopf.ui.session

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.Player

class PlayerListHeaderAdapter(
    private val players: List<Player> = listOf(),
    var playerClickListener: OnPlayerClickListener? = null
) :
    RecyclerView.Adapter<PlayerListHeaderAdapter.ViewHolder>() {

    interface OnPlayerClickListener {
        fun onPlayerClicked(player: Player)
    }

    class ViewHolder(private val playerView: TextView) : RecyclerView.ViewHolder(playerView) {
        fun bind(player: Player, listener: OnPlayerClickListener?) {
            playerView.text = player.name
            playerView.setOnClickListener {
                listener?.onPlayerClicked(player)
            }
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
        tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tv.textSize = 22f
        tv.setTextColor(ContextCompat.getColor(context, R.color.neural_dark))
//        tv.setTypeface(tv.typeface, Typeface.BOLD)

        return tv
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = players[position]
        holder.bind(player, playerClickListener)
    }

    override fun getItemCount(): Int {
        return players.size
    }
}