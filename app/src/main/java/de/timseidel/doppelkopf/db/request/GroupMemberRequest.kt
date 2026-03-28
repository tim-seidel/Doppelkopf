package de.timseidel.doppelkopf.db.request

import de.timseidel.doppelkopf.db.FirebaseDTO
import de.timseidel.doppelkopf.db.FirebaseStrings
import de.timseidel.doppelkopf.db.MemberDto
import de.timseidel.doppelkopf.db.request.base.BaseReadRequest
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.util.Logging

class GroupMemberRequest(private val groupId: String) : BaseReadRequest<List<Member>>() {

    override fun doExecute() {
        firestore
            .collection(FirebaseStrings.COLLECTION_GROUPS)
            .document(groupId)
            .collection(FirebaseStrings.COLLECTION_MEMBERS)
            .get()
            .addOnSuccessListener { snapshot ->
                try {
                    val members = snapshot.documents.asSequence().mapNotNull { doc ->
                        runCatching {
                            val dto = doc.toObject(MemberDto::class.java) ?: return@runCatching null
                            FirebaseDTO.fromMemberDTOtoMember(dto)
                        }.getOrElse { e ->
                            Logging.e("Member parse failed for groupId=$groupId, docId=${doc.id}", e)
                            null
                        }
                    }.toList()

                    onReadResult(members)
                } catch (e: Exception) {
                    failWithLog("GroupMemberRequest handling failed for groupId=$groupId", e)
                }
            }
            .addOnFailureListener { e ->
                failWithLog("GroupMemberRequest failed for groupId=$groupId", e)
            }
    }
}
