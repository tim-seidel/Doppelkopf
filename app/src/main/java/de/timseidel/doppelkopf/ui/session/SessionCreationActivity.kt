package de.timseidel.doppelkopf.ui.session

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.controller.SessionController
import de.timseidel.doppelkopf.databinding.ActivitySessionCreationBinding
import de.timseidel.doppelkopf.util.EditTextListener

class SessionCreationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionCreationBinding
    private val viewModel = SessionCreationViewModel()

    private val sessionController: ISessionController = SessionController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySessionCreationBinding.inflate(layoutInflater)

        setupEditTexts()

        binding.btnStartSession.setOnClickListener{ onCreateSessionClicked()}

        checkIfSessionIsValid()
        setContentView(binding.root)
    }

    private fun onCreateSessionClicked(){
        val playerNames = mutableListOf<String>(viewModel.player1Name, viewModel.player2Name, viewModel.player3Name, viewModel.player4Name)
        if(viewModel.player5Name.isNotEmpty()) playerNames.add(viewModel.player5Name)

        try {
            sessionController.createSession(viewModel.sessionName, playerNames)
            Log.d("DOKO", "Session wurde erstellt: ${playerNames.toString()}")
        }catch (e: Exception){
            Log.e("DOKO", "Session konnte nicht erstellt werden: ${playerNames.toString()}")
        }
    }

    private fun setupEditTexts(){
        binding.etSessionName.addTextChangedListener(object: EditTextListener(){
            override fun afterTextChanged(s: Editable?) {
                viewModel.sessionName = s.toString()
                checkIfSessionIsValid()
            }
        })
        binding.etPlayerName1.addTextChangedListener(object: EditTextListener(){
            override fun afterTextChanged(s: Editable?) {
                viewModel.player1Name = s.toString()
                checkIfSessionIsValid()
            }
        })
        binding.etPlayerName2.addTextChangedListener(object: EditTextListener(){
            override fun afterTextChanged(s: Editable?) {
                viewModel.player2Name = s.toString()
                checkIfSessionIsValid()
            }
        })
        binding.etPlayerName3.addTextChangedListener(object: EditTextListener(){
            override fun afterTextChanged(s: Editable?) {
                viewModel.player3Name = s.toString()
                checkIfSessionIsValid()
            }
        })
        binding.etPlayerName4.addTextChangedListener(object: EditTextListener() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.player4Name = s.toString()
                checkIfSessionIsValid()
            }
        })
        binding.etPlayerName5.addTextChangedListener(object: EditTextListener() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.player5Name = s.toString()
                checkIfSessionIsValid()
            }
        })
    }

    private fun checkIfSessionIsValid(){
        val isValid = viewModel.checkIsSetupValid()

        binding.btnStartSession.isEnabled = isValid
    }
}