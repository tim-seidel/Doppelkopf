package de.timseidel.doppelkopf.model

data class Ranking(var title: String, var description: String, val items: List<RankingItem>)

data class RankingItem(var name: String, var value: String)
