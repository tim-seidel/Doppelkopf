package de.timseidel.doppelkopf.ui.session.gamecreation

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.model.PlayerAndFaction

class PlayerFactionSelectAdapter(
    private val players: List<PlayerAndFaction>,
    var listener: PlayerFactionClickListener? = null
) :
    RecyclerView.Adapter<PlayerFactionSelectAdapter.ViewHolder>() {

    interface PlayerFactionClickListener {
        fun onFactionUpdate(player: Player, newFaction: Faction)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerFactionSelectView: PlayerFactionSelectView

        init {
            playerFactionSelectView = view as PlayerFactionSelectView
        }

        fun bind(paf: PlayerAndFaction) {
            playerFactionSelectView.setPlayerName(paf.player.name)
            setFaction(paf.faction)
        }

        fun setFaction(faction: Faction) {
            playerFactionSelectView.setFaction(faction)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = PlayerFactionSelectView(parent.context)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(players[position])

        val btnRe: ImageButton = holder.playerFactionSelectView.findViewById(R.id.btn_faction_re)
        val btnContra: ImageButton =
            holder.playerFactionSelectView.findViewById(R.id.btn_faction_contra)

        btnRe.setOnClickListener {
            val newFaction = getNewFaction(players[position].faction, Faction.RE)
            listener?.onFactionUpdate(players[position].player, Faction.RE)
            players[position].faction = newFaction
            holder.setFaction(newFaction)
        }
        btnContra.setOnClickListener {
            val newFaction = getNewFaction(players[position].faction, Faction.CONTRA)
            listener?.onFactionUpdate(players[position].player, Faction.CONTRA)
            players[position].faction = newFaction
            holder.setFaction(newFaction)
        }
    }

    private fun getNewFaction(currentFaction: Faction, selectedFaction: Faction): Faction{
        return when(selectedFaction) {
            Faction.RE -> if (currentFaction != Faction.RE) Faction.RE else Faction.NONE
            Faction.CONTRA -> if (currentFaction != Faction.CONTRA) Faction.CONTRA else Faction.NONE
            Faction.NONE -> selectedFaction
        }
    }

    override fun getItemCount(): Int {
        return players.size
    }

    fun resetPlayerFactions() {
        players.forEach { p ->
            p.faction = Faction.NONE
        }
        notifyItemRangeChanged(0, players.size)
    }
}