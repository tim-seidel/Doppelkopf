package de.timseidel.doppelkopf.ui.group.statistic

import android.view.View
import android.widget.TextView

enum class StatisticLoadingState {
    IDLE,
    LOADING_SESSIONS,
    CALCULATING,
    ERROR
}

class StatisticLoadingOverlayController(
    private val overlay: View,
    private val progress: View,
    private val messageView: TextView,
    private val loadingMessage: CharSequence,
    private val calculatingMessage: CharSequence,
    private val defaultErrorMessage: CharSequence,
    private val busyViews: List<View> = emptyList()
) {
    var state: StatisticLoadingState = StatisticLoadingState.IDLE
        private set

    fun isBusy(): Boolean {
        return state == StatisticLoadingState.LOADING_SESSIONS ||
            state == StatisticLoadingState.CALCULATING
    }

    fun render(newState: StatisticLoadingState, errorMessage: CharSequence? = null) {
        state = newState

        val isBusy = isBusy()
        busyViews.forEach { it.isEnabled = !isBusy }

        when (newState) {
            StatisticLoadingState.IDLE -> {
                overlay.visibility = View.GONE
                progress.visibility = View.GONE
            }

            StatisticLoadingState.LOADING_SESSIONS -> {
                overlay.visibility = View.VISIBLE
                progress.visibility = View.VISIBLE
                messageView.text = loadingMessage
            }

            StatisticLoadingState.CALCULATING -> {
                overlay.visibility = View.VISIBLE
                progress.visibility = View.VISIBLE
                messageView.text = calculatingMessage
            }

            StatisticLoadingState.ERROR -> {
                overlay.visibility = View.VISIBLE
                progress.visibility = View.GONE
                messageView.text = errorMessage ?: defaultErrorMessage
            }
        }
    }
}
