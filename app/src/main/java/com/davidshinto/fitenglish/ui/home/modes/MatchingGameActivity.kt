package com.davidshinto.fitenglish.ui.home.modes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davidshinto.fitenglish.R
import com.davidshinto.fitenglish.utils.Flashcard
import com.davidshinto.fitenglish.utils.Word
import com.davidshinto.fitenglish.utils.WordList
import com.google.firebase.database.FirebaseDatabase

class MatchingGameActivity : AppCompatActivity() {

    private lateinit var englishWordsAdapter: EnglishWordsAdapter
    private lateinit var polishWordsAdapter: PolishWordsAdapter
    private var selectedEnglishWord: String = ""
    private var selectedPolishWord: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_game)
        val polishWords = WordList.wordList.map { it.polName }.toMutableList()
        val englishWords = WordList.wordList.map { it.engName }.toMutableList()

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
