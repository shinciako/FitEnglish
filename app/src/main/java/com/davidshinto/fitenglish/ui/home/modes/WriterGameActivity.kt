package com.davidshinto.fitenglish.ui.home.modes

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.davidshinto.fitenglish.databinding.ActivityWriterGameBinding
import com.davidshinto.fitenglish.db.Session
import com.davidshinto.fitenglish.utils.*
import java.time.OffsetDateTime
import java.util.*
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class WriterGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriterGameBinding
    private lateinit var inputGame: Game
    private var points = 0
    private var randomNumber by Delegates.notNull<Int>()
    private val navigationArgs: WriterGameActivityArgs by navArgs()
    val categoryWordList = mutableListOf<Word>()
    private var currentPosition = 0

    private var numberOfQuestions = 0
    private var currentDistance = 0
    private lateinit var gameConfHelper: GameHelper

    private lateinit var wordList: List<Word>
    private lateinit var englishWords: MutableList<String>
    private lateinit var polishWords: MutableList<String>

    private val startGPSTrackerActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val returnedGameHelper = result.data?.parcelable<GameHelper>("GAME_HELPER")
                currentDistance += returnedGameHelper?.breakDistance ?: gameConfHelper.breakDistance
                currentPosition = 0
                binding.pbQuestionsWriter.progress = 0
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriterGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        inputGame = navigationArgs.game
        WordList.wordList.forEach {word ->
            if(word.category == inputGame.category) categoryWordList.add(word)
        }

        binding.tvCategoryName.text = inputGame.category
        setupQuestion()
        gameConfHelper = GameHelper(inputGame.distanceAfterTest, inputGame.distance)
        wordList = WordList.wordList
        setupButton()
    }

    override fun onStart() {
        super.onStart()
        englishWords = wordList.map { it.engName }.toMutableList()
        polishWords = wordList.map { it.polName }.toMutableList()
        setupQuestion()
    }

    private fun setupQuestion() {
        randomizeNumber()
        binding.tvWriter.text = categoryWordList[randomNumber].engName
        binding.etAnswer.setText("")
    }

    private fun setupButton() {
        binding.btnSubmitWriter.setOnClickListener {
            if(binding.etAnswer.text.toString() == categoryWordList[randomNumber].polName){
                points++
            }
            currentPosition++
            numberOfQuestions++
            if (currentPosition == inputGame.questionsPerTest) {
                binding.pbQuestionsWriter.progress = 100
                if (currentDistance < inputGame.distance) {
                    trackDistance()
                } else {
                    finishGame()
                }
            } else {
                setupQuestion()
                binding.pbQuestionsWriter.progress += ((1.0 / inputGame.questionsPerTest) * 100).toInt()
            }
        }
    }

    private fun trackDistance() {
        val intent = Intent(this, GPSTracker::class.java)
        intent.putExtra("GAME_HELPER", gameConfHelper)
        startGPSTrackerActivity.launch(intent)
    }

    private fun finishGame() {
        val accuracy =
            (points.toFloat() / numberOfQuestions.toFloat()) * 100.0
        accuracy.roundToInt()
        val session = Session(0, inputGame, accuracy, numberOfQuestions, OffsetDateTime.now())
        val popupActivity = FinishScreenActivity(this, session)
        popupActivity.show()
    }

    private fun randomizeNumber() {
        randomNumber = Random().nextInt(categoryWordList.size - 1)
    }
}