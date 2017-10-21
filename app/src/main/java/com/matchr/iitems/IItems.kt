package com.matchr.iitems

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.utils.bindView
import com.matchr.R
import com.matchr.data.QuestionType
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter

/**
 * Created by Allan Wang on 2017-10-21.
 */
class QuestionItem(val text: String) : KauIItem<QuestionItem, QuestionItem.ViewHolder>(
        R.layout.iitem_question, { ViewHolder(it) }
) {

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        holder.text.text = text
    }

    override fun unbindView(holder: ViewHolder) {
        holder.text.text = null
        super.unbindView(holder)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView by bindView(R.id.question)
    }
}

open class ChoiceItem(
        val type: QuestionType,
        val text: String,
        val adapter: FastItemAdapter<ChoiceItem>,
        val position: Int
) : KauIItem<ChoiceItem, ChoiceItem.ViewHolder>(type.layoutRes, { ChoiceItem.ViewHolder(it) }) {

    private var selected: Boolean = position == 0 && type == QuestionType.SINGLE_CHOICE
    private var holder: ViewHolder? = null

    override fun isSelected() = selected

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        this.holder = holder;
        holder.button.text = text
        holder.button.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && type == QuestionType.SINGLE_CHOICE) {
                adapter.adapterItems.forEach { if (it != this) it.select(false) }
            }
            this.selected = isChecked
        }
    }

    fun select(selected: Boolean) {
        if (this.selected == selected) return
        this.selected = selected
        holder?.button?.isChecked = selected
    }

    override fun unbindView(holder: ViewHolder) {
        holder.button.text = null
        this.holder = null
        super.unbindView(holder)
    }

    fun toggle() {

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: CompoundButton by bindView(R.id.iitem_choice)
    }
}
