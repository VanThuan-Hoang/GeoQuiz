package com.example.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {
    var scoreCount =0
    var questionNum = -2
    var currentIndex = 0
    var isAnswer = false
    var isCheater = false
    var isFinish = false
    private val questionBank = mutableListOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, true),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, false)
    )

    private val questionBankOriginal = mutableListOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, true),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, false)
    )

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer
    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId
    val questionSize: Int
        get() = questionBank.size

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }
    fun moveToBack() {
        currentIndex = (currentIndex - 1) % questionBank.size
    }
    fun removeAt() {
        questionBank.removeAt(currentIndex)
    }
    fun reset(){
        scoreCount =0
        questionNum = questionBankOriginal.size
        currentIndex = 0
        isAnswer = false
        isCheater = false
        isFinish = false
        Log.i("OriginalSize:", questionBankOriginal.size.toString())
        for (it in 1..questionBankOriginal.size){
            questionBank.add(questionBankOriginal[it-1])
        }
    }
}