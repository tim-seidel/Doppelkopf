package de.timseidel.doppelkopf.ui.group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import de.timseidel.doppelkopf.MainActivity
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.db.request.GroupInfoRequestByCode
import de.timseidel.doppelkopf.db.request.GroupInfoRequestById
import de.timseidel.doppelkopf.db.request.GroupMembersRequest
import de.timseidel.doppelkopf.db.request.ReadRequestListener
import de.timseidel.doppelkopf.model.Group
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

    private lateinit var tvTitle: TextView
    private lateinit var tvMessage: TextView
    private lateinit var ivLoadingIcon: ImageView
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_loading)

        supportActionBar?.hide()

        findViews()
        initButtons()
        initAnimation()

        setTitle(getString(R.string.app_name))

        checkLoadingMode()
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
            }
        }
    }

    private val listener = object : ReadRequestListener<Group> {

        override fun onReadComplete(group: Group) {
            DokoShortAccess.getGroupCtrl().set(group)

            setTitle(group.name)
            setMessage(getString(R.string.loading_players))

            GroupMembersRequest(group.id).execute(object :
                ReadRequestListener<List<Member>> {

                override fun onReadComplete(members: List<Member>) {
                    DokoShortAccess.getMemberCtrl().addMembers(members)
                    storeCurrentGroupId(group.id)

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
    }

    private fun loadGroupById() {
        val id = intent.getStringExtra(KEY_GROUP_ID) ?: ""

        if (id.isEmpty()) {
            Logging.e("GroupLoadingActivity", "GROUP LOADING MODE IS ID BUT NO ID WAS PROVIDED)")
            handleLoadingError()
        }

        setMessage(getString(R.string.loading_group))
        GroupInfoRequestById(id).execute(listener)
    }

    private fun loadGroupByCode() {
        val code = intent.getStringExtra(KEY_GROUP_CODE) ?: ""

        if (code.isEmpty()) {
            Logging.e("GroupLoadingActivity", "GROUP LOADING MODE IS CODE BUT NO CODE WAS PROVIDED")
            handleLoadingError()
        }

        setMessage(getString(R.string.loading_group))
        GroupInfoRequestByCode(code).execute(listener)
    }

    private fun setTitle(title: String) {
        tvTitle.text = title
    }

    private fun setMessage(message: String) {
        tvMessage.text = message
    }

    private fun findViews() {
        tvTitle = findViewById(R.id.tv_group_loading_title)
        tvMessage = findViewById(R.id.tv_group_loading_message)
        ivLoadingIcon = findViewById(R.id.iv_group_loading_icon)
        btnBack = findViewById(R.id.btn_group_loading_back)
    }

    private fun initButtons() {
        btnBack.isVisible = false
        btnBack.isEnabled = false
    }

    private fun initAnimation() {
        val anim = AnimationUtils.loadAnimation(this, R.anim.animation_loading_rotate_shake)
        ivLoadingIcon.startAnimation(anim)
    }

    private fun handleLoadingError() {
        clearCurrentGroupId()

        ivLoadingIcon.clearAnimation()
        ivLoadingIcon.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.baseline_sentiment_very_dissatisfied_24
            )
        )
        setMessage(getString(R.string.loading_group_no_success))
        enableBackButton()
    }

    private fun enableBackButton() {
        btnBack.isVisible = true
        btnBack.isEnabled = true

        btnBack.setOnClickListener {
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
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}