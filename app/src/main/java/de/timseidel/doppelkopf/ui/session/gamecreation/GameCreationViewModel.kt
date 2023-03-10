package de.timseidel.doppelkopf.ui.session.gamecreation

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.PlayerAndFaction

class GameCreationViewModel {

    var playerFactionList: List<PlayerAndFaction> = mutableListOf()
    var winningFaction: Faction = Faction.NONE
    var tackenCount: Int = 0
    var gameScore: Int = 0
    var isBockrunde: Boolean = false

    fun checkIsFactionAssignmentValid(): Boolean {
        if (playerFactionList.count() < 4) return false

        var reCount = 0;
        var contraCount = 0
        playerFactionList.forEach { pf ->
            when (pf.faction) {
                Faction.RE -> reCount += 1
                Faction.CONTRA -> contraCount += 1
                Faction.NONE -> {}
            }
        }

        //Only 2v2 or 1v3 (Solo) are valid
        if ((reCount == 2 && contraCount == 2) || (reCount == 1 && contraCount == 3)) return true

        return false
    }

    fun checkIsValid(): Boolean {
        if (winningFaction == Faction.NONE) return false
        if (gameScore < 0 || gameScore > 240) return false
        return checkIsFactionAssignmentValid()
    }

    fun reset() {
        winningFaction = Faction.NONE
        tackenCount = 0
        gameScore = 0
        isBockrunde = false

        playerFactionList.forEach { paf ->
            paf.faction = Faction.NONE
        }
    }

    override fun toString(): String {
        return "${winningFaction.name} | $tackenCount | $gameScore | $playerFactionList | $isBockrunde"
    }

}