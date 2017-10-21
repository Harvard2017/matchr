package com.matchr.data

import com.matchr.fragments.QuestionFragment
import com.matchr.fragments.ShortAnswerFragment

/**
 * Created by Allan Wang on 2017-10-20.
 */
enum class QuestionType(
        val fragmentFactory: () -> QuestionFragment
) {
    SINGLE_CHOICE({ ShortAnswerFragment() }),
    MULTIPLE_CHOICE({ ShortAnswerFragment() }),
    LIST({ ShortAnswerFragment() }),
    SHORT_ANSWER({ ShortAnswerFragment() }),
    LONG_ANSWER({ ShortAnswerFragment() });

    fun createFragment(question: IQuestion): QuestionFragment
            = fragmentFactory().withQuestion(question)
}

data class Response(val key: Int, val data: List<String>)