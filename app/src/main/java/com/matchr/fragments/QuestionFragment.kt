package com.matchr.fragments

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import butterknife.ButterKnife
import butterknife.Unbinder
import ca.allanwang.kau.utils.rndColor
import com.matchr.data.IQuestion
import com.matchr.data.IQuestionContainer
import com.matchr.data.Matchr
import com.matchr.utils.L


/**
 * Created by Allan Wang on 2017-10-21.
 */
abstract class QuestionFragment : Fragment(), IQuestionContainer {
    abstract protected val layoutRes: Int
    private var isShown: Boolean = false
    private lateinit var unbinder: Unbinder
    val question: IQuestion by lazy { Matchr.questionFromOrdinal(arguments!!.getInt(ARG_QUESTION)) }

    companion object {
        private const val ARG_QUESTION = "arg_question"
    }

    private fun addToBundle(action: (Bundle) -> Unit): QuestionFragment {
        arguments = (arguments ?: Bundle()).apply { action(this) }
        return this
    }

    fun withQuestion(question: IQuestion)
            = addToBundle { it.putInt(ARG_QUESTION, question.ordinal) }

    override final fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutRes, container, false)
        unbinder = ButterKnife.bind(this, view)
        L.d("Frag create ${hashCode()} ${question.name}")
        view.setBackgroundColor(rndColor)
        return view
    }

    @CallSuper
    open fun onPageScrolled(offset: Float) {
        if (offset == 0f && !isShown) {
            onShow()
            isShown = true
        }
    }

    fun onPageSelected() {
        if (view != null) onPageSelectedImpl()
    }

    protected open fun onPageSelectedImpl() {

    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (nextAnim <= 0) return super.onCreateAnimation(transit, enter, nextAnim)

        val anim = AnimationUtils.loadAnimation(activity, nextAnim)

        anim.setAnimationListener(object : AnimationListener {

            override fun onAnimationStart(animation: Animation) {
                L.d("Frag start ${hashCode()}")
            }

            override fun onAnimationRepeat(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                L.d("Frag end ${hashCode()}")
                if (!isShown) {
                    isShown = true
                    onShow()
                }
            }
        })

        return anim
    }

    abstract fun onShow()

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
        isShown = false
    }
}