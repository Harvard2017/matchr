package com.matchr.data

/**
 * Created by Allan Wang on 2017-10-20.
 */
interface IQuestionContainer {
    /**
     * When triggered, collect the response list
     * from the view
     */
    fun getResponse(): Response?

    /**
     * When triggered, notify the user of an error
     */
    fun onError(flag: Int)
}

interface IQuestionFlow {

    val start: IQuestion
    /**
     * Controls the question flow on a received response
     * [response] is null if question is skipped
     */
    fun onResponse(question: IQuestion, response: Response?): IQuestion?

    /**
     * Check if a given response is valid
     * Note that the response it not null, as questions
     * are already defined as skippable or not
     *
     * Returns [true] for a valid response, and [false] otherwise
     */
    fun validateResponse(question: IQuestion, response: Response): Boolean
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
    val ordinal: Int
    val name: String
    val weight: Float
    val isSkippable: Boolean
    val viewData: List<String>
    fun createFragment() = type.createFragment(this)
}

/**
 * Global handler to compare data sets
 * Implementation allows the option to handle data through enums
 */
interface IMatchr<Q : Enum<*>> : IQuestionFlow {

    fun questionFromOrdinal(ordinal: Int): Q

    /**
     * Given a userid, fetch results through a callback
     * Results are mapped as userid to match score
     */
    fun fetch(userId: String, callback: (matches: List<Pair<String, Float>>) -> Unit)

    /**
     * Get key of a response to be mapped
     */
    fun map(response: Response) = questionFromOrdinal(response.qOrdinal) to response

    /**
     * Matches the corresponding keys
     * For instance, a question asking for time schedules should map the same types
     * A question pair such as "describe yourself" and "what do you look for in a person?" should map to different types
     *
     * The intent is to map key codes together, though the base implementation
     * is a list to allow for multiple matches for the same key
     */
    val responseMapper: List<Pair<Q, Q>>

    /**
     * Match a defined pair of responses
     * Note that the other response is nullable
     */
    fun matchResponse(key: Q, response: Response, otherKey: Q, otherResponse: Response?): Float
}