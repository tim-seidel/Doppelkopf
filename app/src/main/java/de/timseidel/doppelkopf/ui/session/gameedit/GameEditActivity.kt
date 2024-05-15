package de.timseidel.doppelkopf.ui.session.gameedit

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.databinding.ActivityGameEditBinding
import de.timseidel.doppelkopf.db.DoppelkopfDatabase
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.ui.GameConfigurationView
import de.timseidel.doppelkopf.ui.session.gamecreation.GameConfiguration
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.GameUtil
import de.timseidel.doppelkopf.util.Logging

class GameEditActivity : AppCompatActivity() {

    companion object {
        const val KEY_GAME_EDIT_ID = "GAME_EDIT_ID"
    }

    private lateinit var binding: ActivityGameEditBinding

    private lateinit var gameConfigurationView: GameConfigurationView
    private lateinit var btnUpdateGame: Button

    private var gameConfiguration: GameConfiguration = GameConfiguration()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findViews()
        setupToolbar()
        setupButtons()

        val game = getGameToEdit()
        if (game != null) {
            setupViewModel(game)
            setupGameConfiguration()
        } else {
            Toast.makeText(this, "Kein gültiges Spiel zum Ändern gefunden.", Toast.LENGTH_LONG)
                .show()
        }

        checkSaveGameButtonEnabled()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbar() {
        setTitle(R.string.title_game_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun getGameToEdit(): Game? {
        val gameId = getGameId()
        return DokoShortAccess.getGameCtrl().getGame(gameId)
    }

    private fun getGameId(): String {
        return intent.getStringExtra(KEY_GAME_EDIT_ID) ?: ""
    }

    private fun setupViewModel(game: Game) {
        gameConfiguration.playerFactionList = game.players.map { it.copy() }
        gameConfiguration.winningFaction = game.winningFaction
        gameConfiguration.tackenCount = game.tacken
        gameConfiguration.isBockrunde = game.isBockrunde
        gameConfiguration.gameType = game.gameType
    }

    private fun findViews() {
        btnUpdateGame = binding.btnConfirmEditGame
        gameConfigurationView = binding.gcvEditGameConfiguration
    }

    private fun setupButtons() {
        btnUpdateGame.setOnClickListener {
            onGameEditConfirmClicked()
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

                //Auto switch game type if solo is detected or vice versa. Does not auto switch if game type is already set to special game type
                if (GameUtil.isFactionCompositionSolo(gameConfiguration.playerFactionList) && gameConfiguration.gameType == GameType.NORMAL) {
                    gameConfiguration.gameType = GameType.SOLO
                    gameConfigurationView.setGameType(GameType.SOLO)
                }
                if (!GameUtil.isFactionCompositionSolo(gameConfiguration.playerFactionList) && gameConfiguration.gameType == GameType.SOLO) {
                    gameConfiguration.gameType = GameType.NORMAL
                    gameConfigurationView.setGameType(GameType.NORMAL)
                }

                checkSaveGameButtonEnabled()
            }

            override fun onBockrundeChanged(isBockrunde: Boolean) {
                gameConfiguration.isBockrunde = isBockrunde

                gameConfigurationView.setIsBockrunde(isBockrunde)
                checkSaveGameButtonEnabled()
            }

            override fun onGameTypeChanged(gameType: GameType) {
                gameConfiguration.gameType = gameType

                gameConfigurationView.setGameType(gameType)
                checkSaveGameButtonEnabled()
            }
        })
    }

    private fun setButtonColor(btn: Button, colorResId: Int) {
        btn.background.setTint(ContextCompat.getColor(btn.context, colorResId))
    }

    private fun checkSaveGameButtonEnabled() {
        val isValid = gameConfiguration.isValid()

        btnUpdateGame.isEnabled = isValid
        setButtonColor(btnUpdateGame, if (isValid) R.color.neural else R.color.neural_light)
    }

    private fun onGameEditConfirmClicked() {
        if (!gameConfiguration.isValid()) {
            Toast.makeText(
                this,
                getString(R.string.game_edit_error),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val gameToEdit = getGameToEdit()
        if (gameToEdit == null) {
            Toast.makeText(
                this,
                getString(R.string.game_edit_error),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        try {
            Logging.d("GameEditActivity | SAVE", gameConfiguration.toString())

            val updatedGame = Game(
                gameToEdit.id,
                gameToEdit.timestamp,
                gameConfiguration.playerFactionList.map { it.copy() },
                gameConfiguration.winningFaction,
                gameToEdit.winningPoints,
                gameConfiguration.tackenCount,
                gameConfiguration.isBockrunde,
                gameConfiguration.gameType,
                gameToEdit.soloType
            )

            //Update locally
            DokoShortAccess.getGameCtrl().updateGame(gameToEdit.id, updatedGame)

            //Update in Firestore
            val db = Firebase.firestore
            val firebase = DoppelkopfDatabase()
            firebase.setFirestore(db)
            firebase.updateGameInSession(
                updatedGame,
                DokoShortAccess.getSessionCtrl().getSession(),
                DokoShortAccess.getGroupCtrl().getGroup()
            )

            Toast.makeText(
                this,
                getString(R.string.game_updated),
                Toast.LENGTH_LONG
            ).show()

            resetViewModel()

            setResult(RESULT_OK)
            finish()
        } catch (e: Exception) {
            Logging.e("GameEditActivity", "Error editing game", e)
        }
    }

    private fun resetViewModel() {
        gameConfiguration.reset()
        gameConfigurationView.resetGameConfiguration()
    }
}