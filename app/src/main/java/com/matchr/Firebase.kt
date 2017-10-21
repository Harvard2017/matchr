package com.matchr

import com.google.firebase.database.*
import com.matchr.data.Question
import com.matchr.data.QuestionType
import com.matchr.data.Response
import com.matchr.utils.L
import java.util.*

/**
 * Created by Allan Wang on 2017-10-21.
 */
object Firebase {

    const val TYPE = "type"
    const val DATA = "data"
    const val QUESTION = "question"
    const val OPTIONS = "options"
    const val USER_ID = "user_id"

    const val QUESTION_DATA_TUTORING = "q_tutoring"

    val question_pool_key = QUESTION_DATA_TUTORING

    val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    val users
        get() = database.getReference("test")

    inline fun getFirstQuestion(crossinline callback: (Int) -> Unit) {
        users.genericGet("startId") { data ->
            L.v(data?.key)
            L.v(data?.value?.toString())
            callback(data.intValueOr(-1))
        }
    }

    fun DataSnapshot?.intValueOr(default: Int) = this?.value?.toString()?.toIntOrNull() ?: default

    inline fun getQuestion(id: Int, crossinline callback: (Question?) -> Unit) {
        users.genericGet(Endpoints.QUESTIONS.pathWith("$question_pool_key/$id")) { data ->
            if (data == null) return@genericGet callback(null)
            val nextId = data.key.toInt()
            val question = data.child(QUESTION).value.toString()
            val options = data.child(OPTIONS).children.filterNotNull().map { d ->
                d.key to d.intValueOr(-1)
            }
            val type = data.child(TYPE).intValueOr(0)
            callback(Question(nextId, question, options, if (type >= QuestionType.values.size) 0 else type))
        }
    }

    fun DatabaseReference.genericGet(path: String, callback: (DataSnapshot?) -> Unit) {
        L.v("Getting data for path $path")
        child(path).ref.orderByPriority().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                callback(null)
            }

            override fun onDataChange(p0: DataSnapshot) {
                callback(p0)
            }
        })
    }

    fun saveResponse(id: String, response: Response) {
        Endpoints.RESPONSES.saveData(id, response.qOrdinal,
                DATA to response.data)
    }

    fun saveQuestion(poolKey: String, question: Question) {
        val data = mutableListOf<Pair<String, Any>>().apply {
            add(TYPE to question.type)
            add(QUESTION to question.question)
            question.options.forEach { (k, v) ->
                add("$OPTIONS/$k" to v)
            }
        }
        saveDataImpl("${Endpoints.QUESTIONS.name.toLowerCase(Locale.US)}/$poolKey",
                question.id, *data.toTypedArray())
    }

    fun test() {
        testImpl()
    }

    fun testImpl() {
        tutoringData().map { saveQuestion(QUESTION_DATA_TUTORING, it) }
    }

    fun readQuestion(id: Int, callback: (Question) -> Unit) {

    }
}

enum class Endpoints {
    RESPONSES, QUESTIONS;

    fun saveData(userId: String, childId: Int, vararg data: Pair<String, Any>)
            = saveDataImpl("$userId/${name.toLowerCase(Locale.US)}", childId, *data)

    fun pathWith(post: String) = "${name.toLowerCase(Locale.US)}/$post"
}

fun saveDataImpl(path: String, childId: Int, vararg data: Pair<String, Any>) {
    val child = Firebase.users.child("$path/$childId")
    data.forEachIndexed { i, (k, v) ->
        child.child(k).setValue(v)
        child.child(k).setPriority(i)
    }
}