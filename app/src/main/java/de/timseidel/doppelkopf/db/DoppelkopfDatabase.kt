package de.timseidel.doppelkopf.db

import com.google.firebase.firestore.FirebaseFirestore
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Group
import de.timseidel.doppelkopf.model.GroupSettings
import de.timseidel.doppelkopf.model.Member

class DoppelkopfDatabase {

    private lateinit var db: FirebaseFirestore

    fun setFirestore(firestore: FirebaseFirestore) {
        db = firestore
    }

    fun storeGroup(group: Group, groupSettings: GroupSettings) {
        val groupDto = FirebaseDTO.fromGroupToGroupDTO(group, groupSettings)

        db.collection(FirebaseStrings.collectionGroups)
            .document(group.id)
            .set(groupDto)
    }

    fun storeGroupSettings(group: Group, groupSettings: GroupSettings) {
        db.collection(FirebaseStrings.collectionGroups)
            .document(group.id)
            .update("settingIsBockrundeEnabled", groupSettings.isBockrundeEnabled)
    }

    fun storeMember(member: Member, group: Group) {
        val memberDto = FirebaseDTO.fromMemberToMemberDTO(member)

        db.collection(FirebaseStrings.collectionGroups)
            .document(group.id)
            .collection(FirebaseStrings.collectionMembers)
            .document(member.id)
            .set(memberDto)
    }

    fun storeMembers(members: List<Member>, group: Group) {
        val batch = db.batch()

        members.forEach { m ->
            val memberDto = FirebaseDTO.fromMemberToMemberDTO(m)
            batch.set(
                db.collection(FirebaseStrings.collectionGroups)
                    .document(group.id)
                    .collection(FirebaseStrings.collectionMembers)
                    .document(m.id),
                memberDto
            )
        }

        batch.commit()
    }

    fun storeSession(session: Session, group: Group) {
        val sessionDTO = FirebaseDTO.fromSessionToSessionDTO(session)

        db.collection(FirebaseStrings.collectionGroups)
            .document(group.id)
            .collection(FirebaseStrings.collectionSessions)
            .document(session.id)
            .set(sessionDTO)
    }

    fun storeGameInSession(game: Game, session: Session, group: Group) {
        val gameDTO = FirebaseDTO.fromGameToGameDTO(game)
        db.collection(FirebaseStrings.collectionGroups)
            .document(group.id)
            .collection(FirebaseStrings.collectionSessions)
            .document(session.id)
            .collection(FirebaseStrings.collectionGames)
            .document(game.id)
            .set(gameDTO)
    }

    fun storeGamesInSession(games: List<Game>, session: Session, group: Group) {
        val batch = db.batch()

        games.forEach { g ->
            val gameDTO = FirebaseDTO.fromGameToGameDTO(g)
            batch.set(
                db.collection(FirebaseStrings.collectionGroups)
                    .document(group.id)
                    .collection(FirebaseStrings.collectionSessions)
                    .document(session.id)
                    .collection(FirebaseStrings.collectionGames)
                    .document(g.id),
                gameDTO
            )
        }

        batch.commit()
    }

    fun updateGameInSession(updatedGame: Game, session: Session, group: Group) {
        val gameDTO = FirebaseDTO.fromGameToGameDTO(updatedGame)
        db.collection(FirebaseStrings.collectionGroups)
            .document(group.id)
            .collection(FirebaseStrings.collectionSessions)
            .document(session.id)
            .collection(FirebaseStrings.collectionGames)
            .document(updatedGame.id)
            .set(gameDTO)
    }

    fun updateSessionMembers(session: Session, group: Group) {
        val sessionDto = FirebaseDTO.fromSessionToSessionDTO(session)
        db.collection(FirebaseStrings.collectionGroups)
            .document(group.id)
            .collection(FirebaseStrings.collectionSessions)
            .document(session.id)
            .update("memberIds", sessionDto.memberIds)
    }
}