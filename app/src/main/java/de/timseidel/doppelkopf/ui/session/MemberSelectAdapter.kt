package de.timseidel.doppelkopf.ui.session

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.ui.group.MemberSelectView

class MemberSelectAdapter(
    private val members: MutableList<MemberSelection>,
    private var listener: MemberSelectListener? = null
) : RecyclerView.Adapter<MemberSelectAdapter.ViewHolder>() {

    interface MemberSelectListener {
        fun onMemberSelected(member: MemberSelection)
    }

    data class MemberSelection(val member: Member, var isSelected: Boolean)

    class ViewHolder(val view: MemberSelectView, var listener: MemberSelectListener?) :
        RecyclerView.ViewHolder(view) {
        fun bind(member: MemberSelection) {
            view.setName(member.member.name)
            view.setChecked(member.isSelected)
            view.setIsCheckedListener { _, _ -> listener?.onMemberSelected(member) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = MemberSelectView(parent.context)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount(): Int {
        return members.size
    }
}