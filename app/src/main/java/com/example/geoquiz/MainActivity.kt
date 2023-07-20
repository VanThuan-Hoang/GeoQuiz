package com.example.geoquiz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView

    private val quizViewModel: QuizViewModel by lazy{
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }
    private var scorePercent = 0.toDouble()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate(Bundle?) called")

        if (quizViewModel.questionNum == -2) {
            quizViewModel.questionNum = quizViewModel.questionSize
        }
        Log.i("questionNum:", quizViewModel.questionNum.toString())

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        previousButton = findViewById(R.id.previous_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)

        trueButton.setOnClickListener { //view: View ->
            if (!quizViewModel.isFinish) {
                Log.i("isAnswer:", quizViewModel.isAnswer.toString())
                if (!quizViewModel.isAnswer) {
                    checkAnswer(true)
                    quizViewModel.isAnswer = true
                } else {
                    val messageResId = R.string.already_answer
                    Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
                }
            } else {
                val messageResId = R.string.already_finish
                Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
            }
        }
        falseButton.setOnClickListener { //view: View ->
            if (!quizViewModel.isFinish) {
                Log.i("isAnswer:", quizViewModel.isAnswer.toString())
                if (!quizViewModel.isAnswer) {
                    checkAnswer(false)
                    quizViewModel.isAnswer = true
                } else {
                    val messageResId = R.string.already_answer
                    Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
                }
            } else {
                val messageResId = R.string.already_finish
                Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
            }
        }
        nextButton.setOnClickListener { //view: View ->
            if (!quizViewModel.isFinish){
                if (quizViewModel.questionSize > 0) {
                    //currentIndex = (currentIndex + 1) % questionBank.size
                    quizViewModel.moveToNext()
                }
                updateQuestion()
                Log.i("CurrentSize:", quizViewModel.questionSize.toString())
            } else {
                val messageResId = R.string.already_finish
                Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
            }
        }
        previousButton.setOnClickListener {
            if (!quizViewModel.isFinish) {
                if (quizViewModel.questionSize > 0) {
                    //currentIndex = (currentIndex - 1) % questionBank.size
                    quizViewModel.moveToBack()
                    //if (currentIndex<0)
                    if (quizViewModel.currentIndex < 0)
                        quizViewModel.currentIndex = quizViewModel.questionSize - 1
                }
                updateQuestion()
                //Log.i("CurrentSize:", questionBank.size.toString())
                Log.i("CurrentSize:", quizViewModel.questionSize.toString())
            } else {
                val messageResId = R.string.already_finish
                Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
            }
        }
        cheatButton.setOnClickListener {
            if (!quizViewModel.isFinish) {
                Log.i("isAnswer:", quizViewModel.isAnswer.toString())
                if (!quizViewModel.isAnswer) {
                    // Start CheatActivity
                    //val intent = Intent(this, CheatActivity::class.java)
                    val answerIsTrue = quizViewModel.currentQuestionAnswer
                    val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
                    //startActivity(intent)
                    startActivityForResult(intent, REQUEST_CODE_CHEAT)
                } else {
                    val messageResId = R.string.already_answer
                    Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
                }
            } else {
                val messageResId = R.string.already_finish
                Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
            }
        }
        questionTextView.setOnClickListener {
            if (quizViewModel.isFinish) {
                quizViewModel.reset()
                val questionTextResId = quizViewModel.currentQuestionText
                questionTextView.setText(questionTextResId)
            }
        }
        updateQuestion()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }
    private fun updateQuestion() {
        //Log.d(TAG, "Updating question text", Exception())
        quizViewModel.isAnswer = false
        quizViewModel.isCheater = false
        //if (questionBank.size>0){
        if (quizViewModel.questionSize > 0){
            //val questionTextResId = questionBank[currentIndex].textResId
            val questionTextResId = quizViewModel.currentQuestionText
            questionTextView.setText(questionTextResId)
        } else {
            scorePercent = quizViewModel.scoreCount.toDouble() / quizViewModel.questionNum * 10000
            scorePercent -= scorePercent % 1
            scorePercent /= 100

            val stringScore = "Your score: ".plus(scorePercent.toString()).plus(" % \n\nPress here to play again OR Good luck! :)")
            questionTextView.setText(stringScore)
            quizViewModel.isFinish = true

            Log.i("scoreCount:", quizViewModel.scoreCount.toString())
            Log.i("questionNum:", quizViewModel.questionNum.toString())
        }
    }
    private fun checkAnswer(userAnswer: Boolean) {
        if (quizViewModel.questionSize > 0){
            val correctAnswer = quizViewModel.currentQuestionAnswer
            Log.i("isCheater:", quizViewModel.isCheater.toString())
            val messageResId = when {
                quizViewModel.isCheater -> R.string.judgment_toast
                userAnswer == correctAnswer -> R.string.correct_toast
                else -> R.string.incorrect_toast
            }
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

            //questionBank.removeAt(currentIndex)
            quizViewModel.removeAt()
            if (userAnswer == correctAnswer) quizViewModel.scoreCount += 1
            Log.i("scoreCount:", quizViewModel.scoreCount.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
        Log.i("questionNum:", quizViewModel.questionNum.toString())
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
        Log.i("questionNum:", quizViewModel.questionNum.toString())
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
}