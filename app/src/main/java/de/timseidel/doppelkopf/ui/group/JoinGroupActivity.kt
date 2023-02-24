package de.timseidel.doppelkopf.ui.group

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import de.timseidel.doppelkopf.MainActivity
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.databinding.ActivityJoinGroupBinding
import de.timseidel.doppelkopf.ui.session.SessionLoadingActivity
import de.timseidel.doppelkopf.util.EditTextListener

class JoinGroupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinGroupBinding

    private var groupCodeInputValue = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJoinGroupBinding.inflate(layoutInflater)

        val id = getStoredGroupId()
        if (isCurrentGroupIdSet(id)) {
            val intent = Intent(this, GroupLoadingActivity::class.java)
            intent.putExtra(
                GroupLoadingActivity.KEY_LOADING_MODE,
                GroupLoadingActivity.LOADING_MODE_ID
            )
            intent.putExtra(GroupLoadingActivity.KEY_GROUP_ID, id)
            startActivity(intent)
        } else {
            setupButtons()
            setupCodeInput()

            checkGroupCode()
            setContentView(binding.rootJoinGroup)
        }
    }

    private fun setupButtons() {
        binding.btnJoinGroup.setOnClickListener {
            onJoinGroupClicked()
        }
        binding.btnCreateGroup.setOnClickListener {
            onCreateGroupClicked()
        }
    }

    private fun setupCodeInput() {
        binding.etGroupCode.addTextChangedListener(object : EditTextListener() {
            override fun afterTextChanged(s: Editable?) {
                groupCodeInputValue = s.toString()
                checkGroupCode()
            }
        })
    }

    private fun isCurrentGroupIdSet(id: String): Boolean {
        return id.isNotEmpty()
    }

    private fun getStoredGroupId(): String {
        val sharedPreferences = getSharedPreferences(
            getString(R.string.shared_preferences_file_key),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(getString(R.string.shared_preferences_group_id_key), "")
            ?: ""
    }

    private fun checkGroupCode(): Boolean {
        val valid = groupCodeInputValue.length == 6 && groupCodeInputValue.toIntOrNull() != null
        binding.btnJoinGroup.isEnabled = valid
        return valid
    }

    private fun onJoinGroupClicked() {
        if (!checkGroupCode()) {
            return
        }

        val intent = Intent(this, GroupLoadingActivity::class.java)
        intent.putExtra(
            GroupLoadingActivity.KEY_LOADING_MODE,
            GroupLoadingActivity.LOADING_MODE_CODE
        )
        intent.putExtra(GroupLoadingActivity.KEY_GROUP_CODE, groupCodeInputValue)
        startActivity(intent)
    }

    private fun onCreateGroupClicked() {
        val intent = Intent(this, GroupCreationActivity::class.java)
        startActivity(intent)
    }
}