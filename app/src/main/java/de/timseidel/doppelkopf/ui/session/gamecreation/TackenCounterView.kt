package de.timseidel.doppelkopf.ui.session.gamecreation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import de.timseidel.doppelkopf.R

class TackenCounterView constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private lateinit var tvTackenCount: TextView

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_tacken_counter, this)
        findViews()

        applyAttributes(attrs)
    }

    private fun findViews() {
        tvTackenCount = findViewById(R.id.tv_tacken_count)
    }

    private fun applyAttributes(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TackenCounterView)
        try {
            val counter = ta.getInteger(R.styleable.TackenCounterView_tackenCount, 0)
            setCounter(counter)
        } finally {
            ta.recycle()
        }
    }

    fun setCounter(value: Int) {
        tvTackenCount.text = if (value in 0..9) {
            "0$value"
        } else {
            value.toString()
        }
    }
}