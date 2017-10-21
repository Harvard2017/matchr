package com.matchr

import android.view.ViewGroup

/**
 * Created by Allan Wang on 2017-10-20.
 */
interface IQuestionView {
    fun onTransition(progress: Float)
    fun onResponse(): List<String>
}

interface IQuestion {
    val type: QuestionType
    val key: Int
    val weight: Float
    val isSkippable: Boolean
    fun getViewData(): List<String>
    fun bindToView(parent: ViewGroup): IQuestionView
    fun onResponse(response: Response): IQuestion
}

interface Matcher {
    fun match(response: Response, otherResponse: Response): Float
    fun onMatchless(response: Response): Float
}