package de.timseidel.doppelkopf.util

import de.timseidel.doppelkopf.model.PlayerAndFaction

data class PlayerGameResult(val playerAndFaction: PlayerAndFaction, val tacken: Int, val points: Int)