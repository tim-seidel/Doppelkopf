package de.timseidel.doppelkopf.model.statistic

import kotlin.math.round

data class SimpleStatisticEntry(
    var games: Int = 0,
    var tacken: Int = 0,
    var points: Int = 0
) {
    fun getTackenPerGame(): Float {
        return if(games > 0) round(tacken.toFloat() / games * 100) /100 else 0f
    }

    fun getPointsPerGame(): Float {
        return if(games > 0) round(points.toFloat() / games * 100) /100 else 0f
    }
}

data class StatisticEntry(
    val total: SimpleStatisticEntry = SimpleStatisticEntry(),
    val wins: SimpleStatisticEntry = SimpleStatisticEntry(),
    val loss: SimpleStatisticEntry = SimpleStatisticEntry()
)