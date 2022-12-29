package de.timseidel.doppelkopf.db

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import de.timseidel.doppelkopf.contracts.IPlayerController
import de.timseidel.doppelkopf.model.DokoSession
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Group
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.util.Logging

class DoppelkopfDatabase {

    private lateinit var db: FirebaseFirestore

    fun setFirestore(firestore: FirebaseFirestore) {
        db = firestore
    }

    fun storeGroup(group: Group) {
        db.collection(FirebaseStrings.collectionGroups).add(group)
    }

    fun storeSession(session: DokoSession/*, group: Group*/) {
        val sessionDTO = FirebaseDTO.fromSessionToSessionDTO(session)
        db.collection(FirebaseStrings.collectionSessions).document(session.id).set(sessionDTO)

        //db.collection(FirebaseStrings.collectionGroups).document(group.id)
        //.collection(FirebaseStrings.collectionSessions).add(session.id)
    }

    fun storePlayerInSession(player: Player, session: DokoSession) {
        val playerDTO = FirebaseDTO.fromPlayerToPlayerDTO(player)
        db.collection(FirebaseStrings.collectionSessions).document(session.id)
            .collection(FirebaseStrings.collectionPlayers).document(player.id).set(playerDTO)
    }

    fun storePlayersInSession(players: List<Player>, session: DokoSession) {
        val batch = db.batch()

        players.forEach { p ->
            val playerDTO = FirebaseDTO.fromPlayerToPlayerDTO(p)
            batch.set(
                db.collection(FirebaseStrings.collectionSessions).document(session.id)
                    .collection(FirebaseStrings.collectionPlayers).document(p.id),
                playerDTO
            )
        }

        batch.commit()
    }

    fun storeGameInSession(game: Game, session: DokoSession) {
        val gameDTO = FirebaseDTO.fromGameToGameDTO(game)
        db.collection(FirebaseStrings.collectionSessions).document(session.id)
            .collection(FirebaseStrings.collectionGames).document(game.id).set(gameDTO)
    }

    fun storeGamesInSession(games: List<Game>, session: DokoSession) {
        val batch = db.batch()

        games.forEach { g ->
            val gameDTO = FirebaseDTO.fromGameToGameDTO(g)
            batch.set(
                db.collection(FirebaseStrings.collectionSessions).document(session.id)
                    .collection(FirebaseStrings.collectionGames).document(g.id),
                gameDTO
            )
        }

        batch.commit()
    }
}