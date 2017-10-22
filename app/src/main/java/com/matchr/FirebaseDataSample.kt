package com.matchr

import com.matchr.data.Matchr
import com.matchr.data.multiChoiceQuestion
import com.matchr.data.singleChoiceQuestion

/**
 * Created by Allan Wang on 2017-10-21.
 */
fun tutoringData() = listOf(
        singleChoiceQuestion(1, "Are you a tutor or tutee?",
                "Tutor" to 2,
                "Tutee" to 20),
        singleChoiceQuestion(2, "How many years have you been tutoring?",
                "< 1 year" to 3,
                "1 - 2 years" to 3,
                "> 2 years" to 3),
        singleChoiceQuestion(3, "How much do you charge?",
                "15" to 4,
                "15 - 30" to 4,
                "30 - 50" to 4,
                ">50" to 4),
        multiChoiceQuestion(4, "What days are you free?", 5,
                "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"),

        singleChoiceQuestion(20, "How much experience are you looking for in your tutor?",
                "< 1 year" to 21,
                "1 - 2 years" to 21,
                "> 2 years" to 21),
        multiChoiceQuestion(21, "How much are you willing to pay?", 4,
                "15", "15 - 30", "30 - 50", ">50")
)

fun tutoringMatchData() = listOf(
        Matchr(1, 1, -50f),
        Matchr(2, 20, 1f),
        Matchr(20, 2, 1f),
        Matchr(3, 21, 1f),
        Matchr(21, 3, 1f),
        Matchr(4, 4, 10f)
)