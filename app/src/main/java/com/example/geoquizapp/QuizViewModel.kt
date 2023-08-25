package com.example.geoquizapp

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
const val CURRENT_SCORE_KEY = "CURRENT_SCORE_KEY"
const val CURRENT_ANSWERS_KEY = "CURRENT_ANSWERS_KEY"
const val IS_CHEATER_KEY = "IS_CHEATER_KEY"
const val CHEATED_QUESTIONS_KEY = "cheatedQuestions"


class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    var isCheater: Boolean
        get() = savedStateHandle.get(IS_CHEATER_KEY) ?: false
        set(value) = savedStateHandle.set(IS_CHEATER_KEY, value)

    fun markPlayerAsCheater() {
        isCheater = true
    }

    var currentIndex: Int
        get() = savedStateHandle[CURRENT_INDEX_KEY] ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)


    var answers: Int
        get() = savedStateHandle.get(CURRENT_ANSWERS_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_ANSWERS_KEY, value)

    var score: Int
        get() = savedStateHandle.get(CURRENT_SCORE_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_SCORE_KEY, value)

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer
    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
        Log.d("MoveNext", "Current index is now $currentIndex.")
    }

    fun moveToPrev() {
        currentIndex = (currentIndex - 1) % questionBank.size
        Log.d("MovePrev", "Current index is now $currentIndex.")
    }

    val isQuizFinished: Boolean
        get() = answers == questionBank.size

    val questionBool: MutableList<Boolean> = MutableList(questionBank.size) { false }

    private val cheatedQuestions = savedStateHandle.get<MutableSet<Int>>(CHEATED_QUESTIONS_KEY) ?: mutableSetOf()

    fun markQuestionAsCheated() {
        cheatedQuestions.add(currentIndex)
        savedStateHandle.set(CHEATED_QUESTIONS_KEY, cheatedQuestions)
        Log.d("QuizViewModel", "Question $currentIndex marked as cheated.")
        Log.d("QuizViewModel", "$cheatedQuestions")
    }

    fun hasCheatedOnCurrentQuestion(): Boolean {
        val hasCheated = cheatedQuestions.contains(currentIndex)
        Log.d("QuizViewModel", "Question $currentIndex marked as cheated: $hasCheated")
        return hasCheated
    }

}



