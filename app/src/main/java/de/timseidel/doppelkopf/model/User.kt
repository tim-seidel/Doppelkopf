package de.timseidel.doppelkopf.model

import de.timseidel.doppelkopf.util.IdGenerator

class User(
    val id: String = IdGenerator.generateIdWithTimestamp("user"),
    val groupIds: List<String> //TODO: Maybe schon Gruppenobjekte (ohne 'deep session')
)