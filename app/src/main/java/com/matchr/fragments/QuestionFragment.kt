package com.matchr.fragments

import android.app.Fragment
import android.os.Bundle
import android.support.annotation.CallSuper
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
    abstract val question: IQuestion
    private var isShown: Boolean = false
    private lateinit var unbinder: Unbinder

    override final fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(layoutRes, container, false)
        unbinder = ButterKnife.bind(this, view)
        return view
    }

    @CallSuper
    open fun translate(offset: Float) {
        if (offset == 0f && !isShown) {
            onShow()
            isShown = true
        }
    }

    abstract fun onShow()

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
        isShown = false
    }
}