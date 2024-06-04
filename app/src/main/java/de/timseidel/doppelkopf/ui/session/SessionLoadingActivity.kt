package de.timseidel.doppelkopf.ui.session

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.databinding.ActivitySessionLoadingBinding
import de.timseidel.doppelkopf.db.request.SessionGameRequest
import de.timseidel.doppelkopf.db.request.SessionInfoRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.Logging

class SessionLoadingActivity : AppCompatActivity() {

    companion object {
        const val KEY_SESSION_ID = "SESSION_ID"
    }

    private lateinit var binding: ActivitySessionLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySessionLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        initButtons()
        initAnimation()
        setTitle(getString(R.string.app_name))

        checkAndStartSessionLoading()
    }

    private fun initAnimation() {
        val anim = AnimationUtils.loadAnimation(this, R.anim.animation_loading_rotate_shake)
        binding.ivSessionLoadingIcon.startAnimation(anim)
    }

    private fun initButtons() {
        binding.btnSessionLoadingBack.visibility = Button.GONE
        binding.btnSessionLoadingBack.isEnabled = false
    }

    private fun setTitle(title: String) {
        binding.tvSessionLoadingTitle.text = title
    }

    private fun setMessage(message: String) {
        binding.tvSessionLoadingMessage.text = message
    }

    private fun checkAndStartSessionLoading() {
        val sessionId = intent.getStringExtra(KEY_SESSION_ID)
        if (sessionId != null) {
            loadSession(DokoShortAccess.getGroupCtrl().getGroup().id, sessionId)
        } else {
            Logging.e("SessionLoadingActivity", "No session id found")
            handleLoadingError()
        }
    }

    private fun loadSession(groupId: String, sessionId: String) {
        setMessage(getString(R.string.loading_session))
        SessionInfoRequest(groupId, sessionId).execute(object : ReadRequestListener<Session> {
            override fun onReadComplete(result: Session) {
                DokoShortAccess.getSessionCtrl().set(result)

                setTitle(result.name)
                setMessage(getString(R.string.loading_games))

                SessionGameRequest(groupId, sessionId, DokoShortAccess.getMemberCtrl()).execute(
                    object : ReadRequestListener<List<Game>> {
                        override fun onReadComplete(result: List<Game>) {
                            result.forEach { g ->
                                DokoShortAccess.getGameCtrl().addGame(g)
                            }

                            setMessage(getString(R.string.loading_finished))
                            openSessionActivity()
                        }

                        override fun onReadFailed() {
                            handleLoadingError()
                        }
                    })
            }

            override fun onReadFailed() {
                handleLoadingError()
            }
        })
    }

    private fun handleLoadingError() {
        binding.ivSessionLoadingIcon.clearAnimation()
        binding.ivSessionLoadingIcon.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.baseline_sentiment_very_dissatisfied_24
            )
        )
        setMessage(getString(R.string.loading_session_no_success))
        enableBackButton()
    }

    private fun enableBackButton() {
        binding.btnSessionLoadingBack.visibility = Button.VISIBLE
        binding.btnSessionLoadingBack.isEnabled = true

        binding.btnSessionLoadingBack.setOnClickListener {
            finish()
        }
    }

    private fun openSessionActivity() {
        val intent = Intent(this, SessionActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.btnSessionLoadingBack.setOnClickListener(null)
        binding.ivSessionLoadingIcon.clearAnimation()
    }
}