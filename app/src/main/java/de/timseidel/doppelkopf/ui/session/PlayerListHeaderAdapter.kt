package de.timseidel.doppelkopf.ui.session

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.ui.util.Converter

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
        val dp2 = Converter.convertDpToPixels(2f, context)

        val tv = TextView(context)
        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(dp2)
        tv.layoutParams = layoutParams
        tv.setPadding(0,dp2,0,dp2)
        tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tv.textSize = 20f
        tv.setTextColor(ContextCompat.getColor(context, R.color.neural_dark))
        tv.background = ContextCompat.getDrawable(context, R.drawable.border_background)
        //tv.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)

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