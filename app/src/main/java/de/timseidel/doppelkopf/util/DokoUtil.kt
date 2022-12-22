package de.timseidel.doppelkopf.util

import de.timseidel.doppelkopf.model.*

class DokoUtil {
    companion object {

        fun isWinner(pgr: PlayerGameResult): Boolean {
            return isWinner(pgr.playerAndFaction.faction, pgr.points)
        }

        fun isWinner(faction: Faction, points: Int): Boolean {
            return faction == Faction.RE && points > 120 || faction == Faction.CONTRA && points >= 120
        }

        fun getFactionTacken(faction: Faction, winningFaction: Faction, tacken: Int): Int {
            if (faction == Faction.NONE || winningFaction == Faction.NONE) return 0
            return if (faction == winningFaction) tacken else (-1) * tacken
        }

        fun getFactionPoints(faction: Faction, winningFaction: Faction, winningPoints: Int): Int {
            if (faction == Faction.NONE || winningFaction == Faction.NONE) return 0
            return if (faction == winningFaction) winningPoints else 240 - winningPoints
        }

        fun isPlayerInGame(player: Player, game: Game): Boolean {
            return game.players.any { paf -> paf.player.id == player.id }
        }

        fun getPlayerResult(player: Player, game: Game): PlayerGameResult {
            val paf = game.players.firstOrNull { paf -> paf.player.id == player.id }
            return if (paf == null) {
                PlayerGameResult(PlayerAndFaction(player, Faction.NONE), 0, 0)
            } else {
                PlayerGameResult(
                    paf,
                    getFactionTacken(paf.faction, game.winningFaction, game.tacken),
                    getFactionPoints(paf.faction, game.winningFaction, game.winningPoints)
                )
            }
        }

        //TODO Later calculate with SoloType
        fun isPlayerPlayingSolo(player: Player, game: Game): Boolean {
            val result = getPlayerResult(player, game)
            val reCount = game.players.count { p -> p.faction == Faction.RE }
            return result.playerAndFaction.faction != Faction.RE || reCount != 1
        }
    }
}