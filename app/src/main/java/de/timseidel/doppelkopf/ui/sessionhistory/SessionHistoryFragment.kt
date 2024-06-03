package de.timseidel.doppelkopf.ui.sessionhistory

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import de.timseidel.doppelkopf.databinding.FragmentSessionHistoryBinding
import de.timseidel.doppelkopf.db.request.SessionCountRequest
import de.timseidel.doppelkopf.db.request.SessionGameCountRequest
import de.timseidel.doppelkopf.db.request.SessionInfoListRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.session.SessionLoadingActivity
import de.timseidel.doppelkopf.ui.session.creation.SessionCreationActivity
import de.timseidel.doppelkopf.ui.util.Converter
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.Logging

class SessionHistoryFragment : Fragment() {

    private var _binding: FragmentSessionHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionHistoryListAdapter: SessionHistoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionHistoryBinding.inflate(inflater, container, false)

        setupCreateSessionButton()
        setupSessionHistory()

        return binding.root
    }

    private fun setupCreateSessionButton() {
        binding.fabAddSession.setOnClickListener {
            val intent = Intent(context, SessionCreationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupSessionHistory() {
        sessionHistoryListAdapter = SessionHistoryListAdapter(mutableListOf(), object :
            SessionHistoryListAdapter.OnSessionClickListener {
            override fun onOpenSessionClicked(session: Session) {
                openSessionLoadingActivity(session.id)
            }
        })

        val listView = binding.rvSessions
        listView.adapter = sessionHistoryListAdapter
        listView.layoutManager = LinearLayoutManager(context)
        listView.addItemDecoration(
            RecyclerViewMarginDecoration(
                Converter.convertDpToPixels(8f, listView.context),
                Converter.convertDpToPixels(4f, listView.context)
            )
        )
    }

    private fun updateSessionHistory() {
        val sessionInfos = DokoShortAccess.getSessionInfoCtrl().getSessionInfos()

        sessionHistoryListAdapter.setSessionInfos(sessionInfos.sortedByDescending { it.date })
        setNoSessionsPlaceholderVisibility(sessionInfos.isEmpty())
    }

    private fun setNoSessionsPlaceholderVisibility(visible: Boolean) {
        binding.llNoSessionsTextWrapper.visibility = if (visible) View.VISIBLE else View.GONE
        binding.rvSessions.visibility = if (visible) View.GONE else View.VISIBLE
    }

    private fun openSessionLoadingActivity(sessionId: String) {
        val intent = Intent(context, SessionLoadingActivity::class.java)
        intent.putExtra(SessionLoadingActivity.KEY_SESSION_ID, sessionId)
        startActivity(intent)
    }

    private fun checkForNewSession() {
        SessionCountRequest(DokoShortAccess.getGroupCtrl().getGroup().id).execute(object :
            ReadRequestListener<Int> {
            override fun onReadComplete(result: Int) {
                if (result > sessionHistoryListAdapter.itemCount) {
                    updateSessionHistory()
                }
            }

            override fun onReadFailed() {
                Logging.e("SessionHistoryFragment", "Failed to check for new sessions")
            }
        })
    }

    private fun downloadSessionHistory() {
       SessionInfoListRequest(DokoShortAccess.getGroupCtrl().getGroup().id).execute(object :
            ReadRequestListener<List<Session>> {
            override fun onReadComplete(result: List<Session>) {

            }

            override fun onReadFailed() {
                Logging.e("SessionHistoryFragment", "Failed to download session history")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        updateSessionHistory()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.fabAddSession.setOnClickListener(null)
        _binding = null
    }
}