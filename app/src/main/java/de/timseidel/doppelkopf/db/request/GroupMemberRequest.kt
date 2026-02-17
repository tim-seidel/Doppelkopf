package de.timseidel.doppelkopf.db.request

import com.google.firebase.firestore.FirebaseFirestore
import de.timseidel.doppelkopf.db.FirebaseDTO
import de.timseidel.doppelkopf.db.FirebaseStrings
import de.timseidel.doppelkopf.db.MemberDto
import de.timseidel.doppelkopf.db.request.base.BaseReadRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.util.Logging

class GroupMemberRequest(private val groupId: String) : BaseReadRequest<List<Member>>() {

    override fun execute(listener: ReadRequestListener<List<Member>>) {
        readRequestListener = listener
        val firestore = FirebaseFirestore.getInstance()

        firestore
            .collection(FirebaseStrings.COLLECTION_GROUPS)
            .document(groupId)
            .collection(FirebaseStrings.COLLECTION_MEMBERS)
            .get()
            .addOnSuccessListener { snapshot ->
                val members = snapshot.documents.asSequence().mapNotNull { doc ->
                    runCatching {
                        val dto = doc.toObject(MemberDto::class.java) ?: return@runCatching null
                        FirebaseDTO.fromMemberDTOtoMember(dto)
                    }.getOrElse { e ->
                        // Skip malformed member documents; keep the request successful for valid records.
                        Logging.e("Member parse failed for groupId=$groupId, docId=${doc.id}", e)
                        null
                    }
                }.toList()

                onReadResult(members)
            }
            .addOnFailureListener { e ->
                failWithLog("GroupMemberRequest failed for groupId=$groupId", e)
            }
    }
}
