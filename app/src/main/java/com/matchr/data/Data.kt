package com.matchr.data

import com.matchr.fragments.QuestionFragment
import com.matchr.fragments.ShortAnswerFragment
import paperparcel.PaperParcel

/**
 * Created by Allan Wang on 2017-10-20.
 */
enum class QuestionType(
        val fragmentFactory: () -> QuestionFragment
) {
    //    MULTIPLE_CHOICE, SINGLE_CHOICE, SHORT_ANSWER,
    LONG_ANSWER({ ShortAnswerFragment() });

    fun createFragment(question: IQuestion): QuestionFragment
            = fragmentFactory().withQuestion(question)
}

@PaperParcel
data class Question(override val type: QuestionType,
                    override val key: Int,
                    override val weight: Float,
                    override val isSkippable: Boolean,
                    override val viewData: List<String>) : IQuestion {
    companion object {
        @JvmField
        val CREATOR = PaperParcelQuestion.CREATOR
    }
}

data class Response(val key: Int, val data: List<String>)