package de.timseidel.doppelkopf.contracts.statistic

interface ISessionStatisticsController {

    fun getPlayerStatisticsCalculator(): IPlayerStatisticsCalculator

    fun getSessionStatisticsCalculator(): ISessionStatisticsCalculator
}