package com.davidshinto.fitenglish

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.davidshinto.fitenglish.databinding.ActivityFlashGameBinding


class FlashGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlashGameBinding
    private var currentPosition = 0
    private var numberOfQuestions = 0
    private var points = 0
    private lateinit var inputGame: Game


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
                val returnedGame = result.data?.getParcelableExtra<Game>("GAME")
                inputGame = returnedGame ?: inputGame
                Toast.makeText(this, "Game: ${inputGame.mode}", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputGame = intent.getParcelableExtra("GAME")!!
        numberOfQuestions = inputGame.questionsPerTest

        binding.tvCategoryName.text = inputGame.category.name
        binding.tvFlash.text = dummyFlashcards[0].englishWord

        setupButtons()
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
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
        currentPosition++
        if (currentPosition < numberOfQuestions) {
            binding.tvFlash.text = dummyFlashcards[currentPosition].englishWord
            binding.pbQuestions.progress += ((1.0 / numberOfQuestions) * 100).toInt()
        } else {
            Toast.makeText(this, "You finished the test!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, GPSTracker::class.java)
            intent.putExtra("gameOutput", inputGame)
            startGPSTrackerActivity.launch(intent)
        }
    }
}
