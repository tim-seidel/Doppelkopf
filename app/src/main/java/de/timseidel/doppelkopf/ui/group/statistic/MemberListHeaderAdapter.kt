package de.timseidel.doppelkopf.ui.group.statistic

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.Member

class MemberListHeaderAdapter(
    private val members: List<Member> = listOf(),
    var memberClickListener: OnMemberClickListener? = null
) :
    RecyclerView.Adapter<MemberListHeaderAdapter.ViewHolder>() {

    interface OnMemberClickListener {
        fun onMemberClicked(member: Member)
    }

    class ViewHolder(private val memberView: TextView) : RecyclerView.ViewHolder(memberView) {
        fun bind(member: Member, listener: OnMemberClickListener?) {
            memberView.text = member.name
            memberView.setOnClickListener {
                listener?.onMemberClicked(member)
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

        return tv
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = members[position]
        holder.bind(member, memberClickListener)
    }

    override fun getItemCount(): Int {
        return members.size
    }
}