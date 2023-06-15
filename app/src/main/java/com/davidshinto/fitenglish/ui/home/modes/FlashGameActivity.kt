package com.davidshinto.fitenglish.ui.home.modes

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.davidshinto.fitenglish.databinding.ActivityFlashGameBinding
import com.davidshinto.fitenglish.db.Session
import com.davidshinto.fitenglish.utils.Word
import com.davidshinto.fitenglish.utils.WordList
import com.davidshinto.fitenglish.utils.parcelable
import java.time.OffsetDateTime
import java.util.Random
import com.davidshinto.fitenglish.utils.*
import java.util.*
import kotlin.math.roundToInt
import kotlin.properties.Delegates


class FlashGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlashGameBinding
    private var currentPosition = 0
    private var points = 0
    private var numberOfQuestions = 0
    private lateinit var inputGame: Game
    private var randomNumber by Delegates.notNull<Int>()
    private val navigationArgs: FlashGameActivityArgs by navArgs()
    private var currentDistance = 0
    private lateinit var gameConfHelper: GameHelper
    private val categoryWordList = mutableListOf<Word>()


    private lateinit var wordList: List<Word>
    private lateinit var englishWords: MutableList<String>



    private val startGPSTrackerActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val returnedGameHelper = result.data?.parcelable<GameHelper>("GAME_HELPER")
                binding.pbQuestions.progress = 0
                currentPosition = 0
                currentDistance += returnedGameHelper?.breakDistance ?: gameConfHelper.breakDistance
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        inputGame = navigationArgs.game
        gameConfHelper = GameHelper(inputGame.distanceAfterTest, inputGame.distance)
        binding.tvCategoryName.text = inputGame.category
        setupButtons()

        WordList.wordList.forEach {word ->
            if(word.category == inputGame.category) categoryWordList.add(word)
        }
    }

    private fun setupButtons() {
        binding.btnNo.setOnClickListener {
            goToNextQuestion()
        }
        binding.btnYes.setOnClickListener {
            points++
            goToNextQuestion()
        }
    }

    override fun onStart() {
        super.onStart()
        englishWords = wordList.map { it.engName }.toMutableList()
        setupRandomFlashcards()
    }

    private fun goToNextQuestion() {
        randomizeNumber()
        currentPosition++
        numberOfQuestions++
        if (currentPosition < numberOfQuestions) {
            binding.tvFlash.text = categoryWordList[randomNumber].engName
            binding.pbQuestions.progress += ((1.0 / numberOfQuestions) * 100).toInt()
        }
        if (currentPosition < inputGame.questionsPerTest) {
            getQuestionAndUpdateProgressBar()
        } else if (currentDistance < inputGame.distance) {
            trackDistance()
        } else {
            finishGame()
        }
    }

    private fun randomizeNumber() {
        randomNumber = Random().nextInt(categoryWordList.size - 1)
    }

    private fun getQuestionAndUpdateProgressBar(){
        binding.tvFlash.text = englishWords[randomNumber]
        binding.pbQuestions.progress += ((1.0 / inputGame.questionsPerTest) * 100).toInt()
    }

    private fun trackDistance(){
        val intent = Intent(this, GPSTracker::class.java)
        intent.putExtra("GAME_HELPER", gameConfHelper)
        startGPSTrackerActivity.launch(intent)
    }

    private fun finishGame(){
        binding.pbQuestions.progress = 100
        val accuracy = (points.toFloat() / numberOfQuestions.toFloat())*100.0
        accuracy.roundToInt()
        val session = Session(0, inputGame, accuracy, numberOfQuestions, OffsetDateTime.now())
        val popupActivity = FinishScreenActivity(this, session)
        popupActivity.show()
    }


    private fun setupRandomFlashcards() {
        randomizeNumber()
        binding.tvFlash.text = categoryWordList[randomNumber].engName
    }
}
