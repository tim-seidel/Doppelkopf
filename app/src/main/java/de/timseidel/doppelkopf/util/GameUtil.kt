package de.timseidel.doppelkopf.util

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.GameHistoryColumn
import de.timseidel.doppelkopf.model.GameHistoryItem
import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.MemberAndFaction

class GameUtil {
    companion object {

        fun isFactionCompositionSolo(members: List<MemberAndFaction>): Boolean {
            val reCount = members.count { maf -> maf.faction == Faction.RE }
            val contraCount = members.count { maf -> maf.faction == Faction.CONTRA }

            return reCount == 1 && contraCount == 3
        }

        fun getFactionTacken(faction: Faction, game: Game): Int {
            if (faction == Faction.NONE || game.winningFaction == Faction.NONE) {
                return 0
            }

            val isWinner = faction == game.winningFaction
            val isSoloFaction = isGameTypeSoloType(game.gameType) && faction == Faction.RE

            var tacken = game.tacken
            if (!isWinner) {
                tacken *= -1
            }
            if (isSoloFaction) {
                tacken *= 3
            }

            return tacken
        }

        fun isGameTypeValid(
            gameType: GameType,
            factions: List<MemberAndFaction>
        ): Pair<Boolean, String> {
            val reCount = factions.count { paf -> paf.faction == Faction.RE }
            val contraCount = factions.count { paf -> paf.faction == Faction.CONTRA }

            when (gameType) {
                GameType.NORMAL -> {
                    return Pair(
                        reCount == 2 && contraCount == 2,
                        "Wähle für ein Normalspiel genau 2 Re und 2 Contra Spieler:innen aus."
                    )
                }

                GameType.HOCHZEIT -> {
                    return Pair(
                        reCount == 2 && contraCount == 2,
                        "Hochzeit: Genau 2x Re für die Hochzeitspartei und 2x Contra."
                    )
                }

                GameType.SCHWARZVERLOREN -> {
                    return Pair(
                        reCount == 1 && contraCount == 3,
                        "Schwarz verloren: 1x Re als Verliererpartei und 3x Contra."
                    )
                }

                GameType.SOLO -> {
                    return Pair(
                        reCount == 1 && contraCount == 3,
                        "Solo: 1x Re als Solopartei und 3x Contra."
                    )
                }

                else -> {
                    return Pair(false, "Ungültiger Spieltyp.")
                }
            }
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

        fun getMemberResult(member: Member, game: Game): GameResult {
            val maf = game.members.firstOrNull { maf -> maf.member.id == member.id }
            return if (maf == null) {
                GameResult("", Faction.NONE, false, 0, 0, false, GameType.NORMAL)
            } else {
                GameResult(
                    game.id,
                    maf.faction,
                    maf.faction == game.winningFaction,
                    getFactionTacken(maf.faction, game),
                    getFactionPoints(maf.faction, game.winningFaction, game.winningPoints),
                    game.isBockrunde,
                    game.gameType
                )
            }
        }

        fun getGameHistory(games: List<Game>): List<GameHistoryItem> {
            val gameHistory = mutableListOf<GameHistoryItem>()
            games.forEachIndexed { i, game ->
                val scores = mutableListOf<GameHistoryColumn>()

                game.members.forEach { paf ->
                    val result = getMemberResult(paf.member, game)
                    scores.add(
                        GameHistoryColumn(
                            result.faction,
                            result.tacken,
                            result.isWinner,
                            isMemberPlayingSolo(paf.member, game)
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

        fun isMemberPlayingSolo(member: Member, game: Game): Boolean {
            val result = getMemberResult(member, game)
            return game.gameType == GameType.SOLO && result.faction == Faction.RE
        }

        fun isMemberPlayingSoloType(member: Member, game: Game): Boolean {
            val result = getMemberResult(member, game)
            return isGameTypeSoloType(game.gameType) && result.faction == Faction.RE
        }

        fun isGameTypeSoloType(gameType: GameType): Boolean {
            return gameType == GameType.SOLO || gameType == GameType.SCHWARZVERLOREN
        }
    }
}