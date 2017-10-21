package com.matchr.activities

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.allanwang.kau.animators.KauAnimator
import ca.allanwang.kau.animators.SlideAnimatorAdd
import ca.allanwang.kau.animators.SlideAnimatorRemove
import ca.allanwang.kau.utils.AnimHolder
import ca.allanwang.kau.utils.KAU_LEFT
import ca.allanwang.kau.utils.KAU_RIGHT
import ca.allanwang.kau.utils.bindView
import com.matchr.Firebase
import com.matchr.R
import com.matchr.data.Question
import com.matchr.data.QuestionType
import com.matchr.data.Response
import com.matchr.iitems.ChoiceItem
import com.matchr.utils.L
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import java.util.*


/**
 * Created by Allan Wang on 2017-10-21.
 */
class QuestionActivity : AppCompatActivity() {

    val container: ViewGroup by bindView(R.id.question_container)
    val title: TextView by bindView(R.id.question_title)
    val recycler: RecyclerView by bindView(R.id.question_recycler)
    val fastAdapter: FastItemAdapter<ChoiceItem> = FastItemAdapter()
    val fab: FloatingActionButton by bindView(R.id.fab)
    private val questionStack = Stack<Question>()
    private val userId: String by lazy { intent.getStringExtra(Firebase.USER_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

//        fastAdapter.withOnClickListener { v, adapter, item, position -> true }
//                .withOnPreClickListener { _, _, _, _ -> true }
//                .withEventHook(object : ClickEventHook<ChoiceItem>() {
//                    override fun onBind(viewHolder: RecyclerView.ViewHolder): View?
//                            = (viewHolder as? ChoiceItem.ViewHolder)?.button
//
//                    override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<ChoiceItem>, item: ChoiceItem) {
//                        if (currentType == QuestionType.SINGLE_CHOICE) {
//                            if (!item.isSelected) {
//                                val selections = fastAdapter.selections
//                                if (!selections.isEmpty()) {
//                                    val selectedPosition = selections.iterator().next()
//                                    fastAdapter.deselect()
//                                    fastAdapter.notifyItemChanged(selectedPosition)
//                                }
//                                fastAdapter.select(position)
//                            }
//                        } else {
//                            fastAdapter.toggleSelection(position)
//                        }
//                    }
//                })
        recycler.apply {
            adapter = fastAdapter
//            itemAnimator = KauAnimator(SlideAnimatorAdd(KAU_RIGHT), FadeScaleAnimatorRemove()).apply {
//            itemAnimator = KauAnimator(SlideAnimatorAdd(KAU_BOTTOM, 3f), SlideAnimatorRemove(KAU_TOP, 3f)).apply {
            itemAnimator = KauAnimator(SlideAnimatorAdd(KAU_RIGHT), SlideAnimatorRemove(KAU_LEFT)).apply {
                addDuration = 500L
                removeDuration = 500L
                interpolator = AnimHolder.fastOutSlowInInterpolator(context)
            }
        }
        Firebase.getFirstQuestion {
            if (it >= 0)
                onNext(it)
        }
        fab.setOnClickListener {
            onRespond()
        }
    }

    private val currentType: QuestionType
        get() = questionStack.peek().delegate()

    fun onRespond() {
        if (questionStack.isEmpty())
            return L.e("Question stack is empty")
        val data = fastAdapter.selectedItems
        val response = Response(questionStack.peek().id, data.map { it.text }.toList())
        L.d("Data received $response")
        onNext(questionStack.peek().nextId(fastAdapter.selections))
    }

    fun onNext(qId: Int) {
        Firebase.getQuestion(qId, this::onNext)
    }

    fun onNext(q: Question?) {
        L.d("On next question $q")
        if (q == null) return //todo finish
        questionStack.push(q)
        q.delegate().updateAdapter(q, title, fastAdapter)
    }
}