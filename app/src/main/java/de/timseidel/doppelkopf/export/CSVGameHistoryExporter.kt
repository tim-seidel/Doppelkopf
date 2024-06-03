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

        val members = games.first().members
        members.forEach { p ->
            csv.append(p.member.name)
            csv.append(";")
        }
        csv.append("isBockrunde;winningFaction;gameType\n")

        games.forEach { g ->
            g.members.forEach { p ->
                val result = GameUtil.getMemberResult(p.member, g)
                if (result.faction != Faction.NONE) {
                    csv.append(result.tacken.toString())
                }
                csv.append(";")
            }

            if (g.isBockrunde) {
                csv.append("x")
            }
            csv.append(";${g.winningFaction.name}")
            csv.append(";${g.gameType.name}")
            csv.append("\n")
        }

        return csv.toString()
    }

    fun exportGameHistoryCumulative(games: List<Game>): String {
        if (games.isEmpty()) {
            return ""
        }

        val csv = StringBuilder()

        val members = games.first().members
        members.forEach { p ->
            csv.append(p.member.name)
            csv.append(";")
        }
        csv.append("isBockrunde;winningFaction;gameType\n")

        val tacken = MutableList(members.size) { 0 }

        games.forEach { g ->
            g.members.forEachIndexed { i, p ->
                val result = GameUtil.getMemberResult(p.member, g)
                tacken[i] += result.tacken
                if (result.faction != Faction.NONE) {
                    csv.append(tacken[i].toString())
                }

                csv.append(";")
            }

            if (g.isBockrunde) {
                csv.append("x")
            }
            csv.append(";${g.winningFaction.name}")
            csv.append(";${g.gameType.name}")
            csv.append("\n")
        }

        return csv.toString()
    }
}