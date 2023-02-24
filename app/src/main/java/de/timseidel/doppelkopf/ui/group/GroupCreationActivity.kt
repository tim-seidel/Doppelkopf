package de.timseidel.doppelkopf.ui.group

import  android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.timseidel.doppelkopf.MainActivity
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.controller.DoppelkopfManager
import de.timseidel.doppelkopf.databinding.ActivityGroupCreationBinding
import de.timseidel.doppelkopf.db.DoppelkopfDatabase
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.util.Converter
import de.timseidel.doppelkopf.util.EditTextListener

class GroupCreationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupCreationBinding

    private val groupCreationViewModel = GroupCreationViewModel()
    private var memberNameListAdapter =
        GroupCreationMemberListAdapter(groupCreationViewModel.memberNames)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupCreationBinding.inflate(layoutInflater)

        setupButtons()
        setupNameInput()
        setupNameList()

        checkIsInputValid()

        setContentView(binding.root)
    }

    private fun checkIsInputValid() {
        binding.btnSaveGroup.isEnabled = groupCreationViewModel.isValid()
    }

    private fun onCreateGroupClicked() {
        if (!groupCreationViewModel.isValid()) {
            showGroupCreateError(getString(R.string.create_group_unable_to_create))
        }

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

        groupCreated()
    }

    private fun groupCreated() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun showGroupCreateError(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
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
}