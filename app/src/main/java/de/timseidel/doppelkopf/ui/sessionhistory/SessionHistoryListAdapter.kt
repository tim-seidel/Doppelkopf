package de.timseidel.doppelkopf.ui.sessionhistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.db.request.ReadRequestListener
import de.timseidel.doppelkopf.db.request.SessionPlayersRequest
import de.timseidel.doppelkopf.model.DokoSession
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.util.DokoShortAccess
import java.time.format.DateTimeFormatter

class SessionHistoryListAdapter(
    private val sessionInfo: List<DokoSession>,
    var sessionOpenListener: OnSessionClickListener? = null
) : RecyclerView.Adapter<SessionHistoryListAdapter.ViewHolder>() {

    interface OnSessionClickListener {
        fun onOpenSessionClicked(session: DokoSession)
    }

    class ViewHolder(private val sessionView: SessionHistoryListItemView) :
        RecyclerView.ViewHolder(sessionView) {
        fun bind(session: DokoSession, listener: OnSessionClickListener?) {
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

            SessionPlayersRequest(DokoShortAccess.getGroupCtrl().getGroup().id, session.id).execute(
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

    override fun getItemCount(): Int {
        return sessionInfo.size
    }
}