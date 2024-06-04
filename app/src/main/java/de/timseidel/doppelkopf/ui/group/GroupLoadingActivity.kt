package de.timseidel.doppelkopf.ui.group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.databinding.ActivityGroupLoadingBinding
import de.timseidel.doppelkopf.db.request.GroupInfoRequestByCode
import de.timseidel.doppelkopf.db.request.GroupInfoRequestById
import de.timseidel.doppelkopf.db.request.GroupMemberRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.db.request.SessionInfoListRequest
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.model.Group
import de.timseidel.doppelkopf.model.GroupSettings
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.Logging

class GroupLoadingActivity : AppCompatActivity() {
    companion object {
        const val KEY_LOADING_MODE = "GROUP_LOADING_MODE"
        const val KEY_GROUP_ID = "GROUP_ID"
        const val KEY_GROUP_CODE = "GROUP_CODE"

        const val LOADING_MODE_ID = "LOADING_MODE_ID"
        const val LOADING_MODE_CODE = "LOADING_MODE_CODE"
    }

    private lateinit var binding: ActivityGroupLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        initButtons()
        initAnimation()
        setTitle(getString(R.string.app_name))

        checkLoadingMode()
    }

    private fun initButtons() {
        binding.btnGroupLoadingBack.visibility = Button.GONE
        binding.btnGroupLoadingBack.isEnabled = false
    }

    private fun initAnimation() {
        val anim = AnimationUtils.loadAnimation(this, R.anim.animation_loading_rotate_shake)
        binding.ivGroupLoadingIcon.startAnimation(anim)
    }

    private fun setTitle(title: String) {
        binding.tvGroupLoadingTitle.text = title
    }

    private fun setMessage(message: String) {
        binding.tvGroupLoadingMessage.text = message
    }

    private fun checkLoadingMode() {
        when (intent.getStringExtra(KEY_LOADING_MODE)) {
            LOADING_MODE_ID -> {
                loadGroupById()
            }

            LOADING_MODE_CODE -> {
                loadGroupByCode()
            }

            else -> {
                Logging.e("GroupLoadingActivity", "GROUP LOADING MODE NOT SET (ID/CODE)")
                handleLoadingError()
            }
        }
    }

    private val listener = object : ReadRequestListener<Pair<Group, GroupSettings>> {

        override fun onReadComplete(groupAndSettings: Pair<Group, GroupSettings>) {
            val (group, settings) = groupAndSettings
            DokoShortAccess.getGroupCtrl().set(group)
            DokoShortAccess.getSettingsCtrl().set(settings)

            setTitle(group.name)
            setMessage(getString(R.string.loading_session_members))

            GroupMemberRequest(group.id).execute(object :
                ReadRequestListener<List<Member>> {

                override fun onReadComplete(members: List<Member>) {
                    DokoShortAccess.getMemberCtrl().addMembers(members)
                    storeCurrentGroupId(group.id)

                    setMessage(getString(R.string.loading_sessionInfos))

                    SessionInfoListRequest(
                        DokoShortAccess.getGroupCtrl().getGroup().id
                    ).execute(object :
                        ReadRequestListener<List<Session>> {
                        override fun onReadComplete(sessionInfos: List<Session>) {
                            DokoShortAccess.getSessionInfoCtrl().addSessionInfos(sessionInfos)

                            setMessage(getString(R.string.loading_finished))
                            openGroupOverviewActivity()
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

        override fun onReadFailed() {
            handleLoadingError()
        }
    }

    private fun loadGroupById() {
        val id = intent.getStringExtra(KEY_GROUP_ID) ?: ""
        if (id.isEmpty()) {
            Logging.e("GroupLoadingActivity", "GROUP LOADING MODE IS ID BUT NO ID WAS PROVIDED)")
            handleLoadingError()
            return
        }

        setMessage(getString(R.string.loading_group))
        GroupInfoRequestById(id).execute(listener)
    }

    private fun loadGroupByCode() {
        val code = intent.getStringExtra(KEY_GROUP_CODE) ?: ""
        if (code.isEmpty()) {
            Logging.e("GroupLoadingActivity", "GROUP LOADING MODE IS CODE BUT NO CODE WAS PROVIDED")
            handleLoadingError()
            return
        }

        setMessage(getString(R.string.loading_group))
        GroupInfoRequestByCode(code).execute(listener)
    }

    private fun handleLoadingError() {
        clearCurrentGroupId()

        binding.ivGroupLoadingIcon.clearAnimation()
        binding.ivGroupLoadingIcon.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.baseline_sentiment_very_dissatisfied_24
            )
        )
        setMessage(getString(R.string.loading_group_no_success))
        enableBackButton()
    }

    private fun enableBackButton() {
        binding.btnGroupLoadingBack.visibility = Button.VISIBLE
        binding.btnGroupLoadingBack.isEnabled = true

        binding.btnGroupLoadingBack.setOnClickListener {
            finish()
        }
    }

    private fun clearCurrentGroupId() {
        val sharedPreferences = getSharedPreferences(
            getString(R.string.shared_preferences_file_key),
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit().remove(getString(R.string.shared_preferences_group_id_key)).apply()
    }

    private fun storeCurrentGroupId(groupId: String) {
        val sharedPreferences = getSharedPreferences(
            getString(R.string.shared_preferences_file_key),
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit()
            .putString(getString(R.string.shared_preferences_group_id_key), groupId).apply()
    }

    private fun openGroupOverviewActivity() {
        val intent = Intent(this, GroupActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.btnGroupLoadingBack.setOnClickListener(null)
        binding.ivGroupLoadingIcon.clearAnimation()
    }
}