package de.timseidel.doppelkopf.db.request

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import de.timseidel.doppelkopf.contracts.IPlayerController
import de.timseidel.doppelkopf.db.*
import de.timseidel.doppelkopf.model.DokoSession
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.util.Logging

class SessionInfoRequest(private val sessionId: String) : BaseReadRequest<DokoSession>() {

    override fun execute(listener: ReadRequestListener<DokoSession>) {
        readRequestListener = listener

        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseStrings.collectionSessions).document(sessionId).get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    val sessionDto = doc.toObject<SessionDto>()
                    if (sessionDto != null) {
                        val session = FirebaseDTO.fromSessionDTOtoSession(sessionDto)
                        listener.onReadComplete(session)
                    } else {
                        Logging.e("Unable to convert ${doc.data} to sessionDTO")
                        listener.onReadFailed()
                    }
                } else {
                    Logging.e("No Session with id [$sessionId] found.")
                    listener.onReadFailed()
                }
            }
            .addOnFailureListener { e ->
                Logging.e("SessionInfoRequest failed with ", e)
                listener.onReadFailed()
            }
    }
}

class SessionPlayersRequest(private val sessionId: String) : BaseReadRequest<List<Player>>() {

    override fun execute(listener: ReadRequestListener<List<Player>>) {
        readRequestListener = listener

        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseStrings.collectionSessions).document(sessionId)
            .collection(FirebaseStrings.collectionPlayers).get()
            .addOnSuccessListener { docs ->
                val players = mutableListOf<Player>()
                for (doc in docs) {
                    val playerDto = doc.toObject<PlayerDto>()
                    val player = FirebaseDTO.fromPlayerDTOtoPlayer(playerDto)
                    players.add(player)
                }

                onReadResult(players)
            }
            .addOnFailureListener { e ->
                Logging.e("SessionPlayersRequest failed with ", e)
                onReadFailed()
            }
    }
}

class SessionGamesRequest(
    private val sessionId: String,
    private val playerController: IPlayerController
) :
    BaseReadRequest<List<Game>>() {

    override fun execute(listener: ReadRequestListener<List<Game>>) {
        readRequestListener = listener

        val db = FirebaseFirestore.getInstance()

        db.collection(FirebaseStrings.collectionSessions).document(sessionId)
            .collection(FirebaseStrings.collectionGames).get()
            .addOnSuccessListener { docs ->
                val games = mutableListOf<Game>()
                for (doc in docs) {
                    val gameDto = doc.toObject<GameDto>()
                    val game = FirebaseDTO.fromGameDTOtoGame(gameDto, playerController)
                    games.add(game)
                }

                games.sortBy { g -> g.timestamp }

                onReadResult(games)
            }
            .addOnFailureListener { e ->
                Logging.e("SessionGamesRequest failed with ", e)
                onReadFailed()
            }
    }
}

class SessionListRequest : BaseReadRequest<List<DokoSession>>() {
    override fun execute(listener: ReadRequestListener<List<DokoSession>>) {
        readRequestListener = listener

        val db = FirebaseFirestore.getInstance()

        db.collection(FirebaseStrings.collectionSessions).get()
            .addOnSuccessListener { docs ->
                val sessions = mutableListOf<DokoSession>()
                for (doc in docs) {
                    val sessionDto = doc.toObject<SessionDto>()
                    val session = FirebaseDTO.fromSessionDTOtoSession(sessionDto)
                    sessions.add(session)
                }

                onReadResult(sessions)
            }
            .addOnFailureListener { e ->
                Logging.e("SessionListRequest failed with ", e)
                onReadFailed()
            }
    }
}