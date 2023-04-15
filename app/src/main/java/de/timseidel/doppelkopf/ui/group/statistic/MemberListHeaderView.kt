package de.timseidel.doppelkopf.ui.group.statistic

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.Member
import kotlin.math.max
class MemberListHeaderView constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    private lateinit var adapter: MemberListHeaderAdapter
    private lateinit var rvPlayers: RecyclerView

    private var memberClickListener: MemberListHeaderAdapter.OnMemberClickListener? = null

    init {
        init()
    }

    private fun init() {
        View.inflate(context, R.layout.view_member_list_header, this)

        findViews()
    }

    private fun findViews() {
        rvPlayers = findViewById(R.id.rv_member_header)
    }

    fun setListener(listener: MemberListHeaderAdapter.OnMemberClickListener) {
        memberClickListener = listener
    }

    fun setMembers(members: List<Member>) {
        adapter = MemberListHeaderAdapter(members)
        adapter.memberClickListener = memberClickListener
        rvPlayers.adapter = adapter

    }

    fun setRowSize(rowSize: Int) {
        rvPlayers.layoutManager = GridLayoutManager(rvPlayers.context, max(rowSize, 1))
    }
}