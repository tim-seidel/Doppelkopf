package de.timseidel.doppelkopf.ui.group

class GroupCreationViewModel {

    var groupName = ""
    var memberNames = mutableListOf("")

    fun isValid(): Boolean {
        return groupName.length >= 3
    }

    fun getFilteredMemberNames(): List<String> {
        return memberNames.filter { name -> name.trim().isNotEmpty() }
    }
}