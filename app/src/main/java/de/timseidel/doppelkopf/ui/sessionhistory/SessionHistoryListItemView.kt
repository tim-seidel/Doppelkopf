package de.timseidel.doppelkopf.ui.sessionhistory

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.util.Converter
import kotlin.math.max

class SessionHistoryListItemView constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var rvPlayers: RecyclerView
    private lateinit var ibOpenSession: ImageButton
    private lateinit var playerListAdapter: SessionHistoryListItemPlayerListAdapter
    private var sessionOpenListener: OnClickListener? = null

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_session_history_list_item, this)

        findViews()
        parseAttributes(attrs)
        setupRecyclerView()

        setOnClickListener { sessionOpenListener?.onClick(this) }
    }

    private fun findViews() {
        tvTitle = findViewById(R.id.tv_session_title)
        tvDescription = findViewById(R.id.tv_session_description)
        ibOpenSession = findViewById(R.id.ib_open_session)
        rvPlayers = findViewById(R.id.rv_session_list_players)
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SessionHistoryListItemView)
        try {
            val title = ta.getString(R.styleable.SessionHistoryListItemView_sessionTitle) ?: ""
            val description =
                ta.getString(R.styleable.SessionHistoryListItemView_sessionDescription) ?: ""

            setTitle(title)
            setDescription(description)
        } finally {
            ta.recycle()
        }
    }

    private fun setupRecyclerView() {
        val dp4 = Converter.convertDpToPixels(4f, rvPlayers.context)
        rvPlayers.addItemDecoration(RecyclerViewMarginDecoration(dp4, dp4))
    }

    fun setOpenSessionClickListener(listener: OnClickListener) {
        sessionOpenListener = listener
        ibOpenSession.setOnClickListener(listener)
    }

    fun setPlayers(players: List<Player>) {
        playerListAdapter = SessionHistoryListItemPlayerListAdapter(players)
        rvPlayers.adapter = playerListAdapter
        rvPlayers.layoutManager = GridLayoutManager(rvPlayers.context, max(players.size, 1))
    }

    fun setTitle(title: String) {
        tvTitle.text = title
    }

    fun setDescription(description: String) {
        tvDescription.text = description
    }
}