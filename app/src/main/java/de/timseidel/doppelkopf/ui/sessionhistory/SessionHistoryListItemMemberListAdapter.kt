package de.timseidel.doppelkopf.ui.sessionhistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.model.Member

class SessionHistoryListItemMemberListAdapter(
    private val members: List<Member> = emptyList(),
) :
    RecyclerView.Adapter<SessionHistoryListItemMemberListAdapter.ViewHolder>() {

    class ViewHolder(private val memberView: SessionHistoryListItemMemberView) :
        RecyclerView.ViewHolder(memberView) {
        fun bind(member: Member) {
            memberView.setMemberName(member.name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SessionHistoryListItemMemberView(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = members[position]
        holder.bind(result)
    }

    override fun getItemCount(): Int {
        return members.size
    }
}