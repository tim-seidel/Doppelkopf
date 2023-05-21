package de.timseidel.doppelkopf.db.request

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import de.timseidel.doppelkopf.db.FirebaseDTO
import de.timseidel.doppelkopf.db.FirebaseStrings
import de.timseidel.doppelkopf.db.PlayerDto
import de.timseidel.doppelkopf.db.request.base.BaseReadRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.util.Logging

class SessionPlayerRequest(private val groupId: String, private val sessionId: String) :
    BaseReadRequest<List<Player>>() {

    override fun execute(listener: ReadRequestListener<List<Player>>) {
        readRequestListener = listener

        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseStrings.collectionGroups)
            .document(groupId)
            .collection(FirebaseStrings.collectionSessions)
            .document(sessionId)
            .collection(FirebaseStrings.collectionPlayers)
            .get()
            .addOnSuccessListener { docs ->
                val players = mutableListOf<Player>()
                try {
                    for (doc in docs) {
                        val playerDto = doc.toObject<PlayerDto>()
                        val player = FirebaseDTO.fromPlayerDTOtoPlayer(playerDto)
                        players.add(player)
                    }
                } catch (e: Exception) {
                    Logging.e(
                        "SessionPlayerRequest: Player conversation of ${docs.size()} players failed with ",
                        e
                    )
                }

                onReadResult(players)
            }
            .addOnFailureListener { e ->
                failWithLog("SessionPlayerRequest failed with ", e)
            }
    }
}
