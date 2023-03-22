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

        val players = games.first().players
        players.forEach { p ->
            csv.append(p.player.name)
            csv.append(";")
        }
        csv.append("\n")

        games.forEach { g ->
            g.players.forEach { p ->
                val result = GameUtil.getPlayerResult(p.player, g)
                if (result.faction != Faction.NONE) {
                    csv.append(result.tacken.toString())
                }
                csv.append(";")
            }

            if (g.isBockrunde) csv.append("x")
            csv.append("\n")
        }

        return csv.toString()
    }

    fun exportGameHistoryCumulative(games: List<Game>): String {
        if (games.isEmpty()) {
            return ""
        }

        val csv = StringBuilder()

        val players = games.first().players
        players.forEach { p ->
            csv.append(p.player.name)
            csv.append(";")
        }
        csv.append("\n")

        val tacken = MutableList(players.size) { 0 }

        games.forEach { g ->
            g.players.forEachIndexed { i, p ->
                val result = GameUtil.getPlayerResult(p.player, g)
                tacken[i] += result.tacken
                if (result.faction != Faction.NONE) {
                    csv.append(tacken[i].toString())
                }

                csv.append(";")
            }

            if (g.isBockrunde) csv.append("x")
            csv.append("\n")
        }

        return csv.toString()
    }
}