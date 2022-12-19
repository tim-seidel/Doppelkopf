package de.timseidel.doppelkopf.db

import com.google.firebase.firestore.FirebaseFirestore
import de.timseidel.doppelkopf.model.DokoSession
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Player

//TODO: Change to dtos
class DoppelkopfDatabase {

    private lateinit var db: FirebaseFirestore

    fun setFirestore(firestore: FirebaseFirestore) {
        db = firestore
    }

    fun storePlayer(player: Player) {
        db.collection(FirebaseStrings.collectionUsers).add(player)
    }

    fun storeSession(session: DokoSession) {
        db.collection(FirebaseStrings.collectionSessions).add(session)
    }

    fun storePlayerInSession(player: Player, session: DokoSession) {
        db.collection(FirebaseStrings.collectionSessions).document(session.id)
            .collection(FirebaseStrings.collectionPlayers).add(player)
    }

    fun storeGame(game: Game, session: DokoSession) {
        db.collection(FirebaseStrings.collectionSessions).document(session.id)
            .collection(FirebaseStrings.collectionGames).add(game)
    }
}