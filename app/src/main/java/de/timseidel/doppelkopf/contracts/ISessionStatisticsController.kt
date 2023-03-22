package de.timseidel.doppelkopf.contracts

interface ISessionStatisticsController {

    fun getPlayerStatisticsCalculator(): IPlayerStatisticsCalculator

    fun getSessionStatisticsCalculator(): ISessionStatisticsCalculator
}