package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.MemberAndFaction

interface IGameController {

    fun createGame(
        members: List<MemberAndFaction>,
        winningFaction: Faction,
        winningPoints: Int,
        tacken: Int,
        isBockrunde: Boolean,
        gameType: GameType
    ): Game

    fun addGame(game: Game)

    fun updateGame(gameId: String, updatedGame: Game)

    fun removeGame(game: Game)

    fun getGame(gameId: String): Game?

    fun getGames(): List<Game>

    fun getGameAsMemberResults(game: Game): List<GameResult>

    fun getGamesAsMemberResults(): List<List<GameResult>>

    fun getGamesOfMember(memberId: String): List<Game>

    fun reset()
}