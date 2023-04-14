package de.timseidel.doppelkopf.db.request

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import de.timseidel.doppelkopf.contracts.IPlayerController
import de.timseidel.doppelkopf.db.FirebaseDTO
import de.timseidel.doppelkopf.db.FirebaseStrings
import de.timseidel.doppelkopf.db.GameDto
import de.timseidel.doppelkopf.db.request.base.BaseReadRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.util.Logging

class SessionGameRequest(
    private val groupId: String,
    private val sessionId: String,
    private val playerController: IPlayerController
) :
    BaseReadRequest<List<Game>>() {

    override fun execute(listener: ReadRequestListener<List<Game>>) {
        readRequestListener = listener

        val db = FirebaseFirestore.getInstance()

        db.collection(FirebaseStrings.collectionGroups)
            .document(groupId)
            .collection(FirebaseStrings.collectionSessions)
            .document(sessionId)
            .collection(FirebaseStrings.collectionGames).get()
            .addOnSuccessListener { docs ->
                val games = mutableListOf<Game>()
                for (doc in docs) {
                    try{
                        val gameDto = doc.toObject<GameDto>()
                        val game = FirebaseDTO.fromGameDTOtoGame(gameDto, playerController)
                        games.add(game)
                    }catch (e: Exception) {
                        //Skipping this game and continue with the next others, does not return failure
                        Logging.e("SessionGameRequest: Game conversation of ${doc.data} failed with ", e)
                    }
                }

                games.sortBy { g -> g.timestamp }

                onReadResult(games)
            }
            .addOnFailureListener { e ->
                Logging.e("SessionGameRequest failed with ", e)
                onReadFailed()
            }
    }
}