package de.timseidel.doppelkopf.util

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.GameHistoryColumn
import de.timseidel.doppelkopf.model.GameHistoryItem
import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.model.PlayerAndFaction

class GameUtil {
    companion object {

        fun isFactionCompositionSolo(players: List<PlayerAndFaction>): Boolean {
            val reCount = players.count { paf -> paf.faction == Faction.RE }
            val contraCount = players.count { paf -> paf.faction == Faction.CONTRA }

            return reCount == 1 && contraCount == 3
        }

        fun getFactionTacken(faction: Faction, game: Game): Int {
            if (faction == Faction.NONE || game.winningFaction == Faction.NONE) {
                return 0
            }

            val isWinner = faction == game.winningFaction
            val isSoloFaction = game.gameType == GameType.SOLO && faction == Faction.RE

            var tacken = game.tacken
            if (!isWinner) {
                tacken *= -1
            }
            if (isSoloFaction) {
                tacken *= 3
            }

            return tacken
        }

        fun getFactionPoints(faction: Faction, winningFaction: Faction, winningPoints: Int): Int {
            if (faction == Faction.NONE || winningFaction == Faction.NONE) {
                return 0
            }
            return if (faction == winningFaction) winningPoints else 240 - winningPoints
        }

        fun getStrafTacken(result: GameResult): Int {
            return if (result.tacken < 0) -1 * result.tacken else 0
        }

        fun getPlayerResult(player: Player, game: Game): GameResult {
            val paf = game.players.firstOrNull { paf -> paf.player.id == player.id }
            return if (paf == null) {
                GameResult("", Faction.NONE, false, 0, 0, false, GameType.NORMAL)
            } else {
                GameResult(
                    game.id,
                    paf.faction,
                    paf.faction == game.winningFaction,
                    getFactionTacken(paf.faction, game),
                    getFactionPoints(paf.faction, game.winningFaction, game.winningPoints),
                    game.isBockrunde,
                    game.gameType
                )
            }
        }

        fun getGameHistory(games: List<Game>): List<GameHistoryItem> {
            val gameHistory = mutableListOf<GameHistoryItem>()
            games.forEachIndexed { i, game ->
                val scores = mutableListOf<GameHistoryColumn>()

                game.players.forEach { paf ->
                    val result = GameUtil.getPlayerResult(paf.player, game)
                    scores.add(
                        GameHistoryColumn(
                            result.faction,
                            result.tacken,
                            result.isWinner,
                            isPlayerPlayingSolo(paf.player, game)
                        )
                    )
                }

                gameHistory.add(
                    GameHistoryItem(
                        game,
                        i + 1,
                        scores,
                        game.isBockrunde

                    )
                )
            }

            return gameHistory
        }

        fun isPlayerPlayingSolo(player: Player, game: Game): Boolean {
            val result = getPlayerResult(player, game)
            return game.gameType == GameType.SOLO && result.faction == Faction.RE
        }
    }
}