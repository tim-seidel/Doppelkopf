package de.timseidel.doppelkopf.db.request

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import de.timseidel.doppelkopf.db.FirebaseDTO
import de.timseidel.doppelkopf.db.FirebaseStrings
import de.timseidel.doppelkopf.db.SessionDto
import de.timseidel.doppelkopf.db.request.base.BaseReadRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.util.Logging
import java.time.ZoneOffset

class SessionInfoRequest(private val groupId: String, private val sessionId: String) :
    BaseReadRequest<Session>() {

    override fun execute(listener: ReadRequestListener<Session>) {
        readRequestListener = listener

        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseStrings.collectionGroups)
            .document(groupId)
            .collection(FirebaseStrings.collectionSessions)
            .document(sessionId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    val sessionDto = doc.toObject<SessionDto>()
                    if (sessionDto != null) {
                        try {
                            val session = FirebaseDTO.fromSessionDTOtoSession(sessionDto)
                            listener.onReadComplete(session)
                        } catch (e: Exception) {
                            Logging.e("Unable to convert ${doc.data} to SessionDTO")
                            listener.onReadFailed()
                        }
                    } else {
                        Logging.e("Unable to convert ${doc.data} to SessionDTO")
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

class SessionInfoListRequest(private val groupId: String) : BaseReadRequest<List<Session>>() {
    override fun execute(listener: ReadRequestListener<List<Session>>) {
        readRequestListener = listener

        val db = FirebaseFirestore.getInstance()

        db.collection(FirebaseStrings.collectionGroups)
            .document(groupId)
            .collection(FirebaseStrings.collectionSessions)
            .get()
            .addOnSuccessListener { docs ->
                val sessions = mutableListOf<Session>()
                for (doc in docs) {
                    try {
                        val sessionDto = doc.toObject<SessionDto>()
                        val session = FirebaseDTO.fromSessionDTOtoSession(sessionDto)
                        sessions.add(session)
                    } catch (e: Exception) {
                        //Skipping this session and continue with the next others, does not return failure
                        Logging.e(
                            "SessionInfoListRequest: SessionInfo conversation of ${doc.data} failed with ",
                            e
                        )
                    }
                }

                sessions.sortBy { s -> s.date.toInstant(ZoneOffset.UTC).toEpochMilli() }

                onReadResult(sessions)
            }
            .addOnFailureListener { e ->
                Logging.e("SessionListRequest failed with ", e)
                onReadFailed()
            }
    }
}