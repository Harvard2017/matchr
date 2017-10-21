package com.matchr.data

import com.matchr.data.QuestionType.*

/**
 * Created by Allan Wang on 2017-10-21.
 *
 * This is the only file that needs to be touched to properly build
 * a matchr app. Everything else is abstracted & extensible
 */

/**
 * Define the questions
 * The best use case is through enums as the questions are set in advanced
 * and the implementation is by singletons.
 * [IQuestion] is reused quite often and we should avoid constructing duplicates
 */
enum class Question(override val type: QuestionType,
                    override val weight: Float,
                    override val isSkippable: Boolean,
                    vararg viewData: String) : IQuestion {

    ROLE(MULTIPLE_CHOICE, 1f, false,
            "Are you a student or a tutor?",
            "Student",
            "Tutor"),

    CLASSES(LIST, 1f, false,
            "What classes are you in?"),

    SCHEDULE(LIST, 5f, false,
            "When are you free?"),

    EMAIL(SHORT_ANSWER, 0f, false,
            "What is your email?");

    override val viewData: List<String> = viewData.toList()

    companion object {
        val values = values()
        operator fun get(index: Int) = values[index]
    }
}

/**
 * Define the question flow
 */
class QuestionFlow : QuestionFlowDelegate(Question.ROLE) {
    init {
        withSequence(*Question.values)
        withValidator(Question.EMAIL) { it.data[0].contains("@") }
    }
}

/**
 * Define the interaction logic
 */
object Matchr : IMatchr<Question>, IQuestionFlow by QuestionFlow() {

    override fun questionFromOrdinal(ordinal: Int): Question = Question[ordinal]

    override fun fetch(userId: String, callback: (matches: List<Pair<String, Float>>) -> Unit) {
        //handled by backend?
    }

    override val responseMapper: List<Pair<Question, Question>>
            = Question.values.map { it to it } //in this example, we are directly mapping question numbers to the same question numbers

    override fun matchResponse(key: Question, response: Response, otherKey: Question, otherResponse: Response?): Float {
        //backend?
        return 1f
    }


}

