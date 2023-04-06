package de.timseidel.doppelkopf.ui.session

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.db.request.ReadRequestListener
import de.timseidel.doppelkopf.db.request.SessionGamesRequest
import de.timseidel.doppelkopf.db.request.SessionInfoRequest
import de.timseidel.doppelkopf.db.request.SessionPlayersRequest
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.util.DokoShortAccess

class SessionLoadingActivity : AppCompatActivity() {

    companion object {
        const val KEY_SESSION_ID = "SESSION_ID"
    }

    private lateinit var tvTitle: TextView
    private lateinit var tvMessage: TextView
    private lateinit var ivLoadingIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_loading)

        supportActionBar?.hide()

        findViews()
        initAnimation()

        setTitle(getString(R.string.app_name))

        val sessionId = intent.getStringExtra(KEY_SESSION_ID)

        if (sessionId != null) {
            loadSession(DokoShortAccess.getGroupCtrl().getGroup().id, sessionId)
        }
    }

    private fun setTitle(title: String) {
        tvTitle.text = title
    }

    private fun setMessage(message: String) {
        tvMessage.text = message
    }

    private fun findViews() {
        tvTitle = findViewById(R.id.tv_loading_title)
        tvMessage = findViewById(R.id.tv_loading_message)
        ivLoadingIcon = findViewById(R.id.iv_loading_icon)
    }

    private fun initAnimation() {
        val anim = AnimationUtils.loadAnimation(this, R.anim.animation_loading_rotate_shake)
        ivLoadingIcon.startAnimation(anim)
    }

    private fun loadSession(groupId: String, sessionId: String) {
        setMessage(getString(R.string.loading_session))
        SessionInfoRequest(groupId, sessionId).execute(object : ReadRequestListener<Session> {
            override fun onReadComplete(result: Session) {
                DokoShortAccess.getSessionCtrl().set(result)

                setTitle(result.name)
                setMessage(getString(R.string.loading_players))

                SessionPlayersRequest(groupId, sessionId).execute(object :
                    ReadRequestListener<List<Player>> {

                    override fun onReadComplete(result: List<Player>) {
                        val pctrl = DokoShortAccess.getPlayerCtrl()
                        pctrl.addPlayers(result)

                        setMessage(getString(R.string.loading_games))

                        SessionGamesRequest(groupId, sessionId, pctrl).execute(
                            object : ReadRequestListener<List<Game>> {

                                override fun onReadComplete(result: List<Game>) {
                                    result.forEach { g ->
                                        DokoShortAccess.getGameCtrl().addGame(g)
                                    }

                                    setMessage(getString(R.string.loading_finished))

                                    openSessionActivity()
                                }

                                override fun onReadFailed() {}
                            })
                    }

                    override fun onReadFailed() {}
                })
            }

            override fun onReadFailed() {}
        })
    }

    private fun openSessionActivity() {
        val intent = Intent(this, SessionActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}