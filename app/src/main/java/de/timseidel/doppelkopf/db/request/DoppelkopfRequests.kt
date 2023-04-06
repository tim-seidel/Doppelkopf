package de.timseidel.doppelkopf.db.request

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import de.timseidel.doppelkopf.contracts.IPlayerController
import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.controller.SessionController
import de.timseidel.doppelkopf.db.FirebaseDTO
import de.timseidel.doppelkopf.db.FirebaseStrings
import de.timseidel.doppelkopf.db.GameDto
import de.timseidel.doppelkopf.db.GroupDto
import de.timseidel.doppelkopf.db.MemberDto
import de.timseidel.doppelkopf.db.PlayerDto
import de.timseidel.doppelkopf.db.SessionDto
import de.timseidel.doppelkopf.model.Session
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Group
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.Logging
import java.time.ZoneOffset

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
                        val group = FirebaseDTO.fromGroupDTOtoGroup(groupDto)
                        listener.onReadComplete(group)
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
                        val group = FirebaseDTO.fromGroupDTOtoGroup(groupDto)
                        listener.onReadComplete(group)
                    } else {
                        Logging.e("Unable to convert ${doc.size()} to GroupDTO")
                        listener.onReadFailed()
                    }
                } else {
                    Logging.e("No Group with code [$groupCode] found.")
                    listener.onReadFailed()
                }
            }
    }
}

class GroupMembersRequest(private val groupId: String) : BaseReadRequest<List<Member>>() {

    override fun execute(listener: ReadRequestListener<List<Member>>) {
        readRequestListener = listener

        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseStrings.collectionGroups)
            .document(groupId)
            .collection(FirebaseStrings.collectionMembers)
            .get()
            .addOnSuccessListener { docs ->
                val members = mutableListOf<Member>()
                for (doc in docs) {
                    val memberDto = doc.toObject<MemberDto>()
                    val member = FirebaseDTO.fromMemberDTOtoMember(memberDto)
                    members.add(member)
                }

                onReadResult(members)
            }
            .addOnFailureListener { e ->
                Logging.e("GroupMembersRequest failed with ", e)
                onReadFailed()
            }
    }
}


class SessionInfoRequest(private val groupId: String, private val sessionId: String) :
    BaseReadRequest<Session>() {

    override fun execute(listener: ReadRequestListener<Session>) {
        readRequestListener = listener

        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseStrings.collectionGroups)
            .document(groupId)
            .collection(FirebaseStrings.collectionSessions)
            .document(sessionId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    val sessionDto = doc.toObject<SessionDto>()
                    if (sessionDto != null) {
                        val session = FirebaseDTO.fromSessionDTOtoSession(sessionDto)
                        listener.onReadComplete(session)
                    } else {
                        Logging.e("Unable to convert ${doc.data} to SessionDTO")
                        listener.onReadFailed()
                    }
                } else {
                    Logging.e("No Session with id [$sessionId] found.")
                    listener.onReadFailed()
                }
            }
            .addOnFailureListener { e ->
                Logging.e("SessionInfoRequest failed with ", e)
                listener.onReadFailed()
            }
    }
}

class SessionPlayersRequest(private val groupId: String, private val sessionId: String) :
    BaseReadRequest<List<Player>>() {

    override fun execute(listener: ReadRequestListener<List<Player>>) {
        readRequestListener = listener

        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseStrings.collectionGroups)
            .document(groupId)
            .collection(FirebaseStrings.collectionSessions)
            .document(sessionId)
            .collection(FirebaseStrings.collectionPlayers)
            .get()
            .addOnSuccessListener { docs ->
                val players = mutableListOf<Player>()
                for (doc in docs) {
                    val playerDto = doc.toObject<PlayerDto>()
                    val player = FirebaseDTO.fromPlayerDTOtoPlayer(playerDto)
                    players.add(player)
                }

                onReadResult(players)
            }
            .addOnFailureListener { e ->
                Logging.e("SessionPlayersRequest failed with ", e)
                onReadFailed()
            }
    }
}

class SessionGamesRequest(
    private val groupId: String,
    private val sessionId: String,
    private val playerController: IPlayerController
) :
    BaseReadRequest<List<Game>>() {

    override fun execute(listener: ReadRequestListener<List<Game>>) {
        readRequestListener = listener

        val db = FirebaseFirestore.getInstance()

        db.collection(FirebaseStrings.collectionGroups)
            .document(groupId)
            .collection(FirebaseStrings.collectionSessions)
            .document(sessionId)
            .collection(FirebaseStrings.collectionGames).get()
            .addOnSuccessListener { docs ->
                val games = mutableListOf<Game>()
                for (doc in docs) {
                    val gameDto = doc.toObject<GameDto>()
                    val game = FirebaseDTO.fromGameDTOtoGame(gameDto, playerController)
                    games.add(game)
                }

                games.sortBy { g -> g.timestamp }

                onReadResult(games)
            }
            .addOnFailureListener { e ->
                Logging.e("SessionGamesRequest failed with ", e)
                onReadFailed()
            }
    }
}

class SessionInfoListRequest(private val groupId: String) : BaseReadRequest<List<Session>>() {
    override fun execute(listener: ReadRequestListener<List<Session>>) {
        readRequestListener = listener

        val db = FirebaseFirestore.getInstance()

        db.collection(FirebaseStrings.collectionGroups)
            .document(groupId)
            .collection(FirebaseStrings.collectionSessions)
            .get()
            .addOnSuccessListener { docs ->
                val sessions = mutableListOf<Session>()
                for (doc in docs) {
                    val sessionDto = doc.toObject<SessionDto>()
                    val session = FirebaseDTO.fromSessionDTOtoSession(sessionDto)
                    sessions.add(session)
                }

                sessions.sortBy { s -> s.date.toInstant(ZoneOffset.UTC).toEpochMilli() }

                onReadResult(sessions)
            }
            .addOnFailureListener { e ->
                Logging.e("SessionListRequest failed with ", e)
                onReadFailed()
            }
    }
}

class SessionListRequest(private val sessionInfos: List<Session>) :
    BaseReadRequest<List<ISessionController>>() {
    override fun execute(listener: ReadRequestListener<List<ISessionController>>) {
        readRequestListener = listener

        val sessions = mutableListOf<ISessionController>()
        var remainingLoadCounter = sessionInfos.size

        for (sessionInfo in sessionInfos) {
            val sessionController = SessionController()
            sessionController.set(sessionInfo)

            SessionPlayersRequest(
                DokoShortAccess.getGroupCtrl().getGroup().id,
                sessionInfo.id
            ).execute(object :
                ReadRequestListener<List<Player>> {

                override fun onReadComplete(result: List<Player>) {
                    sessionController.getPlayerController().addPlayers(result)

                    SessionGamesRequest(
                        DokoShortAccess.getGroupCtrl().getGroup().id,
                        sessionInfo.id,
                        sessionController.getPlayerController()
                    ).execute(
                        object : ReadRequestListener<List<Game>> {
                            override fun onReadComplete(result: List<Game>) {
                                result.forEach { game ->
                                    sessionController.getGameController().addGame(game)
                                }

                                sessions.add(sessionController)
                                remainingLoadCounter -= 1

                                if (remainingLoadCounter == 0) {
                                    sessions.sortBy { s ->
                                        s.getSession().date.toInstant(ZoneOffset.UTC).toEpochMilli()
                                    }

                                    onReadResult(sessions)
                                }
                            }

                            override fun onReadFailed() {
                                this.onReadFailed()
                            }
                        })
                }

                override fun onReadFailed() {
                    this.onReadFailed()
                }
            })
        }
    }
}