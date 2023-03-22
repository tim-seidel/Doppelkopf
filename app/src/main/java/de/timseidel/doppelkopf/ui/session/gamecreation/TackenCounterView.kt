package de.timseidel.doppelkopf.ui.session.gamecreation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import de.timseidel.doppelkopf.R

class TackenCounterView constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_tacken_counter, this)

        tvTackenCount = findViewById(R.id.tv_tacken_count)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.TackenCounterView)
        try {
            val counter = ta.getInteger(R.styleable.TackenCounterView_tackenCount, 0)
            setCounter(counter)
        } finally {
            ta.recycle()
        }
    }

    private lateinit var tvTackenCount: TextView

    fun setCounter(value: Int) {
        tvTackenCount.text = if (value in 0..9) "0$value" else value.toString()

        /*
        when {
            value < 0 -> tvTackenCount.setTextColor(ContextCompat.getColor(context, R.color.deep_purple))
            value ==  0 -> tvTackenCount.setTextColor(ContextCompat.getColor(context, R.color.black))
            else -> tvTackenCount.setTextColor(ContextCompat.getColor(context, R.color.teal_dark))
        }
         */
    }
}