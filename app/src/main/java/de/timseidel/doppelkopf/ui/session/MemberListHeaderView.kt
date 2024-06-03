package de.timseidel.doppelkopf.ui.session

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
    private lateinit var rvMembers: RecyclerView

    private var memberClickListener: MemberListHeaderAdapter.OnMemberClickListener? = null

    init {
        init()
    }

    private fun init() {
        View.inflate(context, R.layout.view_game_history_list_header, this)
        findViews()

        setRowSize(1)
    }

    private fun findViews() {
        rvMembers = findViewById(R.id.rv_ghl_player_header)
    }

    fun setListener(listener: MemberListHeaderAdapter.OnMemberClickListener) {
        memberClickListener = listener
    }

    fun setMembers(members: List<Member>) {
        adapter = MemberListHeaderAdapter(members)
        adapter.memberClickListener = memberClickListener
        rvMembers.adapter = adapter
    }

    fun setRowSize(rowSize: Int) {
        rvMembers.layoutManager = GridLayoutManager(rvMembers.context, max(rowSize, 1))
    }
}