package de.timseidel.doppelkopf.ui.group.statistic

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import de.timseidel.doppelkopf.R


class RankingItemView(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    init {
        init(attrs)
    }

    private lateinit var ivCup: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvValue: TextView

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_ranking_item, this)
        findViews()

        val ta = context.obtainStyledAttributes(attrs, R.styleable.RankingItemView)
        try {
            setName(ta.getString(R.styleable.RankingItemView_rankingName) ?: "Name")
            setValue(ta.getString(R.styleable.RankingItemView_rankingValue) ?: "0")
            setIsCupEnabled(ta.getBoolean(R.styleable.RankingItemView_rankingCupEnabled, false))
        } finally {
            ta.recycle()
        }
    }

    private fun findViews(){
        ivCup = findViewById(R.id.iv_ranking_cup_icon)
        tvName = findViewById(R.id.tv_ranking_item_name)
        tvValue = findViewById(R.id.tv_ranking_item_value)
    }

    fun setName(name: String) {
        tvName.text = name
    }

    fun setValue(value: String) {
        tvValue.text = value
    }

    fun setIsCupEnabled(enabled: Boolean) {
        ivCup.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    fun setBackGroundColor(color: Int) {
        setBackgroundColor(color)
    }
}