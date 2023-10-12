package de.timseidel.doppelkopf.ui.session.gamecreation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import de.timseidel.doppelkopf.R

class TackenCounterView constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private var tackenCount = 0

    interface TackenChangeListener {
        fun onTackenChanged(count: Int)
    }

    private lateinit var tvTackenCount: TextView
    private lateinit var ibDecreaseTacken: ImageButton
    private lateinit var ibIncreaseTacken: ImageButton

    private var tackenChangeListener: TackenChangeListener? = null

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_tacken_counter, this)
        findViews()
        setupButtons()

        applyAttributes(attrs)
    }

    private fun findViews() {
        tvTackenCount = findViewById(R.id.tv_tacken_count)
        ibDecreaseTacken = findViewById(R.id.ib_decrease_tacken)
        ibIncreaseTacken = findViewById(R.id.ib_increase_tacken)
    }

    private fun setupButtons() {
        ibDecreaseTacken.setOnClickListener {
            tackenCount -= 1
            update()

            this.tackenChangeListener?.onTackenChanged(tackenCount)
        }

        ibIncreaseTacken.setOnClickListener {
            tackenCount += 1
            update()

            this.tackenChangeListener?.onTackenChanged(tackenCount)
        }
    }

    private fun applyAttributes(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TackenCounterView)
        try {
            val counter = ta.getInteger(R.styleable.TackenCounterView_tackenCount, 0)
            setTackenCount(counter)
        } finally {
            ta.recycle()
        }
    }

    fun setTackenChangeListener(listener: TackenChangeListener) {
        this.tackenChangeListener = listener
    }

    private fun update() {
        tvTackenCount.text = if (tackenCount in 0..9) {
            "0$tackenCount"
        } else {
            tackenCount.toString()
        }
    }

    fun getTackenCount(): Int {
        return tackenCount
    }

    fun setTackenCount(value: Int) {
        tackenCount = value
        update()
    }
}