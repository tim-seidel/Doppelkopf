package de.timseidel.doppelkopf.ui.group.creation

import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.ui.EditTextListener
import de.timseidel.doppelkopf.ui.util.Converter
import de.timseidel.doppelkopf.util.Logging

class GroupCreationMemberListAdapter(
    private val names: MutableList<String>,
    var memberClickListener: OnMemberClickListener? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnMemberClickListener {
        fun onAddMemberClicked()
    }

    class MemberViewHolder(
        private val view: EditText,
        private val textWatcher: PositionalTextWatcher
    ) : RecyclerView.ViewHolder(view) {
        init {
            view.addTextChangedListener(textWatcher)
        }

        fun bind(name: String, position: Int) {
            view.setText(name)
            textWatcher.position = position
        }
    }

    class AddMemberViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) return AddMemberViewHolder(createAddMemberView(parent.context))

        val et = EditText(parent.context)

        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        et.layoutParams = layoutParams
        et.hint = parent.context.resources.getString(R.string.player)
        et.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        et.minHeight = Converter.convertDpToPixels(48f, parent.context)
        et.isSingleLine = true
        et.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(parent.context, R.drawable.ic_baseline_person_outline_24),
            null,
            null,
            null
        )

        return MemberViewHolder(et, PositionalTextWatcher())
    }

    private fun createAddMemberView(context: Context): View {
        val btn = Button(context)
        btn.text = context.resources.getString(R.string.player)
        btn.setTextColor(ContextCompat.getColor(context, R.color.white))
        btn.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.neural))
        btn.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_add_white_24),
            null,
            null,
            null
        )
        btn.setOnClickListener {
            memberClickListener?.onAddMemberClicked()
        }

        return btn
    }

    override fun getItemCount(): Int {
        return names.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position != itemCount - 1) 0 else 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position != (itemCount - 1)) {
            (holder as MemberViewHolder).bind(names[position], position)
        }
    }

    fun addRow() {
        names.add("")
        notifyItemInserted(names.size - 1)
    }

    inner class PositionalTextWatcher(var position: Int = -1) : EditTextListener() {
        override fun afterTextChanged(s: Editable?) {
            if (position != -1) {
                names[position] = s.toString()
            }
        }
    }
}