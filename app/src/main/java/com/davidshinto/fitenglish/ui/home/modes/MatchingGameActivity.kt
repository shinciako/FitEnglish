package com.davidshinto.fitenglish.ui.home.modes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davidshinto.fitenglish.R
import com.davidshinto.fitenglish.utils.Flashcard

class MatchingGameActivity : AppCompatActivity() {

    private lateinit var englishWordsAdapter: EnglishWordsAdapter
    private lateinit var polishWordsAdapter: PolishWordsAdapter
    private var selectedEnglishWord: String = ""
    private var selectedPolishWord: String = ""

    private val flashcards = listOf(
        Flashcard("kot", "cat"),
        Flashcard("pies", "dog"),
        Flashcard("kawa", "coffee")
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_game)
        val polishWords = flashcards.map { it.polishWord }.toMutableList()
        val englishWords = flashcards.map { it.englishWord }.toMutableList()

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
            val selectedEnglishWordTranslation = flashcards.find { it.englishWord == selectedEnglishWord }?.polishWord
            if (selectedPolishWord == selectedEnglishWordTranslation) {
                Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show()
                englishWordsAdapter.removeWord(selectedEnglishWord)
                polishWordsAdapter.removeWord(selectedPolishWord)
            } else {
                Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT).show()
            }
            selectedEnglishWord = ""
            selectedPolishWord = ""
            englishWordsAdapter.resetSelection()
            polishWordsAdapter.resetSelection()
        }
    }
}
