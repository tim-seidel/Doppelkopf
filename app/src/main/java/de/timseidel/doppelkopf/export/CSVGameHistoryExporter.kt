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

        val members = games.first().members
        members.forEach { m ->
            csv.append(";${m.member.id}_tacken")
            csv.append(";${m.member.id}_faction")
        }

        csv.append(";tacken;winningFaction;gameType;soloType;isBockrunde\n")

        games.forEach { g ->
            csv.append("${g.id};")
            csv.append("${g.timestamp};")

            g.members.forEach { p ->
                val result = GameUtil.getMemberResult(p.member, g)
                if (result.faction != Faction.NONE) {
                    csv.append(result.tacken.toString())
                } else {
                    csv.append("0")
                }

                csv.append(";${result.faction.name};")
            }

            csv.append(g.tacken)
            csv.append(";${g.winningFaction.name}")
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

        val members = games.first().members
        members.forEach { m ->
            csv.append(";${m.member.id}_tacken")
            csv.append(";${m.member.id}_faction")
        }
        csv.append(";winningFaction;gameType;soloType;isBockrunde\n")

        val tacken = MutableList(members.size) { 0 }

        games.forEach { g ->
            csv.append("${g.id};")
            csv.append("${g.timestamp};")

            g.members.forEachIndexed { i, p ->
                val result = GameUtil.getMemberResult(p.member, g)
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