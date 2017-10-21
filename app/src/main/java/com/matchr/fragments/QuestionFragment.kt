package com.matchr.fragments

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import com.matchr.data.IQuestion


/**
 * Created by Allan Wang on 2017-10-21.
 */
abstract class QuestionFragment : Fragment() {
    abstract protected val layoutRes: Int
    private var isShown: Boolean = false
    private lateinit var unbinder: Unbinder
    val question: IQuestion by lazy { arguments!!.getParcelable<IQuestion>(ARG_QUESTION) }

    companion object {
        private const val ARG_QUESTION_TYPE = "arg_question_type"
        private const val ARG_QUESTION = "arg_question"
    }

    private fun addToBundle(action: (Bundle) -> Unit): QuestionFragment {
        arguments = (arguments ?: Bundle()).apply { action(this) }
        return this
    }

    fun withQuestion(question: IQuestion)
            = addToBundle { it.putParcelable(ARG_QUESTION, question) }

    override final fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutRes, container, false)
        unbinder = ButterKnife.bind(this, view)
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

    abstract fun onShow()

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
        isShown = false
    }
}