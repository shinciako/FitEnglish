package com.davidshinto.fitenglish.ui.home.modes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.davidshinto.fitenglish.Game
import com.davidshinto.fitenglish.databinding.ActivityWriterGameBinding
import com.davidshinto.fitenglish.utils.Word
import com.davidshinto.fitenglish.utils.WordList
import java.util.Random
import kotlin.properties.Delegates

class WriterGameActivity : AppCompatActivity() {
    private lateinit var binding :ActivityWriterGameBinding
    private lateinit var inputGame: Game
    private var points = 0
    private var randomNumber by Delegates.notNull<Int>()
    private var currentQuestion = 0
    private var numberOfQuestions by Delegates.notNull<Int>()
    private val navigationArgs: WriterGameActivityArgs by navArgs()
    val categoryWordList = mutableListOf<Word>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriterGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputGame = navigationArgs.game
        WordList.wordList.forEach {word ->
            if(word.category == inputGame.category) categoryWordList.add(word)
        }

        numberOfQuestions = inputGame.questionsPerTest
        binding.tvCategoryName.text = inputGame.category
        setupQuestion()
        setupButton()
    }

    private fun setupQuestion(){
        randomizeNumber()
        binding.tvWriter.text = categoryWordList[randomNumber].engName
    }

    private fun setupButton() {
        binding.btnSubmitWriter.setOnClickListener {
            if(binding.etAnswer.text.toString() == categoryWordList[randomNumber].polName){
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
        randomNumber = Random().nextInt(categoryWordList.size - 1)
    }
}