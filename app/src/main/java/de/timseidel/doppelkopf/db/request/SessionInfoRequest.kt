package de.timseidel.doppelkopf.db.request

import com.google.firebase.firestore.AggregateSource
import de.timseidel.doppelkopf.db.FirebaseDTO
import de.timseidel.doppelkopf.db.FirebaseStrings
import de.timseidel.doppelkopf.db.SessionDto
import de.timseidel.doppelkopf.db.request.base.BaseReadRequest
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.Logging
import java.time.ZoneOffset

class SessionInfoRequest(private val groupId: String, private val sessionId: String) :
    BaseReadRequest<Session>() {

    override fun doExecute() {
        firestore.collection(FirebaseStrings.COLLECTION_GROUPS)
            .document(groupId)
            .collection(FirebaseStrings.COLLECTION_SESSIONS)
            .document(sessionId)
            .get()
            .addOnSuccessListener { doc ->
                try {
                    if (!doc.exists()) {
                        failWithLog("No session with id [$sessionId] found for groupId=$groupId.")
                        return@addOnSuccessListener
                    }

                    val sessionDto = doc.toObject(SessionDto::class.java)
                    val session = sessionDto?.toSession()
                    if (session != null) {
                        onReadResult(session)
                    } else {
                        failWithLog("Unable to convert session data for groupId=$groupId, sessionId=$sessionId.")
                    }
                } catch (e: Exception) {
                    failWithLog(
                        "SessionInfoRequest conversion failed for groupId=$groupId, sessionId=$sessionId",
                        e
                    )
                }
            }
            .addOnFailureListener { e ->
                failWithLog(
                    "SessionInfoRequest failed for groupId=$groupId, sessionId=$sessionId",
                    e
                )
            }
    }
}

class SessionInfoListRequest(private val groupId: String) : BaseReadRequest<List<Session>>() {
    override fun doExecute() {
        firestore.collection(FirebaseStrings.COLLECTION_GROUPS)
            .document(groupId)
            .collection(FirebaseStrings.COLLECTION_SESSIONS)
            .get()
            .addOnSuccessListener { snapshot ->
                try {
                    val sessions = snapshot.documents.asSequence().mapNotNull { doc ->
                        runCatching {
                            val sessionDto = doc.toObject(SessionDto::class.java) ?: return@runCatching null
                            FirebaseDTO.fromSessionDTOtoSession(sessionDto, DokoShortAccess.getMemberCtrl())
                        }.getOrElse { e ->
                            Logging.e(
                                "SessionInfoListRequest: Session parse failed for groupId=$groupId, docId=${doc?.id}",
                                e
                            )
                            null
                        }
                    }.sortedBy { it.date.toInstant(ZoneOffset.UTC).toEpochMilli() }.toList()

                    onReadResult(sessions)
                } catch (e: Exception) {
                    failWithLog("SessionInfoListRequest handling failed for groupId=$groupId", e)
                }
            }
            .addOnFailureListener { e ->
                failWithLog("SessionInfoListRequest failed for groupId=$groupId", e)
            }
    }
}

class SessionCountRequest(private val groupId: String) : BaseReadRequest<Int>() {
    override fun doExecute() {
        firestore.collection(FirebaseStrings.COLLECTION_GROUPS)
            .document(groupId)
            .collection(FirebaseStrings.COLLECTION_SESSIONS)
            .count()
            .get(AggregateSource.SERVER)
            .addOnSuccessListener { response ->
                try {
                    onReadResult(response.count.toInt())
                } catch (e: Exception) {
                    failWithLog("SessionCountRequest handling failed for groupId=$groupId", e)
                }
            }.addOnFailureListener { e ->
                failWithLog("SessionCountRequest failed for groupId=$groupId", e)
            }
    }
}

private fun SessionDto.toSession(): Session? {
    return runCatching {
        FirebaseDTO.fromSessionDTOtoSession(this, DokoShortAccess.getMemberCtrl())
    }.getOrElse { e ->
        Logging.e("Unable to convert SessionDto to Session", e)
        null
    }
}
