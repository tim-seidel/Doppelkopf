package de.timseidel.doppelkopf.db

import com.google.firebase.firestore.FirebaseFirestore
import de.timseidel.doppelkopf.model.*

class DoppelkopfDatabase {

    private lateinit var db: FirebaseFirestore

    fun setFirestore(firestore: FirebaseFirestore) {
        db = firestore
    }

    fun storeGroup(group: Group) {
        val groupDto = FirebaseDTO.fromGroupToGroupDTO(group)

        db.collection(FirebaseStrings.collectionGroups)
            .document(group.id)
            .set(groupDto)
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

    fun storeSession(session: DokoSession, group: Group) {
        val sessionDTO = FirebaseDTO.fromSessionToSessionDTO(session)

        db.collection(FirebaseStrings.collectionGroups)
            .document(group.id)
            .collection(FirebaseStrings.collectionSessions)
            .document(session.id)
            .set(sessionDTO)
    }

    fun storePlayerInSession(player: Player, session: DokoSession, group: Group) {
        val playerDTO = FirebaseDTO.fromPlayerToPlayerDTO(player)

        db.collection(FirebaseStrings.collectionGroups)
            .document(group.id)
            .collection(FirebaseStrings.collectionSessions)
            .document(session.id)
            .collection(FirebaseStrings.collectionPlayers)
            .document(player.id)
            .set(playerDTO)
    }

    fun storePlayersInSession(players: List<Player>, session: DokoSession, group: Group) {
        val batch = db.batch()

        players.forEach { p ->
            val playerDTO = FirebaseDTO.fromPlayerToPlayerDTO(p)
            batch.set(
                db.collection(FirebaseStrings.collectionGroups)
                    .document(group.id)
                    .collection(FirebaseStrings.collectionSessions)
                    .document(session.id)
                    .collection(FirebaseStrings.collectionPlayers)
                    .document(p.id),
                playerDTO
            )
        }

        batch.commit()
    }

    fun storeGameInSession(game: Game, session: DokoSession, group: Group) {
        val gameDTO = FirebaseDTO.fromGameToGameDTO(game)
        db.collection(FirebaseStrings.collectionGroups)
            .document(group.id)
            .collection(FirebaseStrings.collectionSessions)
            .document(session.id)
            .collection(FirebaseStrings.collectionGames)
            .document(game.id)
            .set(gameDTO)
    }

    fun storeGamesInSession(games: List<Game>, session: DokoSession, group: Group) {
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
}