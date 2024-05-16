package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.ISettingsController
import de.timseidel.doppelkopf.model.GroupSettings

class SettingsController : ISettingsController {

    private var settings: GroupSettings = GroupSettings()

    override fun getSettings(): GroupSettings {
        return settings
    }

    override fun set(settings: GroupSettings) {
        this.settings = settings
    }

    override fun reset() {
        settings = GroupSettings()
    }
}