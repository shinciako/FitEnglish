package com.davidshinto.fitenglish.ui.home.modes

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.davidshinto.fitenglish.databinding.ActivityFlashGameBinding
import com.davidshinto.fitenglish.db.Session
import com.davidshinto.fitenglish.utils.*
import java.time.OffsetDateTime
import java.util.*
import kotlin.math.roundToInt
import kotlin.properties.Delegates


class FlashGameActivity : AppCompatActivity(), SensorEventListener {

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

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var currentTiltDirection: TiltDirection? = null
    private var isCooldownActive = false
    private val cooldownDurationMillis: Long = 2000



    private enum class TiltDirection {
        LEFT,
        RIGHT
    }


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
        WordList.wordList.forEach {word ->
            if(word.category == inputGame.category) categoryWordList.add(word)
        }
        gameConfHelper = GameHelper(inputGame.distanceAfterTest, inputGame.distance)
        binding.tvCategoryName.text = inputGame.category
        setupButtons()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
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
        setupRandomFlashcards()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
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

    /**
     * Sets the random number from all variable words
     */
    private fun randomizeNumber() {
        randomNumber = Random().nextInt(categoryWordList.size - 1)
    }

    private fun getQuestionAndUpdateProgressBar(){
        binding.tvFlash.text = categoryWordList[randomNumber].engName
        binding.pbQuestions.progress = ((currentPosition.toDouble() / inputGame.questionsPerTest) * 100.0).toInt()
    }

    private fun trackDistance(){
        val intent = Intent(this, GPSTracker::class.java)
        intent.putExtra("GAME_HELPER", gameConfHelper)
        startGPSTrackerActivity.launch(intent)
    }

    /**
     * Finishes the game
     */
    private fun finishGame(){
        binding.pbQuestions.progress = 100
        val accuracy = (points.toFloat() / numberOfQuestions.toFloat())*100.0
        accuracy.roundToInt()
        val session = Session(
            id = 0,
            accuracy = accuracy,
            numberOfQuestions = numberOfQuestions,
            date = OffsetDateTime.now(),
            mode = inputGame.mode,
            category = inputGame.category,
            distance = inputGame.distance,
        )

        val popupActivity = FinishScreenActivity(this, session)
        popupActivity.show()
    }


    private fun setupRandomFlashcards() {
        randomizeNumber()
        binding.tvFlash.text = categoryWordList[randomNumber].engName
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val x = it.values[0]
                val tiltThreshold = 1.5f

                if (x > tiltThreshold && currentTiltDirection != TiltDirection.RIGHT) {
                    currentTiltDirection = TiltDirection.RIGHT
                    handleTiltRight()
                } else if (x < -tiltThreshold && currentTiltDirection != TiltDirection.LEFT) {
                    currentTiltDirection = TiltDirection.LEFT
                    handleTiltLeft()
                }
            }
        }
    }

    private fun handleTiltRight() {
        if (!isCooldownActive) {
            points++
            goToNextQuestion()
            startCooldown()
        }
    }

    private fun handleTiltLeft() {
        if (!isCooldownActive) {
            goToNextQuestion()
            startCooldown()
        }
    }

    private fun startCooldown() {
        isCooldownActive = true
        Handler(Looper.getMainLooper()).postDelayed({
            isCooldownActive = false
        }, cooldownDurationMillis)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //no impl needed
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
}
