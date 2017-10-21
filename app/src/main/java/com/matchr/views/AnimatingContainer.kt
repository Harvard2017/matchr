package com.matchr.views

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet

/**
 * Created by Allan Wang on 2017-10-21.
 */
class AnimatingContainer @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var animListener: (Float) -> Unit = {}

    var animProgress: Float = 0f
        set(value) {
            field = value
            animListener(value)
        }


}