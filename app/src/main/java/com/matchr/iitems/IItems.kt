package com.matchr.iitems

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.TextView
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.utils.bindView
import com.matchr.R
import com.matchr.data.QuestionType

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
        val type:QuestionType,
        val text: String
) : KauIItem<ChoiceItem, ChoiceItem.ViewHolder>(type.layoutRes, { ChoiceItem.ViewHolder(it) }) {
    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        holder.button.text = text
        holder.button.isChecked = isSelected
    }

    override fun unbindView(holder: ViewHolder) {
        holder.button.text = null
        super.unbindView(holder)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: CompoundButton by bindView(R.id.iitem_choice)
    }
}
