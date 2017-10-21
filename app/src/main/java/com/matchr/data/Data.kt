package com.matchr.data

import ca.allanwang.kau.ui.widgets.TextSlider
import com.matchr.R
import com.matchr.iitems.ChoiceItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter

/**
 * Created by Allan Wang on 2017-10-20.
 */
enum class QuestionType(
        val layoutRes: Int, private val withMultiSelect: Boolean
) {
    SINGLE_CHOICE(R.layout.iitem_choice_single, false),
    MULTIPLE_CHOICE(R.layout.iitem_choice_multi, true);

    fun updateAdapter(question: Question, title: TextSlider, adapter: FastItemAdapter<ChoiceItem>) {
        title.setText(question.question)
        adapter.set(question.options.map { ChoiceItem(this, it.first) })
        adapter.withMultiSelect(withMultiSelect)
    }

    companion object {
        val values = values()
        operator fun invoke(index: Int) = values[index]
    }
}

data class Response(val qOrdinal: Int, val data: List<String>)

data class Question(val id: Int, val question: String, val options: List<Pair<String, Int>>, val type: Int) {
    fun delegate() = QuestionType(type)
}