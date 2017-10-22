package com.matchr.activities

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.allanwang.kau.animators.KauAnimator
import ca.allanwang.kau.animators.SlideAnimatorAdd
import ca.allanwang.kau.animators.SlideAnimatorRemove
import ca.allanwang.kau.ui.views.RippleCanvas
import ca.allanwang.kau.utils.*
import com.matchr.Firebase
import com.matchr.R
import com.matchr.data.Question
import com.matchr.data.QuestionType
import com.matchr.data.Response
import com.matchr.iitems.ChoiceItem
import com.matchr.iitems.PersonItem
import com.matchr.utils.L
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import java.util.*


/**
 * Created by Allan Wang on 2017-10-21.
 */
class QuestionActivity : AppCompatActivity() {

    val container: ViewGroup by bindView(R.id.question_container)
    val ripple: RippleCanvas by bindView(R.id.ripple)
    val title: TextView by bindView(R.id.question_title)
    val recycler: RecyclerView by bindView(R.id.question_recycler)
    val fastAdapter: FastItemAdapter<ChoiceItem> = FastItemAdapter()
    val fab: FloatingActionButton by bindView(R.id.fab)
    private val questionStack = Stack<Question>()
    private val userId: String by lazy { intent.getStringExtra(Firebase.USER_ID) }
    private val animator: RecyclerView.ItemAnimator by lazy {
        KauAnimator(SlideAnimatorAdd(KAU_RIGHT), SlideAnimatorRemove(KAU_LEFT)).apply {
            addDuration = 500L
            removeDuration = 500L
            interpolator = AnimHolder.fastOutSlowInInterpolator(this@QuestionActivity)
        }
    }
    private val reverseAnimator: RecyclerView.ItemAnimator by lazy {
        KauAnimator(SlideAnimatorAdd(KAU_LEFT), SlideAnimatorRemove(KAU_RIGHT)).apply {
            addDuration = 500L
            removeDuration = 500L
            interpolator = AnimHolder.fastOutSlowInInterpolator(this@QuestionActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        fastAdapter.withOnClickListener { v, adapter, item, position -> true }
                .withOnPreClickListener { _, _, _, _ -> true }
                .withEventHook(object : ClickEventHook<ChoiceItem>() {
                    override fun onBind(viewHolder: RecyclerView.ViewHolder): View?
                            = (viewHolder as? ChoiceItem.ViewHolder)?.button

                    override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<ChoiceItem>, item: ChoiceItem) {
                        recycler.itemAnimator = null
                        if (currentType == QuestionType.SINGLE_CHOICE) {
                            if (!item.isSelected) {
                                val selections = fastAdapter.selections
                                if (!selections.isEmpty()) {
                                    val selectedPosition = selections.iterator().next()
                                    fastAdapter.deselect()
                                    fastAdapter.notifyItemChanged(selectedPosition)
                                }
                                fastAdapter.select(position)
                            }
                        } else {
                            fastAdapter.toggleSelection(position)
                        }
                    }
                })
        recycler.adapter = fastAdapter
        getFirstQuestion()
        fab.setIcon(GoogleMaterial.Icon.gmd_send)
        fab.setOnClickListener { onRespond() }
    }

    private fun getFirstQuestion()
            = Firebase.getFirstQuestion { if (it >= 0) onNext(it) }

    private val currentType: QuestionType
        get() = questionStack.peek().delegate()

    fun onRespond() {
        if (questionStack.isEmpty())
            return L.e("Question stack is empty")
        val data = fastAdapter.selectedItems
        val response = Response(userId, questionStack.peek().id, data.map { it.text }.toList())
        L.d("Data received $response")
        Firebase.saveResponse(response)
        onNext(questionStack.peek().nextId(fastAdapter.selections))
    }

    fun onNext(qId: Int) {
        Firebase.getQuestion(qId, { onNext(it) })
    }

    fun onNext(q: Question?, forward: Boolean = true) {
        L.d("On next question $q")
        if (q == null) return noMoreQuestions()
        questionStack.push(q)
        recycler.itemAnimator = if (forward) animator else reverseAnimator
        q.delegate().updateAdapter(q, title, fastAdapter)
    }

    fun noMoreQuestions() {
        fab.hide()
        if (questionStack.isEmpty()) {
            materialDialog {
                title("No Questions Found")
                content("Please add some questions and refresh!")
                positiveText("Refresh")
                onPositive { _, _ -> getFirstQuestion() }
            }
        } else {
            questionStack.clear()
            fastAdapter.clear()
            title.fadeOut()
            ripple.ripple(color(R.color.colorAccent), fab.x, fab.y) {
                Firebase.matchData(userId) {
                    val resultsAdapter = FastItemAdapter<PersonItem>()
                    resultsAdapter.withOnClickListener { _, _, item, _ ->
                        Firebase.getResponses(item.user.id) {
                            materialDialog {
                                title("Responses")
                                items(it.map { it.data.joinToString(", ") })
                            }
                        }
                        true
                    }
                    recycler.adapter = resultsAdapter
                    recycler.layoutManager = GridLayoutManager(this, 2)
                    recycler.itemAnimator = KauAnimator(SlideAnimatorAdd(KAU_BOTTOM, 2f)).apply {
                        addDuration = 500L
                        interpolator = AnimHolder.fastOutSlowInInterpolator(this@QuestionActivity)
                    }
                    resultsAdapter.add(it.sortedByDescending { it.second }
                            .map { (user, score) -> PersonItem(user, score) })
                }
            }
        }
    }

    override fun onBackPressed() {
        if (questionStack.size >= 2) {
            questionStack.pop()
            onNext(questionStack.peek(), false)
        } else
            super.onBackPressed()
    }
}