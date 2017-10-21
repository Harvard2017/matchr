package com.matchr

/**
 * Created by Allan Wang on 2017-10-20.
 */
enum class QuestionType {
    MULTIPLE_CHOICE, SINGLE_CHOICE, SHORT_ANSWER, LONG_ANSWER;
}

data class Response(val key: Int, val data: List<String>)