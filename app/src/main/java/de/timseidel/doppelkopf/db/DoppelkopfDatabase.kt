package de.timseidel.doppelkopf.db

import com.google.firebase.firestore.FirebaseFirestore
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Group
import de.timseidel.doppelkopf.model.GroupSettings
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.util.Logging

class DoppelkopfDatabase {

    private lateinit var db: FirebaseFirestore

    fun setFirestore(firestore: FirebaseFirestore) {
        db = firestore
    }

    fun storeGroup(group: Group, groupSettings: GroupSettings) {
        val groupDto = FirebaseDTO.fromGroupToGroupDTO(group, groupSettings)

        db.collection(FirebaseStrings.COLLECTION_GROUPS)
            .document(group.id)
            .set(groupDto)
            .addOnFailureListener { e -> Logging.e("DoppelkopfDatabase", "storeGroup failed: ${e.message}") }
    }

    fun storeGroupSettings(group: Group, groupSettings: GroupSettings) {
        db.collection(FirebaseStrings.COLLECTION_GROUPS)
            .document(group.id)
            .update("settingIsBockrundeEnabled", groupSettings.isBockrundeEnabled)
            .addOnFailureListener { e -> Logging.e("DoppelkopfDatabase", "storeGroupSettings failed: ${e.message}") }
    }

    fun storeMember(member: Member, group: Group) {
        val memberDto = FirebaseDTO.fromMemberToMemberDTO(member)

        db.collection(FirebaseStrings.COLLECTION_GROUPS)
            .document(group.id)
            .collection(FirebaseStrings.COLLECTION_MEMBERS)
            .document(member.id)
            .set(memberDto)
            .addOnFailureListener { e -> Logging.e("DoppelkopfDatabase", "storeMember failed: ${e.message}") }
    }

    fun storeMembers(members: List<Member>, group: Group) {
        val batch = db.batch()

        members.forEach { m ->
            val memberDto = FirebaseDTO.fromMemberToMemberDTO(m)
            batch.set(
                db.collection(FirebaseStrings.COLLECTION_GROUPS)
                    .document(group.id)
                    .collection(FirebaseStrings.COLLECTION_MEMBERS)
                    .document(m.id),
                memberDto
            )
        }

        batch.commit()
            .addOnFailureListener { e -> Logging.e("DoppelkopfDatabase", "storeMembers failed: ${e.message}") }
    }

    fun storeSession(session: Session, group: Group) {
        val sessionDTO = FirebaseDTO.fromSessionToSessionDTO(session)

        db.collection(FirebaseStrings.COLLECTION_GROUPS)
            .document(group.id)
            .collection(FirebaseStrings.COLLECTION_SESSIONS)
            .document(session.id)
            .set(sessionDTO)
            .addOnFailureListener { e -> Logging.e("DoppelkopfDatabase", "storeSession failed: ${e.message}") }
    }

    fun storeGameInSession(game: Game, session: Session, group: Group) {
        val gameDTO = FirebaseDTO.fromGameToGameDTO(game)
        db.collection(FirebaseStrings.COLLECTION_GROUPS)
            .document(group.id)
            .collection(FirebaseStrings.COLLECTION_SESSIONS)
            .document(session.id)
            .collection(FirebaseStrings.COLLECTION_GAMES)
            .document(game.id)
            .set(gameDTO)
            .addOnFailureListener { e -> Logging.e("DoppelkopfDatabase", "storeGameInSession failed: ${e.message}") }
    }

    fun storeGamesInSession(games: List<Game>, session: Session, group: Group) {
        val batch = db.batch()

        games.forEach { g ->
            val gameDTO = FirebaseDTO.fromGameToGameDTO(g)
            batch.set(
                db.collection(FirebaseStrings.COLLECTION_GROUPS)
                    .document(group.id)
                    .collection(FirebaseStrings.COLLECTION_SESSIONS)
                    .document(session.id)
                    .collection(FirebaseStrings.COLLECTION_GAMES)
                    .document(g.id),
                gameDTO
            )
        }

        batch.commit()
            .addOnFailureListener { e -> Logging.e("DoppelkopfDatabase", "storeGamesInSession failed: ${e.message}") }
    }

    fun updateGameInSession(updatedGame: Game, session: Session, group: Group) {
        val gameDTO = FirebaseDTO.fromGameToGameDTO(updatedGame)
        db.collection(FirebaseStrings.COLLECTION_GROUPS)
            .document(group.id)
            .collection(FirebaseStrings.COLLECTION_SESSIONS)
            .document(session.id)
            .collection(FirebaseStrings.COLLECTION_GAMES)
            .document(updatedGame.id)
            .set(gameDTO)
            .addOnFailureListener { e -> Logging.e("DoppelkopfDatabase", "updateGameInSession failed: ${e.message}") }
    }

    fun updateSessionMembers(session: Session, group: Group) {
        val sessionDto = FirebaseDTO.fromSessionToSessionDTO(session)
        db.collection(FirebaseStrings.COLLECTION_GROUPS)
            .document(group.id)
            .collection(FirebaseStrings.COLLECTION_SESSIONS)
            .document(session.id)
            .update("memberIds", sessionDto.memberIds)
            .addOnFailureListener { e -> Logging.e("DoppelkopfDatabase", "updateSessionMembers failed: ${e.message}") }
    }

    fun updateMember(member: Member, group: Group) {
        val memberDto = FirebaseDTO.fromMemberToMemberDTO(member)
        db.collection(FirebaseStrings.COLLECTION_GROUPS)
            .document(group.id)
            .collection(FirebaseStrings.COLLECTION_MEMBERS)
            .document(member.id)
            .set(memberDto)
            .addOnFailureListener { e -> Logging.e("DoppelkopfDatabase", "updateMember failed: ${e.message}") }
    }

    fun updateMembers(members: List<Member>, group: Group) {
        val batch = db.batch()

        members.forEach { m ->
            val memberDto = FirebaseDTO.fromMemberToMemberDTO(m)
            batch.set(
                db.collection(FirebaseStrings.COLLECTION_GROUPS)
                    .document(group.id)
                    .collection(FirebaseStrings.COLLECTION_MEMBERS)
                    .document(m.id),
                memberDto
            )
        }

        batch.commit()
            .addOnFailureListener { e -> Logging.e("DoppelkopfDatabase", "updateMembers failed: ${e.message}") }
    }
}