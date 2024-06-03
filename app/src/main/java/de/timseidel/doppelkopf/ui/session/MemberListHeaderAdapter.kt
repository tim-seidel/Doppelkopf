package de.timseidel.doppelkopf.ui.session

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.ui.util.Converter

class MemberListHeaderAdapter(
    private val members: List<Member> = listOf(),
    var memberClickListener: OnMemberClickListener? = null
) :
    RecyclerView.Adapter<MemberListHeaderAdapter.ViewHolder>() {

    interface OnMemberClickListener {
        fun onPlayerClicked(member: Member)
    }

    class ViewHolder(private val memberView: TextView) : RecyclerView.ViewHolder(memberView) {
        fun bind(member: Member, listener: OnMemberClickListener?) {
            memberView.text = member.name
            memberView.setOnClickListener {
                listener?.onPlayerClicked(member)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(createPlayerNameView(parent.context))
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
        tv.setPadding(0, dp2, 0, dp2)
        tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tv.textSize = 20f
        tv.setTextColor(ContextCompat.getColor(context, R.color.neural_dark))
        tv.background = ContextCompat.getDrawable(context, R.drawable.border_background)

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