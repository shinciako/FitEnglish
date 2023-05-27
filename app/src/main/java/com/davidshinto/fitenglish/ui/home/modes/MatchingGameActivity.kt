package com.davidshinto.fitenglish.ui.home.modes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davidshinto.fitenglish.R
import com.davidshinto.fitenglish.db.Session
import com.davidshinto.fitenglish.utils.*
import java.time.OffsetDateTime
import kotlin.math.roundToInt

class MatchingGameActivity : AppCompatActivity() {

    private lateinit var englishWordsAdapter: EnglishWordsAdapter
    private lateinit var polishWordsAdapter: PolishWordsAdapter
    private var selectedEnglishWord: String = ""
    private var selectedPolishWord: String = ""

    private var numberOfQuestions = 0
    private var negativePoints = 0
    private var currentDistance = 0
    private lateinit var gameConfHelper: GameHelper
    private lateinit var inputGame: Game
    private val navigationArgs: MatchingGameActivityArgs by navArgs()


    private val startGPSTrackerActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val returnedGameHelper = result.data?.parcelable<GameHelper>("GAME_HELPER")
                currentDistance += returnedGameHelper?.breakDistance ?: gameConfHelper.breakDistance
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_game)

        inputGame = navigationArgs.game
        gameConfHelper = GameHelper(inputGame.distanceAfterTest, inputGame.distance)
    }

    override fun onStart() {
        super.onStart()
        val wordList = WordList.wordList.shuffled().take(inputGame.questionsPerTest)
        val polishWords = wordList.map { it.polName }.toMutableList()
        val englishWords = wordList.map { it.engName }.toMutableList()


        polishWords.shuffle()
        englishWords.shuffle()

        val englishWordsRecyclerView = findViewById<RecyclerView>(R.id.englishWordsRecyclerView)
        englishWordsRecyclerView.layoutManager = LinearLayoutManager(this)
        englishWordsAdapter = EnglishWordsAdapter(englishWords) { word ->
            selectedEnglishWord = word
            checkAnswers()
        }
        englishWordsRecyclerView.adapter = englishWordsAdapter

        val polishWordsRecyclerView = findViewById<RecyclerView>(R.id.polishWordsRecyclerView)
        polishWordsRecyclerView.layoutManager = LinearLayoutManager(this)
        polishWordsAdapter = PolishWordsAdapter(polishWords) { word ->
            selectedPolishWord = word
            checkAnswers()
        }
        polishWordsRecyclerView.adapter = polishWordsAdapter
    }


    private fun checkAnswers() {
        if (selectedEnglishWord.isNotEmpty() && selectedPolishWord.isNotEmpty()) {
            val selectedEnglishWordTranslation = WordList.wordList.find { it.engName == selectedEnglishWord }?.polName
            if (selectedPolishWord == selectedEnglishWordTranslation) {
                numberOfQuestions++
                Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show()
                englishWordsAdapter.removeWord(selectedEnglishWord)
                polishWordsAdapter.removeWord(selectedPolishWord)
            } else {
                Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT).show()
                negativePoints++
            }
            selectedEnglishWord = ""
            selectedPolishWord = ""
            englishWordsAdapter.resetSelection()
            polishWordsAdapter.resetSelection()
        }
        if(englishWordsAdapter.itemCount == 0){
            if (currentDistance < inputGame.distance) {
                trackDistance()
            } else {
                finishGame()
            }
        }
    }

    private fun trackDistance(){
        val intent = Intent(this, GPSTracker::class.java)
        intent.putExtra("GAME_HELPER", gameConfHelper)
        startGPSTrackerActivity.launch(intent)
    }

    private fun finishGame(){
        val accuracy = ((numberOfQuestions - negativePoints).toFloat() / numberOfQuestions.toFloat())*100.0
        accuracy.roundToInt()
        val session = Session(0, inputGame, accuracy, numberOfQuestions, OffsetDateTime.now())
        val popupActivity = FinishScreenActivity(this, session)
        popupActivity.show()
    }
}
