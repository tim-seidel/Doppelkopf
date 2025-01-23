package de.timseidel.doppelkopf.ui.session.gamecreation

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.MemberAndFaction
import de.timseidel.doppelkopf.util.GameUtil

class GameConfiguration {

    var memberFactionList: List<MemberAndFaction> = mutableListOf()
    var winningFaction: Faction = Faction.NONE
    var tackenCount: Int = 0
    var isBockrunde: Boolean = false
    var gameType: GameType = GameType.NORMAL

    private fun isFactionAssignmentValid(): Boolean {
        if (memberFactionList.count() < 4) {
            return false
        }

        var reCount = 0
        var contraCount = 0
        memberFactionList.forEach { pf ->
            when (pf.faction) {
                Faction.RE -> reCount += 1
                Faction.CONTRA -> contraCount += 1
                Faction.NONE -> {}
            }
        }

        return (reCount == 2 && contraCount == 2) || (reCount == 1 && contraCount == 3)
    }

    fun isValid(): Boolean {
        if (winningFaction == Faction.NONE) {
            return false
        }
        if (!isFactionAssignmentValid()) {
            return false
        }

        return GameUtil.isGameTypeValid(gameType, memberFactionList).first
    }


    fun reset() {
        winningFaction = Faction.NONE
        tackenCount = 0
        isBockrunde = false
        gameType = GameType.NORMAL

        memberFactionList.forEach { paf ->
            paf.faction = Faction.NONE
        }
    }

    override fun toString(): String {
        return "${winningFaction.name} | $tackenCount | $memberFactionList | $isBockrunde"
    }

}