package com.matchr.activities

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.rndColor
import com.matchr.R
import com.matchr.data.IQuestion
import com.matchr.data.Matchr
import com.matchr.fragments.QuestionFragment

/**
 * Created by Allan Wang on 2017-10-21.
 */
class QuestionActivity : AppCompatActivity() {

    val container: ViewGroup by bindView(R.id.question_container)
    val fab: FloatingActionButton by bindView(R.id.fab)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)
        proceed(Matchr.start)
        fab.setOnClickListener {
            val questionFragment = (supportFragmentManager.findFragmentById(R.id.question_container) as QuestionFragment)
            questionFragment.getResponse()
            val nextQuestion = Matchr.onResponse(questionFragment.question, questionFragment.getResponse())
            if (nextQuestion != null)
                proceed(nextQuestion)
        }
    }

    fun proceed(question: IQuestion) {
        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.kau_slide_in_right, R.anim.kau_slide_out_left).replace(R.id.question_container, question.createFragment()).commit()
    }

}