package de.timseidel.doppelkopf.ui.session.creation

import de.timseidel.doppelkopf.model.MemberSelection

class SessionCreationViewModel {

    var sessionName = "Doppelkopfabend"
    var memberInputName = ""
    var memberSelections = mutableListOf<MemberSelection>()

    fun checkIsSetupValid(): Boolean {
        if (sessionName.length < 3) {
            return false
        }

        var memberCount = 0
        memberSelections.forEach { ms ->
            if (ms.isSelected) {
                memberCount += 1
            }
        }

        if (memberCount < 4) {
            return false
        }

        val names = memberSelections.filter { ms -> ms.isSelected }.map { ms -> ms.member.name }
        return names.distinct().count() == names.count()
    }

    fun reset() {
        sessionName = ""
        memberInputName = ""
        memberSelections.forEach { ms ->
            ms.isSelected = false
        }
    }
}