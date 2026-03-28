package de.timseidel.doppelkopf.db.request

import com.google.firebase.firestore.AggregateSource
import de.timseidel.doppelkopf.contracts.IMemberController
import de.timseidel.doppelkopf.db.FirebaseDTO
import de.timseidel.doppelkopf.db.FirebaseStrings
import de.timseidel.doppelkopf.db.GameDto
import de.timseidel.doppelkopf.db.request.base.BaseReadRequest
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.util.Logging

class SessionGameRequest(
    private val groupId: String,
    private val sessionId: String,
    private val memberController: IMemberController
) : BaseReadRequest<List<Game>>() {

    override fun doExecute() {
        firestore.collection(FirebaseStrings.COLLECTION_GROUPS)
            .document(groupId)
            .collection(FirebaseStrings.COLLECTION_SESSIONS)
            .document(sessionId)
            .collection(FirebaseStrings.COLLECTION_GAMES)
            .get()
            .addOnSuccessListener { snapshot ->
                try {
                    val games = snapshot.documents.asSequence().mapNotNull { doc ->
                        runCatching {
                            val gameDto = doc.toObject(GameDto::class.java) ?: return@runCatching null
                            FirebaseDTO.fromGameDTOtoGame(gameDto, memberController)
                        }.getOrElse { e ->
                            Logging.e(
                                "SessionGameRequest: Game parse failed for groupId=$groupId, sessionId=$sessionId, docId=${doc.id}",
                                e
                            )
                            null
                        }
                    }.sortedBy { it.timestamp }.toList()

                    onReadResult(games)
                } catch (e: Exception) {
                    failWithLog(
                        "SessionGameRequest handling failed for groupId=$groupId, sessionId=$sessionId",
                        e
                    )
                }
            }
            .addOnFailureListener { e ->
                failWithLog("SessionGameRequest failed for groupId=$groupId, sessionId=$sessionId", e)
            }
    }
}

class SessionGameCountRequest(
    private val groupId: String,
    private val sessionId: String
) : BaseReadRequest<Int>() {

    override fun doExecute() {
        firestore.collection(FirebaseStrings.COLLECTION_GROUPS)
            .document(groupId)
            .collection(FirebaseStrings.COLLECTION_SESSIONS)
            .document(sessionId)
            .collection(FirebaseStrings.COLLECTION_GAMES)
            .count()
            .get(AggregateSource.SERVER)
            .addOnSuccessListener { response ->
                try {
                    onReadResult(response.count.toInt())
                } catch (e: Exception) {
                    failWithLog(
                        "SessionGameCountRequest success handling failed for groupId=$groupId, sessionId=$sessionId",
                        e
                    )
                }
            }.addOnFailureListener { e ->
                failWithLog(
                    "SessionGameCountRequest failed for groupId=$groupId, sessionId=$sessionId",
                    e
                )
            }
    }
}
