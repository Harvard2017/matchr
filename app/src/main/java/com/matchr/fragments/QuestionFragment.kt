package com.matchr.fragments

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import ca.allanwang.kau.utils.rndColor
import com.matchr.R
import com.matchr.data.IQuestion
import com.matchr.data.IQuestionContainer
import com.matchr.data.Matchr
import com.matchr.data.Response
import com.matchr.utils.L
import com.matchr.views.AnimatingContainer


/**
 * Created by Allan Wang on 2017-10-21.
 */
abstract class QuestionFragment : Fragment(), IQuestionContainer {
    abstract protected val layoutRes: Int
    private var isShown: Boolean = false
    private lateinit var unbinder: Unbinder
    val question: IQuestion by lazy { Matchr.questionFromOrdinal(arguments!!.getInt(ARG_QUESTION)) }

    @BindView(R.id.animating_container)
    lateinit var container: AnimatingContainer

    companion object {
        private const val ARG_QUESTION = "arg_question"
    }

    private fun addToBundle(action: (Bundle) -> Unit): QuestionFragment {
        arguments = (arguments ?: Bundle()).apply { action(this) }
        return this
    }

    fun withQuestion(question: IQuestion)
            = addToBundle { it.putInt(ARG_QUESTION, question.ordinal) }

    abstract fun getResponseData(): List<String>?

    abstract fun onAnimProgress(progress: Float)

    override final fun getResponse(): Response? {
        val data = getResponseData()
        return if (data?.isNotEmpty() == true) Response(question.ordinal, data) else null
    }

    override final fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutRes, container, false)
        unbinder = ButterKnife.bind(this, view)
        L.d("Frag create ${hashCode()} ${question.name}")
        view.setBackgroundColor(rndColor)
        return view
    }

//    @CallSuper
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        onShow()
//    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (nextAnim <= 0) {
//            onShowImpl()
            return null
        }

        val anim = AnimationUtils.loadAnimation(activity, nextAnim)

        anim.setAnimationListener(object : AnimationListener {

            override fun onAnimationStart(animation: Animation) {
                L.d("Frag start ${hashCode()}")
            }

            override fun onAnimationRepeat(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                L.d("Frag end ${hashCode()}")
//                onShowImpl()
            }
        })

        return anim
    }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator {
        val anim = AnimatorInflater.loadAnimator(context, nextAnim)

        anim.addListener(object : AnimatorListenerAdapter() {

        })
        return super.onCreateAnimator(transit, enter, nextAnim)
    }

    //    private fun onShowImpl() {
//        if (isShown) return
//        isShown = true
//        L.d("Frag Onshow ${hashCode()}")
//        onShow()
//    }
//
//    abstract fun onShow()

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
        isShown = false
    }
}