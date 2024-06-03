package de.timseidel.doppelkopf.ui.session.gamecreation

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.MemberAndFaction

class MemberFactionSelectAdapter(
    private var members: MutableList<MemberAndFaction>,
    var listener: MemberFactionClickListener? = null
) :
    RecyclerView.Adapter<MemberFactionSelectAdapter.ViewHolder>() {

    interface MemberFactionClickListener {
        fun onFactionUpdate(member: Member, newFaction: Faction)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val memberFactionSelectView: MemberFactionSelectView = view as MemberFactionSelectView

        fun bind(maf: MemberAndFaction) {
            memberFactionSelectView.setMemberName(maf.member.name)
            setFaction(maf.faction)
        }

        fun setFaction(faction: Faction) {
            memberFactionSelectView.setFaction(faction)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = MemberFactionSelectView(parent.context)

        return ViewHolder(view)
    }

    //TODO: Not consistent with the rest of the game creation model handling. This adapter controls the new faction assignment instead of the parent view and model.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(members[position])

        val btnRe: ImageButton = holder.memberFactionSelectView.findViewById(R.id.btn_faction_re)
        val btnContra: ImageButton =
            holder.memberFactionSelectView.findViewById(R.id.btn_faction_contra)

        btnRe.setOnClickListener {
            val newFaction = getNewFaction(members[position].faction, Faction.RE)
            listener?.onFactionUpdate(members[position].member, newFaction)
            members[position].faction = newFaction
            holder.setFaction(newFaction)
        }
        btnContra.setOnClickListener {
            val newFaction = getNewFaction(members[position].faction, Faction.CONTRA)
            listener?.onFactionUpdate(members[position].member, newFaction)
            members[position].faction = newFaction
            holder.setFaction(newFaction)
        }
    }

    private fun getNewFaction(currentFaction: Faction, selectedFaction: Faction): Faction {
        return when (selectedFaction) {
            Faction.RE -> if (currentFaction != Faction.RE) Faction.RE else Faction.NONE
            Faction.CONTRA -> if (currentFaction != Faction.CONTRA) Faction.CONTRA else Faction.NONE
            Faction.NONE -> selectedFaction
        }
    }

    override fun getItemCount(): Int {
        return members.size
    }

    fun resetPlayerFactions() {
        if (members.isEmpty()) {
            return
        }

        members.forEach { p ->
            p.faction = Faction.NONE
        }
        notifyItemRangeChanged(0, members.size)
    }

    fun updatePlayerFaction(member: Member, faction: Faction) {
        val memberIndex = members.indexOfFirst { it.member.id == member.id }
        if (memberIndex == -1) {
            return
        }

        members[memberIndex].faction = faction
        notifyItemChanged(memberIndex)
    }

    fun updateMemberFactionList(updatedMembers: List<MemberAndFaction>) {
        members.clear()
        members.addAll(updatedMembers)
        notifyItemRangeChanged(0, members.size)
    }
}