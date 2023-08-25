package com.example.geoquizapp

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.geoquizapp.databinding.ActivityMainBinding


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by viewModels()

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle the result
        if (result.resultCode == Activity.RESULT_OK) {
            quizViewModel.isCheater =
                result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        Log.d("MainActivity", "QuizViewModel instance: $quizViewModel")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Got a com.example.geoquizapp.QuizViewModel: $quizViewModel")

        binding.trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
            disableButtons()

        }

        binding.falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
            disableButtons()
        }

        binding.nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        binding.prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQuestion()
        }

        binding.cheatButton.setOnClickListener {
            // Start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }
        updateQuestion()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        if (quizViewModel.hasCheatedOnCurrentQuestion()) {
            Toast.makeText(this, R.string.judgment_toast, Toast.LENGTH_SHORT).show()
        }else {
            val messageResId = when (userAnswer) {
                correctAnswer -> R.string.correct_toast
                else -> R.string.incorrect_toast
            }
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
                .show()
        }
        quizViewModel.answers++

        if (userAnswer == correctAnswer) {
            quizViewModel.score++
        }

        if (quizViewModel.isQuizFinished){
            quizFinished()
        }

        quizViewModel.questionBool[quizViewModel.currentIndex] = true
    }

    private fun updateQuestion() {
        checkBoundary()
        if (!quizViewModel.questionBool[quizViewModel.currentIndex]) {
            enableButtons()
        } else {
            disableButtons()
        }
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)

    }



    private fun disableButtons() {
        binding.falseButton.isEnabled = false
        binding.falseButton.isClickable = false
        binding.trueButton.isClickable = false
        binding.trueButton.isEnabled = false
    }

    private fun enableButtons() {
        binding.falseButton.isEnabled = true
        binding.falseButton.isClickable = true
        binding.trueButton.isClickable = true
        binding.trueButton.isEnabled = true
    }

    private fun checkBoundary() {
        val index = quizViewModel.currentIndex
        if (quizViewModel.currentIndex + 1 >= quizViewModel.questionBank.size) {
            binding.nextButton.isEnabled = false
            binding.nextButton.isClickable = false
        } else if (index - 1 < 0) {
            binding.prevButton.isEnabled = false
            binding.prevButton.isClickable = false
        } else {
            binding.nextButton.isEnabled = true
            binding.nextButton.isClickable = true
            binding.prevButton.isEnabled = true
            binding.prevButton.isClickable = true
        }

    }
    private fun quizFinished(){
        val size = quizViewModel.questionBank.size
        val score = quizViewModel.score
        val finalScore = (score.toDouble() / size.toDouble()) * 100
        val string = getString(R.string.score_toast, finalScore)

        if (!quizViewModel.isCheater) {
            Toast.makeText(this, string, Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this, R.string.Cheater, Toast.LENGTH_SHORT).show()
        }
    }

}




