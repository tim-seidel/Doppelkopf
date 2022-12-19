package de.timseidel.doppelkopf.model.statistic

data class SimpleStatisticEntry(
    var games: Int = 0,
    var tacken: Int = 0,
    var points: Int = 0
){
    fun getTackenPerGame(): Float {
        return tacken.toFloat() / games
    }

    fun getPointsPerGame(): Float {
        return points.toFloat() / games
    }
}

data class StatisticEntry(
    val total: SimpleStatisticEntry = SimpleStatisticEntry(),
    val wins: SimpleStatisticEntry = SimpleStatisticEntry(),
    val loss: SimpleStatisticEntry = SimpleStatisticEntry()
)