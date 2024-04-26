package de.timseidel.doppelkopf.ui.session.gamecreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.controller.DoppelkopfManager
import de.timseidel.doppelkopf.databinding.FragmentGameCreationBinding
import de.timseidel.doppelkopf.db.DoppelkopfDatabase
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.ui.GameConfigurationView
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.GameUtil
import de.timseidel.doppelkopf.util.Logging

class GameCreationFragment : Fragment() {

    private var _binding: FragmentGameCreationBinding? = null
    private val binding get() = _binding!!

    private lateinit var gameConfigurationView: GameConfigurationView
    private lateinit var btnSaveGame: Button

    private var gameConfiguration: GameConfiguration = GameConfiguration()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameCreationBinding.inflate(inflater, container, false)

        setupViewModel()

        findViews()
        setupToolbar()
        setupGameConfiguration()
        setupButtons()

        checkSaveGameButtonEnabled()

        return binding.rootGameCreation
    }

    private fun setupViewModel() {
        gameConfiguration.playerFactionList = DokoShortAccess.getPlayerCtrl().getPlayersAsFaction()
        gameConfiguration.winningFaction = Faction.NONE
        gameConfiguration.tackenCount = 0
        gameConfiguration.isBockrunde = false
    }

    private fun findViews() {
        btnSaveGame = binding.btnSaveGame
        gameConfigurationView = binding.gcvCreateGameConfiguration
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).supportActionBar?.title =
            DokoShortAccess.getSessionCtrl().getSession().name

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupButtons() {
        btnSaveGame.setOnClickListener {
            onCreateGameClicked()
        }
    }

    private fun setupGameConfiguration() {
        gameConfigurationView.setGameConfiguration(gameConfiguration)

        gameConfigurationView.setGameConfigurationChangeListener(object :
            GameConfigurationView.GameConfigurationChangeListener {
            override fun onTackenCountChanged(tackenCount: Int) {
                gameConfiguration.tackenCount = tackenCount

                gameConfigurationView.setTackenCount(tackenCount)
                checkSaveGameButtonEnabled()
            }

            override fun onWinningFactionChanged(winningFaction: Faction) {
                gameConfiguration.winningFaction = winningFaction

                gameConfigurationView.setWinningFaction(winningFaction)
                checkSaveGameButtonEnabled()
            }

            override fun onPlayerFactionChanged(player: Player, faction: Faction) {
                gameConfiguration.playerFactionList.find { it.player == player }?.faction = faction

                gameConfigurationView.setPlayerFaction(player, faction)
                checkSaveGameButtonEnabled()
            }

            override fun onBockrundeChanged(isBockrunde: Boolean) {
                gameConfiguration.isBockrunde = isBockrunde

                gameConfigurationView.setIsBockrunde(isBockrunde)
                checkSaveGameButtonEnabled()
            }
        })
    }

    private fun setButtonColor(btn: Button, colorResId: Int) {
        btn.background.setTint(ContextCompat.getColor(btn.context, colorResId))
    }

    private fun checkSaveGameButtonEnabled() {
        val isValid = gameConfiguration.isValid()

        btnSaveGame.isEnabled = isValid
        setButtonColor(btnSaveGame, if (isValid) R.color.neural else R.color.neural_light)
    }

    private fun onCreateGameClicked() {
        if (!gameConfiguration.isValid()) {
            Toast.makeText(
                context,
                getString(R.string.game_creation_error),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        try {
            val game = DoppelkopfManager.getInstance().getSessionController().getGameController()
                .createGame(
                    gameConfiguration.playerFactionList.map { it.copy() },
                    gameConfiguration.winningFaction,
                    0,
                    gameConfiguration.tackenCount,
                    gameConfiguration.isBockrunde,
                    if (GameUtil.isFactionCompositionSolo(gameConfiguration.playerFactionList)) GameType.SOLO else GameType.NORMAL
                )
            DokoShortAccess.getGameCtrl().addGame(game)

            val db = Firebase.firestore

            val firebase = DoppelkopfDatabase()
            firebase.setFirestore(db)

            firebase.storeGameInSession(
                game,
                DokoShortAccess.getSessionCtrl().getSession(),
                DokoShortAccess.getGroupCtrl().getGroup()
            )

            Toast.makeText(
                context,
                getString(R.string.game_saved),
                Toast.LENGTH_LONG
            ).show()

            resetViewModel()
        } catch (e: Exception) {
            Logging.e("GameCreationFragment", "Error creating game", e)
        }
    }

    private fun resetViewModel() {
        gameConfiguration.reset()
        gameConfigurationView.resetGameConfiguration()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}