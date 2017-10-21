package com.matchr.data

import android.view.ViewGroup
import paperparcel.PaperParcelable

/**
 * Created by Allan Wang on 2017-10-20.
 */
interface IQuestionView {
    fun onTransition(progress: Float)
    fun onResponse(): List<String>
    fun bindToView(parent: ViewGroup): IQuestionView
}

interface IQuestionFlow {
    fun onResponse(question: IQuestion, response: Response, skipped: Boolean): IQuestion
}

/**
 * A single question that handles view generation
 * and propagation
 */
interface IQuestion : PaperParcelable {
    /**
     * Type, which helps define how the data is handled
     */
    val type: QuestionType
    /**
     * Strictly unique
     */
    val key: Int
    val weight: Float
    val isSkippable: Boolean
    val viewData: List<String>
}

/**
 * Global handler to compare data sets
 * Implementation allows the option to handle data through enums
 */
interface IMatchr<K> {

    /**
     * Get key of a response to be mapped
     */
    fun map(response: Response): Pair<K, Response>

    /**
     * Matches the corresponding keys
     * For instance, a question asking for time schedules should map the same types
     * A question pair such as "describe yourself" and "what do you look for in a person?" should map to different types
     *
     * The intent is to map key codes together, though the base implementation
     * is a list to allow for multiple matches for the same key
     */
    val responseMapper: List<Pair<K, K>>

    /**
     * Match a defined pair of responses
     * Note that the other response is nullable
     */
    fun matchResponse(key: K, response: Response, otherKey: K, otherResponse: Response?): Float
}