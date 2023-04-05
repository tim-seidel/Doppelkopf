package de.timseidel.doppelkopf.model

data class Ranking(var title: String, val items: List<RankingItem>)

data class RankingItem(var name: String, var value: String)
