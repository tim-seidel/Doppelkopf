package de.timseidel.doppelkopf.ui.session.gamecreation

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.PlayerAndFaction

class GameConfiguration {

    var playerFactionList: List<PlayerAndFaction> = mutableListOf()
    var winningFaction: Faction = Faction.NONE
    var tackenCount: Int = 0
    var isBockrunde: Boolean = false
    var gameType: GameType = GameType.NORMAL

    fun isFactionAssignmentValid(): Boolean {
        if (playerFactionList.count() < 4) {
            return false
        }

        var reCount = 0
        var contraCount = 0
        playerFactionList.forEach { pf ->
            when (pf.faction) {
                Faction.RE -> reCount += 1
                Faction.CONTRA -> contraCount += 1
                Faction.NONE -> {}
            }
        }

        if ((reCount == 2 && contraCount == 2) || (reCount == 1 && contraCount == 3)) {
            return true
        }

        return false
    }

    fun isValid(): Boolean {
        if (winningFaction == Faction.NONE) {
            return false
        }
        return isFactionAssignmentValid()
    }

    fun reset() {
        winningFaction = Faction.NONE
        tackenCount = 0
        isBockrunde = false

        playerFactionList.forEach { paf ->
            paf.faction = Faction.NONE
        }
    }

    override fun toString(): String {
        return "${winningFaction.name} | $tackenCount | $playerFactionList | $isBockrunde"
    }

}