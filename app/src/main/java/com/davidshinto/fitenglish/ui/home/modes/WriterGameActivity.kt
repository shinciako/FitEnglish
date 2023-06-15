package com.davidshinto.fitenglish.ui.home.modes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.navArgs
import com.davidshinto.fitenglish.Game
import com.davidshinto.fitenglish.databinding.ActivityWriterGameBinding
import com.davidshinto.fitenglish.utils.Card
import com.davidshinto.fitenglish.utils.WordList
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
        binding.tvWriter.text = WordList.wordList[randomNumber].engName
    }

    private fun setupButton() {
        binding.btnSubmitWriter.setOnClickListener {
            if(binding.etAnswer.text.toString() == WordList.wordList[randomNumber].polName){
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
        randomNumber = Random().nextInt(WordList.wordList.size - 1)
    }
}