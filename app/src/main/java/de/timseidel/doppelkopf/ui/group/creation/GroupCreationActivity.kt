package de.timseidel.doppelkopf.ui.group.creation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.controller.DoppelkopfManager
import de.timseidel.doppelkopf.databinding.ActivityGroupCreationBinding
import de.timseidel.doppelkopf.db.DoppelkopfDatabase
import de.timseidel.doppelkopf.db.request.CreateUniqueGroupCodeRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.model.GroupSettings
import de.timseidel.doppelkopf.ui.EditTextListener
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.group.GroupActivity
import de.timseidel.doppelkopf.ui.util.Converter
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.Logging

class GroupCreationActivity : AppCompatActivity() {

    companion object {
        const val KEY_GROUP_NEWLY_CREATED_FLAG = "GROUP_NEWLY_CREATED_FLAG"
    }


    private lateinit var binding: ActivityGroupCreationBinding

    private val groupCreationViewModel = GroupCreationViewModel()
    private var memberNameListAdapter =
        GroupCreationMemberListAdapter(groupCreationViewModel.memberNames)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupButtons()
        setupInfoButtons()
        setupBockrundenCheckbox()
        setupNameInput()
        setupNameList()

        checkIsInputValid()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbar() {
        setTitle(R.string.title_session_creation)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }


    private fun setupButtons() {
        binding.btnSaveGroup.setOnClickListener {
            onCreateGroupClicked()
        }
    }

    private fun setupInfoButtons() {
        binding.ibBockrundenInfo.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.enable_bockrunden)
                .setMessage(R.string.group_toggle_bockrunden_hint)
                .setPositiveButton(R.string.okay, null)
                .show()
        }
    }


    private fun setupBockrundenCheckbox() {
        binding.cbGroupBockrundenEnable.setOnCheckedChangeListener { _, isChecked ->
            groupCreationViewModel.isBockrundeEnabled = isChecked
            checkIsInputValid()
        }
    }

    private fun setupNameInput() {
        binding.etGroupName.addTextChangedListener(object : EditTextListener() {
            override fun afterTextChanged(s: Editable?) {
                groupCreationViewModel.groupName = s.toString()
                checkIsInputValid()
            }
        })
    }

    private fun setupNameList() {
        memberNameListAdapter.memberClickListener =
            object : GroupCreationMemberListAdapter.MemberListener {
                override fun onAddMemberClicked() {
                    memberNameListAdapter.addRow()
                    checkIsInputValid()
                }

                override fun onMemberNameChanged(name: String, position: Int) {
                    //Currently no need to manually changes the name because the member list is passed by reference
                    //TODO: Change that
                    checkIsInputValid()
                }
            }
        val listView = binding.rvGroupMemberNames
        listView.adapter = memberNameListAdapter
        listView.layoutManager = LinearLayoutManager(listView.context)
        listView.addItemDecoration(
            RecyclerViewMarginDecoration(
                0,
                Converter.convertDpToPixels(4f, listView.context)
            )
        )
    }

    private fun checkIsInputValid() {
        binding.btnSaveGroup.isEnabled = groupCreationViewModel.isValid()
    }

    private fun showGroupCreationError(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun onGroupCodeCreated(groupCode: String) {
        val groupCtrl = DoppelkopfManager.getInstance().getGroupController()

        val group = groupCtrl.createGroup(groupCreationViewModel.groupName, groupCode)
        groupCtrl.set(group)

        val groupSettings =
            GroupSettings(groupCreationViewModel.isBockrundeEnabled)
        DokoShortAccess.getSettingsCtrl().set(groupSettings)

        val db = Firebase.firestore
        val firebase = DoppelkopfDatabase()
        firebase.setFirestore(db)

        firebase.storeGroup(group, groupSettings)

        val memberNames = groupCreationViewModel.getFilteredMemberNames()
        if (memberNames.isNotEmpty()) {
            val members = groupCtrl.getMemberController().createMembers(memberNames)
            groupCtrl.getMemberController().addMembers(members)
            firebase.storeMembers(members, group)
        }

        finishGroupCreation()
    }

    private fun onCreateGroupClicked() {
        if (!groupCreationViewModel.isValid()) {
            showGroupCreationError(getString(R.string.create_group_unable_to_create))
        }

        try {
            CreateUniqueGroupCodeRequest(3).execute(object : ReadRequestListener<String> {
                override fun onReadComplete(result: String) {
                    onGroupCodeCreated(result)
                }

                override fun onReadFailed() {
                    showGroupCreationError(getString(R.string.create_group_unable_to_create_groupcode))
                }
            })
        } catch (e: Exception) {
            Logging.e(
                "GroupCreationActivity",
                "Error while creating group: $groupCreationViewModel",
                e
            )
            showGroupCreationError(getString(R.string.create_group_unable_to_create))
        }
    }

    private fun finishGroupCreation() {
        val intent = Intent(this, GroupActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra(KEY_GROUP_NEWLY_CREATED_FLAG, true)

        startActivity(intent)
        finish()
    }
}