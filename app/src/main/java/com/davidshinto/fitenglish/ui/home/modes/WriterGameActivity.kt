package com.davidshinto.fitenglish.ui.home.modes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.navArgs
import com.davidshinto.fitenglish.Game
import com.davidshinto.fitenglish.databinding.ActivityWriterGameBinding
import com.davidshinto.fitenglish.utils.Card
import java.util.*
import kotlin.properties.Delegates

class WriterGameActivity : AppCompatActivity() {
    private lateinit var binding :ActivityWriterGameBinding
    private lateinit var inputGame: Game
    private var points = 0
    private var randomNumber by Delegates.notNull<Int>()
    private var currentQuestion = 0
    private var numberOfQuestions by Delegates.notNull<Int>()
    private val navigationArgs: WriterGameActivityArgs by navArgs()

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
        binding = ActivityWriterGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputGame = navigationArgs.game
        numberOfQuestions = inputGame.questionsPerTest
        binding.tvCategoryName.text = inputGame.category.name
        setupQuestion()
        setupButton()
    }

    private fun setupQuestion(){
        randomizeNumber()
        binding.tvWriter.text = dummyFlashcards[randomNumber].englishWord
    }

    private fun setupButton() {
        binding.btnSubmitWriter.setOnClickListener {
            if(binding.etAnswer.text.toString() == dummyFlashcards[randomNumber].polishWord){
                points++
                currentQuestion++
                setupQuestion()
            }

            if(currentQuestion >= numberOfQuestions) {
                Toast.makeText(this, "Passed all questions", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun randomizeNumber() {
        randomNumber = Random().nextInt(dummyFlashcards.size - 1)
    }
}