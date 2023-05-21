package de.timseidel.doppelkopf.db.request

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
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

        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseStrings.collectionGroups)
            .document(groupId)
            .collection(FirebaseStrings.collectionMembers)
            .get()
            .addOnSuccessListener { docs ->
                val members = mutableListOf<Member>()
                try {
                    for (doc in docs) {
                        val memberDto = doc.toObject<MemberDto>()
                        val member = FirebaseDTO.fromMemberDTOtoMember(memberDto)
                        members.add(member)
                    }
                } catch (e: Exception) {
                    failWithLog(
                        "GroupMemberRequest: Member conversation of ${docs.size()} members failed with ",
                        e
                    )
                }

                onReadResult(members)
            }
            .addOnFailureListener { e ->
                failWithLog("GroupMemberRequest failed with ", e)
            }
    }
}