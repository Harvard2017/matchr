package com.matchr

import com.matchr.data.Matchr
import com.matchr.data.multiChoiceQuestion
import com.matchr.data.singleChoiceQuestion

/**
 * Created by Allan Wang on 2017-10-21.
 */
fun tutoringData() = listOf(
        singleChoiceQuestion(1, "Are you a tutor or tutee?",
                "Tutor" to 6,
                "Tutee" to 20),
        singleChoiceQuestion(2, "How many years have you been tutoring?",
                "< 1 year" to 3,
                "1 - 2 years" to 3,
                "> 2 years" to 3),
        singleChoiceQuestion(3, "How much do you charge?",
                "15" to 7,
                "15 - 30" to 7,
                "30 - 50" to 7,
                ">50" to 7),
        multiChoiceQuestion(4, "What days are you free?", 5,
                "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"),

        singleChoiceQuestion(5, "What major are you in?",
                "Computer Science" to 22,
                "Biology" to 22,
                "Chemistry" to 22,
                "Psychology" to 22,
                "Political Science" to 22),

        singleChoiceQuestion(6, "What is the highest education you have?",
                "Bachelor's" to 2,
                "Master's" to 2,
                "PhD" to 2),

        singleChoiceQuestion(7, "What major do you specialize in?",
                "Computer Science" to 22,
                "Biology" to 22,
                "Chemistry" to 22,
                "Psychology" to 22,
                "Political Science" to 22),

        multiChoiceQuestion(22, "Where is your preferred choice of study?", 4,
                "Library", "Cafe", "At home", "At school"),

        singleChoiceQuestion(20, "How much experience are you looking for in your tutor?",
                "< 1 year" to 21,
                "1 - 2 years" to 21,
                "> 2 years" to 21),

        singleChoiceQuestion(23, "How often are you planning to meet?",
                "< once a week" to 22,
                "1 - 2 times per week" to 22,
                "> 3 times per week" to 22),

        multiChoiceQuestion(21, "How much are you willing to pay?", 4,
                "15", "15 - 30", "30 - 50", ">50")
)

fun tutoringMatchData() = listOf(
        Matchr(1, 1, -50f),
        Matchr(2, 20, 1f),
        Matchr(20, 2, 1f),
        Matchr(3, 21, 1f),
        Matchr(21, 3, 1f),
        Matchr(4, 4, 10f),
        Matchr(4, 4, 10f),
        Matchr(6,2,1f),
        Matchr(2,6,1f),
        Matchr(22,22,1f),
        Matchr(23,23,1f),
        Matchr(5,7,1f),
        Matchr(7,5,1f)

)