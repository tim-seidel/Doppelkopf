package de.timseidel.doppelkopf.ui.session.creation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.controller.DoppelkopfManager
import de.timseidel.doppelkopf.databinding.ActivitySessionCreationBinding
import de.timseidel.doppelkopf.db.DoppelkopfDatabase
import de.timseidel.doppelkopf.model.MemberSelection
import de.timseidel.doppelkopf.ui.EditTextListener
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.session.SessionActivity
import de.timseidel.doppelkopf.ui.util.Converter
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.Logging

class SessionCreationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionCreationBinding

    private lateinit var etSessionName: EditText
    private lateinit var btnCreateSession: Button
    private lateinit var btnCreateMember: ImageButton
    private lateinit var rvMemberSelectList: RecyclerView

    private lateinit var memberSelectAdapter: MemberSelectAdapter

    private val viewModel = SessionCreationViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySessionCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findViews()
        setupToolbar()
        setupEditTexts()
        setupButtons()
        setupMemberSelectList()

        checkSessionIsValid()
    }

    private fun findViews() {
        etSessionName = binding.etSessionName
        rvMemberSelectList = binding.rvSessionCreationMemberList
        btnCreateSession = binding.btnSaveSession
        btnCreateMember = binding.ibSessionAddMember
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

    private fun setupEditTexts() {
        etSessionName.addTextChangedListener(object : EditTextListener() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.sessionName = s.toString()
                checkSessionIsValid()
            }
        })
    }

    private fun setupButtons() {
        btnCreateMember.setOnClickListener {
            onAddMemberClicked()
        }

        btnCreateSession.setOnClickListener {
            onCreateSessionClicked()
        }
    }

    private fun checkSessionIsValid(): Boolean {
        val isValid = viewModel.checkIsSetupValid()
        btnCreateSession.isEnabled = isValid

        return isValid
    }

    private fun setupMemberSelectList() {
        val members = DokoShortAccess.getMemberCtrl().getActiveMembers()
        members.forEach { m ->
            viewModel.memberSelections.add(
                MemberSelection(
                    m, false
                )
            )
        }

        memberSelectAdapter = MemberSelectAdapter(viewModel.memberSelections,
            object : MemberSelectAdapter.MemberSelectListener {
                override fun onMemberSelected(member: MemberSelection) {
                    member.isSelected = !member.isSelected
                    checkSessionIsValid()
                }
            })

        val dp4 = Converter.convertDpToPixels(4f, rvMemberSelectList.context)
        rvMemberSelectList.adapter = memberSelectAdapter
        rvMemberSelectList.layoutManager = GridLayoutManager(rvMemberSelectList.context, 2)
        rvMemberSelectList.addItemDecoration(RecyclerViewMarginDecoration(dp4, dp4))
    }

    private fun onAddMemberClicked() {
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
            .setMessage(getString(R.string.session_member_creation_short_name_advice))
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

        viewModel.memberSelections.add(MemberSelection(member, true))
        viewModel.memberInputName = ""
        memberSelectAdapter.notifyItemInserted(viewModel.memberSelections.size - 1)
    }

    private fun onCreateSessionClicked() {
        if (!viewModel.checkIsSetupValid()) {
            showSessionCreateError(getString(R.string.create_session_unable_to_create))
            return
        }

        try {
            val sessionCtrl = DoppelkopfManager.getInstance().getSessionController()

            val selectedMembers = viewModel.memberSelections.filter { ms -> ms.isSelected }
            val session = sessionCtrl.createSession(viewModel.sessionName, selectedMembers.map { ms -> ms.member })
            sessionCtrl.set(session)
            DokoShortAccess.getSessionInfoCtrl().addSessionInfo(session)

            val db = Firebase.firestore
            val firebase = DoppelkopfDatabase()
            firebase.setFirestore(db)

            val group = DokoShortAccess.getGroupCtrl().getGroup()
            firebase.storeSession(session, group)

            viewModel.reset()
            memberSelectAdapter.notifyDataSetChanged()

            sessionCreated()
        } catch (e: Exception) {
            Logging.e(
                "SessionCreationActivity",
                "Session konnte nicht erstellt werden: $viewModel",
                e
            )
            showSessionCreateError("Der Doppelkopfabend kann nicht erstellt werden: $e")
        }
    }

    private fun sessionCreated() {
        val intent = Intent(this, SessionActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showSessionCreateError(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}