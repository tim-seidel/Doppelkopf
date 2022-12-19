package de.timseidel.doppelkopf.model

import java.time.LocalDateTime

class Group(
    val id: String,
    val date: LocalDateTime,
    var name: String,
    val players: MutableList<Player>,
    val sessions: MutableList<DokoSession>
)