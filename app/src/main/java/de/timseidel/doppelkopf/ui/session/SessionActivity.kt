package de.timseidel.doppelkopf.ui.session

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.controller.SessionController
import de.timseidel.doppelkopf.databinding.ActivitySessionBinding
import de.timseidel.doppelkopf.db.DoppelkopfDatabase
import de.timseidel.doppelkopf.db.FirebaseDTO
import de.timseidel.doppelkopf.db.FirebaseStrings
import de.timseidel.doppelkopf.db.GameDto
import de.timseidel.doppelkopf.db.PlayerDto
import de.timseidel.doppelkopf.db.SessionDto
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.Logging

class SessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
    }

    private fun portGroup() {
        val sessionId = "session_id"
        val group = DokoShortAccess.getGroupCtrl().getGroup()

        val ctrl = SessionController()
        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseStrings.collectionSessions)
            .document(sessionId)
            .get()
            .addOnSuccessListener { sessionDoc ->
                if (sessionDoc != null) {
                    val sessionDto = sessionDoc.toObject<SessionDto>()
                    if (sessionDto != null) {
                        val session = FirebaseDTO.fromSessionDTOtoSession(sessionDto)
                        ctrl.set(session)

                        db.collection(FirebaseStrings.collectionSessions)
                            .document(sessionId)
                            .collection(FirebaseStrings.collectionPlayers)
                            .get()
                            .addOnSuccessListener { docs ->
                                val players = mutableListOf<Player>()
                                for (doc in docs) {
                                    val playerDto = doc.toObject<PlayerDto>()
                                    val player = FirebaseDTO.fromPlayerDTOtoPlayer(playerDto)
                                    players.add(player)
                                }

                                ctrl.getPlayerController().addPlayers(players)

                                db.collection(FirebaseStrings.collectionSessions)
                                    .document(sessionId)
                                    .collection(FirebaseStrings.collectionGames).get()
                                    .addOnSuccessListener { gameDocs ->
                                        val games = mutableListOf<Game>()
                                        for (gameDoc in gameDocs) {
                                            val gameDto = gameDoc.toObject<GameDto>()
                                            val game = FirebaseDTO.fromGameDTOtoGame(
                                                gameDto,
                                                ctrl.getPlayerController()
                                            )
                                            games.add(game)
                                        }

                                        games.sortBy { g -> g.timestamp }
                                        games.forEach { g ->
                                            ctrl.getGameController().addGame(g)
                                        }


                                        val firebase = DoppelkopfDatabase()
                                        firebase.setFirestore(db)

                                        firebase.storeSession(ctrl.getSession(), group)
                                        firebase.storePlayersInSession(
                                            ctrl.getPlayerController().getPlayers(),
                                            ctrl.getSession(),
                                            group
                                        )
                                        ctrl.getGameController().getGames().forEach { game ->
                                            firebase.storeGameInSession(
                                                game,
                                                ctrl.getSession(),
                                                group
                                            )
                                        }

                                    }
                                    .addOnFailureListener { e ->
                                        Logging.e("PORTING", "SessionGameRequest failed with ", e)
                                    }
                            }
                            .addOnFailureListener { e ->
                                Logging.e("PORTING", "SessionPlayersRequest failed with ", e)
                            }

                    } else {
                        Logging.e("PORTING", "Unable to convert ${sessionDoc.data} to SessionDTO")
                    }
                } else {
                    Logging.e("PORTING", "No Session with id [$sessionId] found.")
                }
            }
            .addOnFailureListener { e ->
                Logging.e("PORTING", "SessionInfoRequest failed with ", e)
            }
    }

    private fun setupBottomNavigation() {
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_session)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_game_creation,
                R.id.navigation_game_history,
                R.id.navigation_session_statistic
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_session, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_session_show_group_code) {
            showGroupCode()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showGroupCode() {
        val groupCode = DokoShortAccess.getGroupCtrl().getGroup().code
        AlertDialog.Builder(this)
            .setTitle("Gruppencode")
            .setMessage("Der Gruppencode ist: $groupCode")
            .setPositiveButton("Okay") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}