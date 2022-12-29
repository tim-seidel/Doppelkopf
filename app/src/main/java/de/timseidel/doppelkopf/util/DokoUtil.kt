package de.timseidel.doppelkopf.util

import de.timseidel.doppelkopf.controller.DoppelkopfManager
import de.timseidel.doppelkopf.model.*

class DokoUtil {
    companion object {

        //TODO: Points sind im Moment nicht benutzt
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
                PlayerGameResult(Faction.NONE, false, 0, 0)
            } else {
                PlayerGameResult(
                    paf.faction,
                    //isWinner(paf.faction, getFactionPoints(paf.faction, game.winningFaction, game.winningPoints)),
                    paf.faction == game.winningFaction,
                    getFactionTacken(paf.faction, game.winningFaction, game.tacken),
                    getFactionPoints(paf.faction, game.winningFaction, game.winningPoints)
                )
            }
        }

        //TODO Later calculate with SoloType
        fun isPlayerPlayingSolo(player: Player, game: Game): Boolean {
            val result = getPlayerResult(player, game)
            val reCount = game.players.count { p -> p.faction == Faction.RE }
            return result.faction != Faction.RE || reCount != 1
        }
    }

    private fun createSampleGames() {
        for (i in (1..40)) {
            DoppelkopfManager.getInstance().getSessionController().getGameController()
                .addGame(getSampleGame())
        }
    }

    private fun getSampleGame(): Game {
        val pafs = DoppelkopfManager.getInstance().getSessionController().getPlayerController()
            .getPlayersAsFaction()
        val reContra = listOf(Faction.RE, Faction.RE, Faction.CONTRA, Faction.CONTRA).shuffled()
        for (i in 0..3) pafs[i].faction = reContra[i]

        return DoppelkopfManager.getInstance().getSessionController().getGameController()
            .createGame(
                pafs,
                if ((0..100).random() <= 60) Faction.RE else Faction.CONTRA,
                (121..240).random(),
                (-1..8).random(),
                (0..9).random() == 0
            )
    }
}