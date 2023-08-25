package de.timseidel.doppelkopf.ui.sessionhistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.db.request.SessionPlayerRequest
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.Logging
import java.time.format.DateTimeFormatter

class SessionHistoryListAdapter(
    private val sessionInfo: MutableList<Session>,
    var sessionOpenListener: OnSessionClickListener? = null
) : RecyclerView.Adapter<SessionHistoryListAdapter.ViewHolder>() {

    interface OnSessionClickListener {
        fun onOpenSessionClicked(session: Session)
    }

    class ViewHolder(private val sessionView: SessionHistoryListItemView) :
        RecyclerView.ViewHolder(sessionView) {
        fun bind(session: Session, listener: OnSessionClickListener?) {
            sessionView.setTitle(session.name)
            sessionView.setDescription(
                "Gespielt am ${
                    session.date.format(
                        DateTimeFormatter.ofPattern(
                            "dd.MM.yyyy"
                        )
                    )
                }"
            )

            sessionView.setOpenSessionClickListener {
                listener?.onOpenSessionClicked(session)
            }

            SessionPlayerRequest(DokoShortAccess.getGroupCtrl().getGroup().id, session.id).execute(
                object : ReadRequestListener<List<Player>> {
                    override fun onReadComplete(result: List<Player>) {
                        sessionView.setPlayers(result)
                    }

                    override fun onReadFailed() {}
                })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = SessionHistoryListItemView(parent.context)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val session = sessionInfo[position]
        holder.bind(session, sessionOpenListener)
    }

    fun setSessionInfos(sessionInfos: List<Session>) {
        sessionInfo.clear()
        sessionInfo.addAll(sessionInfos)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return sessionInfo.size
    }
}