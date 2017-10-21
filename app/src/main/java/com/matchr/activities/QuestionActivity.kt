package com.matchr.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import ca.allanwang.kau.ui.views.RippleCanvas
import ca.allanwang.kau.ui.widgets.InkPageIndicator
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.fadeScaleTransition
import ca.allanwang.kau.utils.scaleXY
import ca.allanwang.kau.utils.setIcon
import com.matchr.R
import com.matchr.fragments.QuestionFragment
import com.matchr.fragments.ShortAnswerFragment
import com.mikepenz.google_material_typeface_library.GoogleMaterial

/**
 * Created by Allan Wang on 2017-10-21.
 */
class QuestionActivity : AppCompatActivity(), ViewPager.PageTransformer, ViewPager.OnPageChangeListener {

    val ripple: RippleCanvas by bindView(R.id.intro_ripple)
    val viewpager: ViewPager by bindView(R.id.intro_viewpager)
    lateinit var adapter: IntroPageAdapter
    val indicator: InkPageIndicator by bindView(R.id.intro_indicator)
    val skip: Button by bindView(R.id.intro_skip)
    val next: ImageButton by bindView(R.id.intro_next)
    private var barHasNext = true

    val fragments = listOf(
            ShortAnswerFragment(),
            ShortAnswerFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)
        adapter = IntroPageAdapter(supportFragmentManager, fragments)
        viewpager.apply {
            setPageTransformer(true, this@QuestionActivity)
            addOnPageChangeListener(this@QuestionActivity)
            adapter = this@QuestionActivity.adapter
        }
        indicator.setViewPager(viewpager)
        next.setIcon(GoogleMaterial.Icon.gmd_navigate_next)
        next.setOnClickListener {
            if (barHasNext) viewpager.setCurrentItem(viewpager.currentItem + 1, true)
            else finish(next.x + next.pivotX, next.y + next.pivotY)
        }
        skip.setOnClickListener { finish() }
//        ripple.set(Prefs.bgColor)
    }

    fun finish(x: Float, y: Float) = finish()

    /**
     * Transformations are mainly handled on a per view basis
     * This sifies it by making the first fragment fade out as the second fragment comes in
     * All fragments are locked in position
     */
    override fun transformPage(page: View, position: Float) {
        //only apply to adjacent pages
        if ((position < 0 && position > -1) || (position > 0 && position < 1)) {
            val pageWidth = page.width
            val translateValue = position * -pageWidth
            page.translationX = (if (translateValue > -pageWidth) translateValue else 0f)
            page.alpha = if (position < 0) 1 + position else 1f
        } else {
            page.alpha = 1f
            page.translationX = 0f
        }

    }

    override fun onBackPressed() {
        if (viewpager.currentItem > 0) viewpager.setCurrentItem(viewpager.currentItem - 1, true)
        else finish()
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        fragments[position].onPageScrolled(positionOffset)
        if (position + 1 < fragments.size)
            fragments[position + 1].onPageScrolled(positionOffset - 1)
    }

    override fun onPageSelected(position: Int) {
        fragments[position].onPageSelected()
        val hasNext = position != fragments.size - 1
        if (barHasNext == hasNext) return
        barHasNext = hasNext
        next.fadeScaleTransition {
            setIcon(if (barHasNext) GoogleMaterial.Icon.gmd_navigate_next else GoogleMaterial.Icon.gmd_done, color = Color.WHITE)
        }
        skip.animate().scaleXY(if (barHasNext) 1f else 0f)
    }

    class IntroPageAdapter(fm: FragmentManager, private val fragments: List<QuestionFragment>) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.size
    }

}