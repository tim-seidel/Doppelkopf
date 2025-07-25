package de.timseidel.doppelkopf.ui.group.settings

import android.os.Bundle
import android.text.Editable
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.databinding.ActivityGroupSettingsBinding
import de.timseidel.doppelkopf.db.DoppelkopfDatabase
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.MemberSelection
import de.timseidel.doppelkopf.ui.EditTextListener
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.session.creation.MemberSelectAdapter
import de.timseidel.doppelkopf.ui.util.Converter
import de.timseidel.doppelkopf.util.DokoShortAccess

class GroupSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupSettingsBinding

    private lateinit var cbGroupSettingsBockrundenEnable: CheckBox
    private lateinit var rvMemberActiveList: RecyclerView
    private lateinit var btnSaveGroupSettings: Button
    private lateinit var btnCreateMember: ImageButton

    private lateinit var memberActiveAdapter: MemberSelectAdapter

    private val viewModel = GroupSettingsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findViews()
        setupViewModel()

        setupToolbar()
        setupBockrundenCheckbox()
        setupMemberActiveList()
        setupButtons()
    }

    private fun findViews() {
        cbGroupSettingsBockrundenEnable = binding.cbGroupSettingsBockrundenEnable
        rvMemberActiveList = binding.rvGroupSettingsActiveMemberList
        btnCreateMember = binding.ibGroupSettingsAddMember
        btnSaveGroupSettings = binding.btnSaveGroupSettings
    }

    private fun setupViewModel() {
        viewModel.isBockrundeEnabled =
            DokoShortAccess.getSettingsCtrl().getSettings().isBockrundeEnabled
        val members = DokoShortAccess.getMemberCtrl().getMembers()
        val memberSelections = members.map { m -> MemberSelection(m, m.isActive) }.toMutableList()

        viewModel.memberActiveList.clear()
        viewModel.memberActiveList.addAll(memberSelections)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(RESULT_CANCELED)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbar() {
        setTitle(R.string.title_group_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupBockrundenCheckbox() {
        cbGroupSettingsBockrundenEnable.isChecked = viewModel.isBockrundeEnabled
        cbGroupSettingsBockrundenEnable.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isBockrundeEnabled = isChecked
        }
    }

    private fun setupMemberActiveList() {
        memberActiveAdapter = MemberSelectAdapter(viewModel.memberActiveList,
            object : MemberSelectAdapter.MemberSelectListener {
                override fun onMemberSelected(member: MemberSelection) {
                    member.isSelected = !member.isSelected
                }
            })

        val dp4 = Converter.convertDpToPixels(4f, rvMemberActiveList.context)
        rvMemberActiveList.adapter = memberActiveAdapter
        rvMemberActiveList.layoutManager = GridLayoutManager(rvMemberActiveList.context, 2)
        rvMemberActiveList.addItemDecoration(RecyclerViewMarginDecoration(dp4, dp4))
    }

    private fun setupButtons() {
        binding.btnSaveGroupSettings.setOnClickListener {
            saveSettings()
            setResult(RESULT_OK)
            finish()
        }
        btnCreateMember.setOnClickListener {
            onAddMemberClicked()
        }
    }

    private fun onAddMemberClicked(){
        val inputLayout = layoutInflater.inflate(R.layout.dialog_member_creation, null)
        val etMemberName = inputLayout.findViewById<EditText>(R.id.et_member_creation_name)
        val tvMemberMessage =
            inputLayout.findViewById<TextView>(R.id.tv_member_creation_info_message)

        etMemberName.addTextChangedListener(object : EditTextListener() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.memberInputName = s.toString()
                if (DokoShortAccess.getMemberCtrl().validateName(s.toString())) {
                    tvMemberMessage.text = ""
                } else {
                    tvMemberMessage.text = getString(R.string.session_member_creation_unique_name)
                }
            }
        })

        AlertDialog.Builder(this).setTitle(getString(R.string.session_creation_create_new_member))
            .setMessage(getString(R.string.group_settings_member_creation_short_name_advice))
            .setPositiveButton(getString(R.string.okay)) { _, _ -> createMember(etMemberName.text.toString()) }
            .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            .setView(inputLayout).show()
    }

    private fun createMember(name: String) {
        val memberCtrl = DokoShortAccess.getMemberCtrl()
        if (!memberCtrl.validateName(name)) return

        val member = memberCtrl.createMember(name)
        memberCtrl.addMember(member)

        val db = Firebase.firestore
        val firebase = DoppelkopfDatabase()
        firebase.setFirestore(db)
        firebase.storeMember(member, DokoShortAccess.getGroupCtrl().getGroup())

        viewModel.memberActiveList.add(MemberSelection(member, true))
        viewModel.memberInputName = ""
        memberActiveAdapter.notifyItemInserted(viewModel.memberActiveList.size - 1)
    }

    private fun saveSettings() {
        val settings = DokoShortAccess.getSettingsCtrl().getSettings()
        settings.isBockrundeEnabled = viewModel.isBockrundeEnabled

        val membersToUpdate = mutableListOf<Member>()
        DokoShortAccess.getMemberCtrl().getMembers().forEach { m ->
            val selectedMember = viewModel.memberActiveList.find { ma -> ma.member.id == m.id }
            if(selectedMember != null && selectedMember.isSelected != m.isActive) {
                DokoShortAccess.getMemberCtrl().updateMember(m.id, m.copy(isActive = selectedMember.isSelected))
                membersToUpdate.add(m)
            }
        }

        val db = Firebase.firestore
        val firebase = DoppelkopfDatabase()
        firebase.setFirestore(db)

        firebase.storeGroupSettings(DokoShortAccess.getGroupCtrl().getGroup(), settings)
        firebase.updateMembers(membersToUpdate, DokoShortAccess.getGroupCtrl().getGroup())
    }
}