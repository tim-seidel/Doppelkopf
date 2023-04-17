package de.timseidel.doppelkopf.ui.sessionhistory

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.timseidel.doppelkopf.databinding.FragmentSessionHistoryBinding
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.session.SessionLoadingActivity
import de.timseidel.doppelkopf.ui.session.creation.SessionCreationActivity
import de.timseidel.doppelkopf.ui.util.Converter
import de.timseidel.doppelkopf.util.DokoShortAccess

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
        val root: View = binding.root

        setupCreateSessionButton()

        setSessionHistoryList(
            DokoShortAccess.getSessionInfoCtrl().getSessionInfos().sortedByDescending { it.date })

        return root
    }

    private fun setSessionHistoryList(sessions: List<Session>) {
        sessionHistoryListAdapter = SessionHistoryListAdapter(sessions, object :
            SessionHistoryListAdapter.OnSessionClickListener {
            override fun onOpenSessionClicked(session: Session) {
                redirectToSessionLoadingActivity(session.id)
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

    private fun redirectToSessionLoadingActivity(sessionId: String) {
        val intent = Intent(context, SessionLoadingActivity::class.java)
        intent.putExtra(SessionLoadingActivity.KEY_SESSION_ID, sessionId)
        startActivity(intent)
    }

    private fun setupCreateSessionButton() {
        val gotoSessionCreationButton: FloatingActionButton = binding.fabAddSession

        gotoSessionCreationButton.setOnClickListener {
            val intent = Intent(context, SessionCreationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}