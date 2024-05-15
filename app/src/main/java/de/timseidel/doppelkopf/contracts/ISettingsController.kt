package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.GroupSettings

interface ISettingsController {

    fun getSettings(): GroupSettings

    fun set(settings: GroupSettings)

    fun reset()
}