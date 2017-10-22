package com.matchr

import ca.allanwang.kau.utils.postDelayed
import com.google.firebase.database.*
import com.matchr.data.*
import com.matchr.utils.L
import org.jetbrains.anko.doAsync
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
    const val RESPONSE_ID = "response_id"
    const val WEIGHT = "weight"
    const val NAME = "name"
    const val EMAIL = "email"

    const val QUESTION_DATA_TUTORING = "q_tutoring"

    val question_pool_key = QUESTION_DATA_TUTORING

    val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    val users
        get() = database.getReference("test")

    fun getFirstQuestion(callback: (Int) -> Unit) {
        users.genericGet("startId") { data ->
            L.v(data?.key)
            L.v(data?.value?.toString())
            callback(data.intValueOr(-1))
        }
    }

    private fun DataSnapshot?.intValueOr(default: Int) = this?.value?.toString()?.toIntOrNull() ?: default

    private fun DataSnapshot?.floatValueOr(default: Float) = this?.value?.toString()?.toFloatOrNull() ?: default

    fun getQuestion(id: Int, callback: (Question?) -> Unit) {
        users.genericGet(Endpoints.QUESTIONS.pathWith("$question_pool_key/$id")) { data ->
            if (data == null) return@genericGet callback(null)
            val question = data.child(QUESTION).value.toString()
            val options = data.child(OPTIONS).children.filterNotNull().map { d ->
                d.key to d.intValueOr(-1)
            }
            if (options.isEmpty()) return@genericGet callback(null)
            val type = data.child(TYPE).intValueOr(0)
            callback(Question(id, question, options, if (type >= QuestionType.values.size) 0 else type))
        }
    }

    fun saveQuestion(poolKey: String, question: Question) {
        val data = mutableListOf<Pair<String, Any>>().apply {
            add(TYPE to question.type)
            add(QUESTION to question.question)
            question.options.forEach { (k, v) ->
                add("$OPTIONS/$k" to v)
            }
        }
        Endpoints.QUESTIONS.saveData(poolKey, question.id, *data.toTypedArray())
    }

    private fun DatabaseReference.genericGet(path: String, callback: (DataSnapshot?) -> Unit) {
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

    fun saveResponse(response: Response) {
        Endpoints.RESPONSES.saveData("$question_pool_key/${response.userId}", response.qOrdinal,
                DATA to response.data)
    }

    fun getResponse(id: String, qId: Int, callback: (Response?) -> Unit) {
        users.genericGet(Endpoints.RESPONSES.pathWith("$question_pool_key/$id")) { data ->
            if (data == null) return@genericGet callback(null)
            val response = data.child(DATA).children.filterNotNull().map { d -> d.value.toString() }
            if (response.isEmpty()) return@genericGet callback(null)
            callback(Response(id, qId, response))
        }
    }

    fun test(userId: String) {
//        testImpl()
    }

    private fun testImpl() {
        tutoringMatchData().map { saveMatchr(QUESTION_DATA_TUTORING, it) }
        postDelayed(2000) {
            getMatchrs {
                it.forEach { L.d(it.toString()) }
            }
        }
//        tutoringData().map { saveQuestion(QUESTION_DATA_TUTORING, it) }
    }

    fun matchData(userId: String, callback: (List<Pair<User, Float>>) -> Unit) {
        doAsync {
            users.genericGet(Endpoints.RESPONSES.pathWith(question_pool_key)) { data ->
                if (data == null) return@genericGet callback(emptyList())
                var self: Pair<User, Map<Int, List<String>>>? = null
                getUsers { users ->
                    if (users.isEmpty()) return@getUsers callback(emptyList())
                    val candidates: List<Pair<User, Map<Int, List<String>>>> = data.children.mapNotNull {
                        L.d("K ${it.key}")
                        val candidate = users[it.key] ?: return@mapNotNull null
                        val responses = it.children.map { r ->
                            val response = r.child(DATA).children.filterNotNull().map { d -> d.value.toString() }
                            r.key.toInt() to response
                        }.filter { it.second.isNotEmpty() }.toMap()
                        if (candidate.id == userId)
                            self = candidate to responses
                        candidate to responses
                    }.filter { (user, responses) -> user.id != "null" && user.id != userId && responses.isNotEmpty() }
                    L.d("Self $self")
                    L.d("Match candidates $candidates")
                    if (candidates.isEmpty() || self == null) return@getUsers callback(emptyList())
                    getMatchrs { m ->
                        if (m.isEmpty()) return@getMatchrs callback(emptyList())
                        callback(match(m, self!!.second, candidates))
                    }
                }
            }
        }
    }

    private fun match(matchrs: List<Matchr>, selfResponses: Map<Int,
            List<String>>, candidates: List<Pair<User, Map<Int, List<String>>>>): List<Pair<User, Float>> {
        return candidates.map { c ->
            val score = matchrs.map { m ->
                match(m, selfResponses, c)
            }.sum()
            val u = c.first to score
            L.v("UUU $u")
            u
        }
    }

    private fun match(matchr: Matchr, selfResponses: Map<Int, List<String>>,
                      candidate: Pair<User, Map<Int, List<String>>>): Float {
        val data1 = selfResponses[matchr.rId1] ?: return 0f
        val data2 = candidate.second[matchr.rId2] ?: return 0f
        //compute intersect, divide by average data size, multiply be weight
        return data1.intersect(data2).size.toFloat() * 2 / (data1.size + data2.size) * matchr.weight
    }

    fun saveUser(user: User) {
        val child = database.reference.child(Endpoints.USERS.pathWith(user.id))
        child.child(NAME).setValue(user.name)
        child.child(EMAIL).setValue(user.email)
    }

    fun getUsers(callback: (Map<String, User>) -> Unit) {
        database.reference.genericGet(Endpoints.USERS.pathWith("")) { data ->
            data ?: return@genericGet callback(emptyMap())
            callback(data.children.map { it.key to User(it.key, it.child(NAME).toString(), it.child(EMAIL).toString()) }.toMap())
        }
    }

    fun saveMatchr(poolKey: String, rId1: Int, rId2: Int, weight: Float)
            = saveMatchr(poolKey, Matchr(rId1, rId2, weight))

    fun saveMatchr(poolKey: String, matchr: Matchr) {
        Endpoints.MATCHR.saveData(poolKey, matchr.rId1,
                RESPONSE_ID to matchr.rId2, WEIGHT to matchr.weight)
    }

    fun getMatchrs(callback: (List<Matchr>) -> Unit) {
        users.genericGet(Endpoints.MATCHR.pathWith(question_pool_key)) { data ->
            data ?: return@genericGet callback(emptyList())
            callback(data.children.map {
                val children = it.children
                Matchr(it.key.toInt(), it.child(RESPONSE_ID).intValueOr(-1),
                        it.child(WEIGHT).floatValueOr(0f))
            }.filter { it.rId1 >= 0 && it.rId2 >= 0 && it.weight != 0f })
        }
    }


}

enum class Endpoints {
    RESPONSES, QUESTIONS, MATCHR, USERS;

    fun saveData(userId: String, childId: Int, vararg data: Pair<String, Any>)
            = saveDataImpl(pathWith(userId), childId, *data)

    fun pathWith(post: String) = "${name.toLowerCase(Locale.US)}/$post"
}

fun saveDataImpl(path: String, childId: Int, vararg data: Pair<String, Any>) {
    val child = Firebase.users.child("$path/$childId")
    data.forEachIndexed { i, (k, v) ->
        child.child(k).setValue(v)
        child.child(k).setPriority(i)
    }
}