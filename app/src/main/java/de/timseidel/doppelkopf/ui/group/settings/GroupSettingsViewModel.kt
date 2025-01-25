package de.timseidel.doppelkopf.ui.group.settings

import de.timseidel.doppelkopf.model.MemberSelection

class GroupSettingsViewModel {

    var isBockrundeEnabled = false
    var memberInputName = ""
    var memberActiveList = mutableListOf<MemberSelection>()
}