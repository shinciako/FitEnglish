package com.davidshinto.fitenglish

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.davidshinto.fitenglish.databinding.ActivityFlashGameBinding
import com.davidshinto.fitenglish.db.Session
import com.davidshinto.fitenglish.ui.home.modes.GPSTracker
import com.davidshinto.fitenglish.utils.Card
import com.davidshinto.fitenglish.utils.Word
import com.davidshinto.fitenglish.utils.WordList
import com.davidshinto.fitenglish.utils.parcelable
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime
import java.util.*
import kotlin.properties.Delegates


class FlashGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlashGameBinding
    private var currentPosition = 0
    private var numberOfQuestions by Delegates.notNull<Int>()
    private var points = 0
    private lateinit var inputGame: Game
    private var randomNumber by Delegates.notNull<Int>()
    private val navigationArgs: FlashGameActivityArgs by navArgs()
    private var currentDistance = 0
    private lateinit var gameConfHelper: GameHelper

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
        numberOfQuestions = inputGame.questionsPerTest
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
        if (currentPosition < numberOfQuestions) {
            binding.tvFlash.text = WordList.wordList[randomNumber].engName
            binding.pbQuestions.progress += ((1.0 / numberOfQuestions) * 100).toInt()
        } else if (currentDistance < inputGame.distance) {
            val intent = Intent(this, GPSTracker::class.java)
            intent.putExtra("GAME_HELPER", gameConfHelper)
            startGPSTrackerActivity.launch(intent)
        } else {
            val session = Session(0,inputGame, OffsetDateTime.now())
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun randomizeNumber() {
        randomNumber = Random().nextInt(WordList.wordList.size - 1)
    }

    override fun onStart() {
        setupRandomFlashcards()
        super.onStart()
    }

    private fun setupRandomFlashcards() {
        randomizeNumber()
        binding.tvFlash.text = WordList.wordList[randomNumber].engName
    }
}

@Parcelize
data class GameHelper(
    var breakDistance: Int,
    val totalDistance: Int,
    var nowDistance: Int = 0
) : Parcelable