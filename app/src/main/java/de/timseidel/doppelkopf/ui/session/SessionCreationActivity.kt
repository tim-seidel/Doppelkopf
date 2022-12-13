package de.timseidel.doppelkopf.ui.session

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.controller.DoppelkopfManager
import de.timseidel.doppelkopf.controller.SessionController
import de.timseidel.doppelkopf.databinding.ActivitySessionCreationBinding
import de.timseidel.doppelkopf.util.EditTextListener
import de.timseidel.doppelkopf.util.Logging

class SessionCreationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionCreationBinding
    private val viewModel = SessionCreationViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySessionCreationBinding.inflate(layoutInflater)

        setupEditTexts()

        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_session, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.option_confirm_create_session -> {
                createSessionClicked()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun createSessionClicked() {
        if(!viewModel.checkIsSetupValid()){
            showSessionCreateError(getString(R.string.create_session_unable_to_create))
            return
        }

        val playerNames = mutableListOf(
            viewModel.player1Name,
            viewModel.player2Name,
            viewModel.player3Name,
            viewModel.player4Name
        )
        if (viewModel.player5Name.isNotEmpty()) playerNames.add(viewModel.player5Name)

        try {
            val sessionController = SessionController()
            sessionController.createSession(viewModel.sessionName, playerNames)
            Logging.d("Session wurde erstellt: $playerNames")

            DoppelkopfManager.setController(sessionController)

            onSessionCreated()
        } catch (e: Exception) {
            Logging.e("Session konnte nicht erstellt werden: $playerNames", e)
            showSessionCreateError("Der Doppelkopfabend kann nicht erstellt werden: $e")
        }
    }

    private fun showSessionCreateError(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun onSessionCreated() {
        val intent = Intent(this, SessionActivity::class.java)
        startActivity(intent)
    }

    private fun setupEditTexts() {
        binding.etSessionName.addTextChangedListener(object : EditTextListener() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.sessionName = s.toString()
            }
        })
        binding.etPlayerName1.addTextChangedListener(object : EditTextListener() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.player1Name = s.toString()
            }
        })
        binding.etPlayerName2.addTextChangedListener(object : EditTextListener() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.player2Name = s.toString()
            }
        })
        binding.etPlayerName3.addTextChangedListener(object : EditTextListener() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.player3Name = s.toString()
            }
        })
        binding.etPlayerName4.addTextChangedListener(object : EditTextListener() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.player4Name = s.toString()
            }
        })
        binding.etPlayerName5.addTextChangedListener(object : EditTextListener() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.player5Name = s.toString()
            }
        })
    }
}