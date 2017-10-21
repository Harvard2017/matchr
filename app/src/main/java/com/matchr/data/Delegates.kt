package com.matchr.data

/**
 * Created by Allan Wang on 2017-10-21.
 */
open class QuestionFlowDelegate(override val start: IQuestion) : IQuestionFlow {

    private val mapper: MutableMap<IQuestion, (response: Response?) -> IQuestion> = mutableMapOf()
    private val validator: MutableMap<IQuestion, (response: Response) -> Boolean> = mutableMapOf()

    fun withSequence(vararg questions: IQuestion) {
        if (questions.size < 2) return
        var prev: IQuestion? = null
        questions.forEach {
            if (prev != null)
                mapper.put(prev!!, { _ -> it })
            prev = it
        }
    }

    fun withConditional(question: IQuestion, mapper: (response: Response?) -> IQuestion) {
        this.mapper.put(question, mapper)
    }

    fun withValidator(question: IQuestion, validator: (response: Response) -> Boolean) {
        this.validator.put(question, validator)
    }

    override final fun onResponse(question: IQuestion, response: Response?): IQuestion?
            = mapper[question]?.invoke(response)

    override final fun validateResponse(question: IQuestion, response: Response): Boolean
            = validator[question]?.invoke(response) ?: true
}