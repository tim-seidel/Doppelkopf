package de.timseidel.doppelkopf.ui.group.creation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.controller.DoppelkopfManager
import de.timseidel.doppelkopf.databinding.ActivityGroupCreationBinding
import de.timseidel.doppelkopf.db.DoppelkopfDatabase
import de.timseidel.doppelkopf.ui.EditTextListener
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.group.GroupActivity
import de.timseidel.doppelkopf.ui.util.Converter
import de.timseidel.doppelkopf.util.Logging

class GroupCreationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupCreationBinding

    private val groupCreationViewModel = GroupCreationViewModel()
    private var memberNameListAdapter =
        GroupCreationMemberListAdapter(groupCreationViewModel.memberNames)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
        setupNameInput()
        setupNameList()

        checkIsInputValid()

    }

    private fun setupButtons() {
        binding.btnSaveGroup.setOnClickListener {
            onCreateGroupClicked()
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
            object : GroupCreationMemberListAdapter.OnMemberClickListener {
                override fun onAddMemberClicked() {
                    memberNameListAdapter.addRow()
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

    private fun onCreateGroupClicked() {
        if (!groupCreationViewModel.isValid()) {
            showGroupCreationError(getString(R.string.create_group_unable_to_create))
        }

        try {
            val groupCtrl = DoppelkopfManager.getInstance().getGroupController()
            val group = groupCtrl.createGroup(groupCreationViewModel.groupName)
            groupCtrl.set(group)

            val db = Firebase.firestore
            val firebase = DoppelkopfDatabase()
            firebase.setFirestore(db)
            firebase.storeGroup(group)

            val memberNames = groupCreationViewModel.getFilteredMemberNames()
            if (memberNames.isNotEmpty()) {
                val members = groupCtrl.getMemberController().createMembers(memberNames)
                groupCtrl.getMemberController().addMembers(members)
                firebase.storeMembers(members, group)
            }

            finishGroupCreation()
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
        startActivity(intent)
        finish()
    }
}