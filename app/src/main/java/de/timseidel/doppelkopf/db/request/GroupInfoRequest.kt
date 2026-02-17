package de.timseidel.doppelkopf.db.request

import de.timseidel.doppelkopf.db.FirebaseDTO
import de.timseidel.doppelkopf.db.FirebaseStrings
import de.timseidel.doppelkopf.db.GroupDto
import de.timseidel.doppelkopf.db.request.base.BaseReadRequest
import de.timseidel.doppelkopf.model.Group
import de.timseidel.doppelkopf.model.GroupSettings
import de.timseidel.doppelkopf.util.Logging

class GroupInfoRequestById(private val groupId: String) :
    BaseReadRequest<Pair<Group, GroupSettings>>() {
    override fun doExecute() {
        firestore.collection(FirebaseStrings.COLLECTION_GROUPS)
            .document(groupId)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    failWithLog("No group with id [$groupId] found.")
                    return@addOnSuccessListener
                }

                val groupDto = doc.toObject(GroupDto::class.java)
                val groupAndSettings = groupDto?.toGroupAndSettings()
                if (groupAndSettings != null) {
                    onReadResult(groupAndSettings)
                } else {
                    failWithLog("Unable to convert group data for id [$groupId].")
                }
            }
            .addOnFailureListener { e ->
                failWithLog("GroupInfoRequestById failed for groupId=$groupId", e)
            }
    }
}

class GroupInfoRequestByCode(private val groupCode: String) :
    BaseReadRequest<Pair<Group, GroupSettings>>() {

    override fun doExecute() {
        firestore.collection(FirebaseStrings.COLLECTION_GROUPS)
            .whereEqualTo("code", groupCode)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                val groupDto = snapshot.documents.firstOrNull()?.toObject(GroupDto::class.java)
                val groupAndSettings = groupDto?.toGroupAndSettings()
                if (groupAndSettings != null) {
                    onReadResult(groupAndSettings)
                } else {
                    failWithLog("No group with code [$groupCode] found or conversion failed.")
                }
            }
            .addOnFailureListener { e ->
                failWithLog("GroupInfoRequestByCode failed for groupCode=$groupCode", e)
            }
    }
}

class GroupCodeExistsRequest(private val groupCode: String) : BaseReadRequest<Boolean>() {
    override fun doExecute() {
        firestore.collection(FirebaseStrings.COLLECTION_GROUPS)
            .whereEqualTo("code", groupCode)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                onReadResult(!snapshot.isEmpty)
            }
            .addOnFailureListener { e ->
                failWithLog("GroupCodeExistsRequest failed for groupCode=$groupCode", e)
            }
    }
}

private fun GroupDto.toGroupAndSettings(): Pair<Group, GroupSettings>? {
    return runCatching {
        val group = FirebaseDTO.fromGroupDTOtoGroup(this)
        val settings = FirebaseDTO.fromGroupDTOtoGroupSettings(this)
        Pair(group, settings)
    }.getOrElse { e ->
        Logging.e("Unable to convert GroupDto to Group/GroupSettings", e)
        null
    }
}
