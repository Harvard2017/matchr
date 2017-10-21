package com.matchr.fragments

import com.matchr.R
import com.matchr.data.Response
import com.matchr.utils.L

/**
 * Created by Allan Wang on 2017-10-21.
 */
class ShortAnswerFragment : QuestionFragment() {
    override fun getResponse(): Response? {
        return null
    }

    override fun onError(flag: Int) {
    }

    override val layoutRes: Int = R.layout.question_short_answer

    override fun onShow() {
        L.d("Frag Onshow")
    }

}