package de.timseidel.doppelkopf.export

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.util.GameUtil

class CSVGameHistoryExporter {

    fun exportGameHistory(games: List<Game>): String {
        if (games.isEmpty()) {
            return ""
        }

        val csv = StringBuilder()
        csv.append("gameId;timestamp")

        val players = games.first().players
        players.forEach { p ->
            csv.append(";${p.player.id}_tacken")
            csv.append(";${p.player.id}_faction")
        }
        csv.append(";winningFaction;gameType;soloType;isBockrunde\n")

        games.forEach { g ->
            csv.append("${g.id};")
            csv.append("${g.timestamp};")
            g.players.forEach { p ->
                val result = GameUtil.getPlayerResult(p.player, g)
                if (result.faction != Faction.NONE) {
                    csv.append(result.tacken.toString())
                } else {
                    csv.append("0")
                }

                csv.append(";${result.faction.name};")
            }

            csv.append(g.winningFaction.name)
            csv.append(";${g.gameType.name}")
            csv.append(";${g.soloType.name}")
            csv.append(";${g.isBockrunde}")
            csv.append("\n")
        }

        return csv.toString()
    }

    fun exportGameHistoryCumulative(games: List<Game>): String {
        if (games.isEmpty()) {
            return ""
        }

        val csv = StringBuilder()
        csv.append("gameId;timestamp")

        val players = games.first().players
        players.forEach { p ->
            csv.append(";${p.player.id}_tacken")
            csv.append(";${p.player.id}_faction")
        }
        csv.append(";winningFaction;gameType;soloType;isBockrunde\n")

        val tacken = MutableList(players.size) { 0 }

        games.forEach { g ->
            csv.append("${g.id};")
            csv.append("${g.timestamp};")
            g.players.forEachIndexed { i, p ->
                val result = GameUtil.getPlayerResult(p.player, g)
                tacken[i] += result.tacken
                if (result.faction != Faction.NONE) {
                    csv.append(tacken[i].toString())
                } else {
                    csv.append("0")
                }

                csv.append(";")
            }

            csv.append(g.winningFaction.name)
            csv.append(";${g.gameType.name}")
            csv.append(";${g.soloType.name}")
            csv.append(";${g.isBockrunde}")
            csv.append("\n")
        }

        return csv.toString()
    }
}