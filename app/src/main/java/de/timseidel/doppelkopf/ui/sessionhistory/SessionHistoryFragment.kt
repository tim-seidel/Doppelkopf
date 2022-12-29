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
import de.timseidel.doppelkopf.db.request.*
import de.timseidel.doppelkopf.model.DokoSession
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.session.SessionActivity
import de.timseidel.doppelkopf.ui.session.SessionCreationActivity
import de.timseidel.doppelkopf.util.Converter
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
        val root: View = binding.root

        setupCreateSessionButton()
        loadSessionHistoryList()

        return root
    }

    private fun loadSessionHistoryList() {
        SessionListRequest().execute(object : ReadRequestListener<List<DokoSession>> {
            override fun onReadComplete(result: List<DokoSession>) {
                Logging.d("setupSessionHistoryList: $result")
                setSessionHistoryList(result)
            }

            override fun onReadFailed() {}
        })
    }

    private fun setSessionHistoryList(sessions: List<DokoSession>) {
        sessionHistoryListAdapter = SessionHistoryListAdapter(sessions, object :
            SessionHistoryListAdapter.OnSessionClickListener {
            override fun onOpenSessionClicked(session: DokoSession) {
                loadSession(session.id)
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

    private fun loadSession(sessionId: String) {
        SessionInfoRequest(sessionId).execute(object : ReadRequestListener<DokoSession> {
            override fun onReadComplete(result: DokoSession) {
                Logging.d("Session data: $result")
                DokoShortAccess.getSessionCtrl().set(result)

                SessionPlayersRequest(sessionId).execute(object :
                    ReadRequestListener<List<Player>> {

                    override fun onReadComplete(result: List<Player>) {
                        val pctrl = DokoShortAccess.getPlayerCtrl()
                        pctrl.addPlayers(result)

                        SessionGamesRequest(sessionId, pctrl).execute(
                            object : ReadRequestListener<List<Game>> {

                                override fun onReadComplete(result: List<Game>) {
                                    result.forEach { g ->
                                        DokoShortAccess.getGameCtrl().addGame(g)
                                    }
                                    Logging.d("Game data: $result")

                                    openSessionActivity()
                                }

                                override fun onReadFailed() {}
                            })
                        Logging.d("Players data: $result")
                    }

                    override fun onReadFailed() {}
                })
            }

            override fun onReadFailed() {}
        })
    }

    private fun openSessionActivity() {
        val intent = Intent(context, SessionActivity::class.java)
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