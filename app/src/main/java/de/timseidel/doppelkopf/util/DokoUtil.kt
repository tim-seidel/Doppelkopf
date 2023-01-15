package de.timseidel.doppelkopf.util

import de.timseidel.doppelkopf.controller.DoppelkopfManager
import de.timseidel.doppelkopf.model.*

class DokoUtil {
    companion object {

        fun isWinner(faction: Faction, points: Int): Boolean {
            return faction == Faction.RE && points > 120 || faction == Faction.CONTRA && points >= 120
        }

        fun isFactionCompositionSolo(players: List<PlayerAndFaction>) : Boolean{
            val reCount = players.count { paf -> paf.faction == Faction.RE }
            val contraCount = players.count { paf -> paf.faction == Faction.CONTRA }

            return reCount == 1 && contraCount == 3
        }

        fun isSoloPlayer(player: Player, game: Game): Boolean {
            if (game.gameType != GameType.SOLO) return false
            return (game.players.firstOrNull { p -> p.player.id == player.id }?.faction
                ?: Faction.NONE) == Faction.RE
        }

        fun getFactionTacken(faction: Faction, game: Game): Int {
            if (faction == Faction.NONE || game.winningFaction == Faction.NONE) return 0

            val isWinner = faction == game.winningFaction
            val isSoloFaction = game.gameType == GameType.SOLO && faction == Faction.RE

            var tacken = game.tacken
            if (!isWinner) tacken *= -1
            if (isSoloFaction) tacken *= 3

            return tacken
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
                PlayerGameResult(Faction.NONE, false, 0, 0, false)
            } else {
                PlayerGameResult(
                    paf.faction,
                    paf.faction == game.winningFaction,
                    getFactionTacken(paf.faction, game),
                    getFactionPoints(paf.faction, game.winningFaction, game.winningPoints),
                    game.isBockrunde
                )
            }
        }

        fun isPlayerPlayingSolo(player: Player, game: Game): Boolean {
            val result = getPlayerResult(player, game)
            return game.gameType == GameType.SOLO && result.faction == Faction.RE
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
                (0..9).random() == 0,
                GameType.NORMAL
            )
    }
}