package com.matchr.data

import android.widget.TextView
import com.matchr.R
import com.matchr.iitems.ChoiceItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter

/**
 * Created by Allan Wang on 2017-10-20.
 */
enum class QuestionType(
        val layoutRes: Int
) {
    SINGLE_CHOICE(R.layout.iitem_choice_single),
    MULTIPLE_CHOICE(R.layout.iitem_choice_multi);

    fun updateAdapter(question: Question, title: TextView, adapter: FastItemAdapter<ChoiceItem>) {
        title.text = question.question
        adapter.set(question.options.mapIndexed { index, (text, _) -> ChoiceItem(this, text, adapter, index) })
    }

    companion object {
        val values = values()
        operator fun invoke(index: Int) = values[index]
    }
}

data class Response(val qOrdinal: Int, val data: List<String>)

data class Question(val id: Int, val question: String, val options: List<Pair<String, Int>>, val type: Int) {
    fun delegate() = QuestionType(type)
    fun nextId(selected: Set<Int>): Int
            = options[selected.firstOrNull() ?: 0].second
}

fun singleChoiceQuestion(id: Int, question: String, vararg options: Pair<String, Int>)
        = Question(id, question, options.asList(), QuestionType.SINGLE_CHOICE.ordinal)

fun multiChoiceQuestion(id: Int, question: String, nextId: Int, vararg options: String)
        = Question(id, question, options.map { it to nextId }, QuestionType.MULTIPLE_CHOICE.ordinal)