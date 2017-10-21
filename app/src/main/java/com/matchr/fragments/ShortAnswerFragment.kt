package com.matchr.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import butterknife.BindView
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.scaleXY
import com.matchr.R
import com.matchr.utils.L

/**
 * Created by Allan Wang on 2017-10-21.
 */
class ShortAnswerFragment : QuestionFragment() {

    @BindView(R.id.question_short_answer)
    lateinit var editText: EditText

    override fun getResponseData(): List<String>?
            = listOf(editText.text.toString())

    override fun onError(flag: Int) {
    }

    override val layoutRes: Int = R.layout.question_short_answer

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editText.alpha = 0f
        editText.translationY = 20f.dpToPx
        editText.scaleXY = 0.7f
        editText.animate().alpha(1f).translationY(0f).scaleXY(1f).setDuration(1000L).setStartDelay(100L)
    }

}