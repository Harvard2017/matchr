package com.matchr

import android.view.ViewGroup

/**
 * Created by Allan Wang on 2017-10-20.
 */
interface IQuestionView {
    fun onTransition(progress: Float)
    fun onResponse(): List<String>
}

/**
 * A single question that handles view generation
 * and propagation
 */
interface IQuestion {
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
    fun getViewData(): List<String>
    fun bindToView(parent: ViewGroup): IQuestionView
    fun onResponse(response: Response): IQuestion
}

/**
 * Global handler to compare data sets
 * Implementation allows the option to handle data through enums
 */
interface Matcher<K> {

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
     * Match two complete data sets
     * This should be handled by a delegate
     */
    fun matchAllResponses(responses: Map<K, Response>, otherResponses: Map<K, Response>): Float

    /**
     * Match a defined pair of responses
     * Note that the other response is nullable
     */
    fun matchResponse(key: K, response: Response, otherKey: K, otherResponse: Response?): Float
}