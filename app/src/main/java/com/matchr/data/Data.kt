package com.matchr.data

import com.matchr.fragments.QuestionFragment

/**
 * Created by Allan Wang on 2017-10-20.
 */
enum class QuestionType {
    MULTIPLE_CHOICE, SINGLE_CHOICE, SHORT_ANSWER, LONG_ANSWER;

    fun createFragment(question: IQuestion): QuestionFragment = object : QuestionFragment() {
        override val layoutRes: Int
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val question: IQuestion
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

        override fun onShow() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}

data class Response(val key: Int, val data: List<String>)