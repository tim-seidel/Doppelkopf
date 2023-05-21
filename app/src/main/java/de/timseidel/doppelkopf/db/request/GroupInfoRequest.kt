package de.timseidel.doppelkopf.db.request

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import de.timseidel.doppelkopf.db.FirebaseDTO
import de.timseidel.doppelkopf.db.FirebaseStrings
import de.timseidel.doppelkopf.db.GroupDto
import de.timseidel.doppelkopf.db.request.base.BaseReadRequest
import de.timseidel.doppelkopf.db.request.base.ReadRequestListener
import de.timseidel.doppelkopf.model.Group
import de.timseidel.doppelkopf.util.Logging

class GroupInfoRequestById(private val groupId: String) :
    BaseReadRequest<Group>() {
    override fun execute(listener: ReadRequestListener<Group>) {
        readRequestListener = listener

        val db = FirebaseFirestore.getInstance()

        db.collection(FirebaseStrings.collectionGroups)
            .document(groupId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    val groupDto = doc.toObject<GroupDto>()
                    if (groupDto != null) {
                        try {
                            val group = FirebaseDTO.fromGroupDTOtoGroup(groupDto)
                            listener.onReadComplete(group)
                        } catch (e: Exception) {
                            Logging.e("Unable to convert ${doc.data} to GroupDTO")
                            listener.onReadFailed()
                        }
                    } else {
                        Logging.e("Unable to convert ${doc.data} to GroupDTO")
                        listener.onReadFailed()
                    }
                } else {
                    Logging.e("No Group with code [$groupId] found.")
                    listener.onReadFailed()
                }
            }
    }
}

class GroupInfoRequestByCode(private val groupCode: String) :
    BaseReadRequest<Group>() {

    override fun execute(listener: ReadRequestListener<Group>) {
        readRequestListener = listener

        val db = FirebaseFirestore.getInstance()

        db.collection(FirebaseStrings.collectionGroups)
            .whereEqualTo("code", groupCode)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.isEmpty) {
                    val groupDto = doc.firstOrNull()?.toObject<GroupDto>()
                    if (groupDto != null) {
                        try {
                            val group = FirebaseDTO.fromGroupDTOtoGroup(groupDto)
                            listener.onReadComplete(group)
                        } catch (e: Exception) {
                            failWithLog("Unable to convert data to GroupDTO", e)
                        }
                    } else {
                        failWithLog("Unable to convert data to GroupDTO")
                    }
                } else {
                    failWithLog("No Group with code [$groupCode] found.")
                }
            }
    }
}
