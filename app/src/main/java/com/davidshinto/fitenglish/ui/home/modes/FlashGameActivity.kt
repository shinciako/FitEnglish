package com.davidshinto.fitenglish.ui.home.modes

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.davidshinto.fitenglish.databinding.ActivityFlashGameBinding
import com.davidshinto.fitenglish.db.Session
import com.davidshinto.fitenglish.utils.Card
import com.davidshinto.fitenglish.utils.Game
import com.davidshinto.fitenglish.utils.GameHelper
import com.davidshinto.fitenglish.utils.parcelable
import java.time.OffsetDateTime
import java.util.*
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


        binding.tvCategoryName.text = inputGame.category.name
        setupButtons()
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

    private fun goToNextQuestion() {
        randomizeNumber()
        currentPosition++
        numberOfQuestions++
        if (currentPosition < inputGame.questionsPerTest) {
            getQuestionAndUpdateProgressBar()
        } else if (currentDistance < inputGame.distance) {
            trackDistance()
        } else {
            finishGame()
        }
    }

    private fun randomizeNumber() {
        randomNumber = Random().nextInt(dummyFlashcards.size - 1)
    }

    private fun getQuestionAndUpdateProgressBar(){
        binding.tvFlash.text = dummyFlashcards[randomNumber].englishWord
        binding.pbQuestions.progress += ((1.0 / inputGame.questionsPerTest) * 100).toInt()
    }

    private fun trackDistance(){
        val intent = Intent(this, GPSTracker::class.java)
        intent.putExtra("GAME_HELPER", gameConfHelper)
        startGPSTrackerActivity.launch(intent)
    }

    private fun finishGame(){
        binding.pbQuestions.progress = 100
        val accuracy = (points.toFloat() / numberOfQuestions.toFloat())*100
        val session = Session(0, inputGame, accuracy, numberOfQuestions, OffsetDateTime.now())
        val popupActivity = FinishScreenActivity(this, session)
        popupActivity.show()
    }

    override fun onStart() {
        setupRandomFlashcards()
        super.onStart()
    }

    private fun setupRandomFlashcards() {
        randomizeNumber()
        binding.tvFlash.text = dummyFlashcards[randomNumber].englishWord
    }
}
