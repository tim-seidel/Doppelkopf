package de.timseidel.doppelkopf.ui.group.finances

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FinancesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Chronisch pleite"
    }
    val text: LiveData<String> = _text
}