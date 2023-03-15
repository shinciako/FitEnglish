package com.davidshinto.fitenglish

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.davidshinto.fitenglish.databinding.ActivityFlashGameBinding
import kotlin.properties.Delegates


class FlashGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFlashGameBinding
    private val navigationArgs : FlashGameActivityArgs by navArgs()
    private var currentPosition = 0
    private var numberOfQuestions by Delegates.notNull<Int>()
    private var points = 0


    private val dummyFlashcards = arrayOf(
        Card(0, "PL1", "ENG1"),
        Card(1, "PL2", "ENG2"),
        Card(2, "PL3", "ENG3"),
        Card(3, "PL4", "ENG4"),
        Card(4, "PL5", "ENG5"),
        Card(5, "PL6", "ENG6"),
        Card(6, "PL7", "ENG7"),
        Card(7, "PL8", "ENG8"),
        Card(8, "PL9", "ENG9"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inputGame = navigationArgs.game
        numberOfQuestions = inputGame.questionsPerTest
        binding.tvCategoryName.text = inputGame.category.name
        binding.tvFlash.text = dummyFlashcards[0].englishWord
        setupButtons()
    }

    private fun setupButtons(){
        binding.btnNo.setOnClickListener{
            goToNextQuestion()
        }

        binding.btnYes.setOnClickListener{
            points++
            goToNextQuestion()
        }
    }

    private fun goToNextQuestion(){
        currentPosition++
        if(currentPosition<numberOfQuestions){
            binding.tvFlash.text = dummyFlashcards[currentPosition].englishWord
            binding.pbQuestions.progress += ((1.0/numberOfQuestions) * 100).toInt()
        } else {
            binding.tvFlash.text = "Finito szefie"
            binding.pbQuestions.progress = 100
        }
    }
}