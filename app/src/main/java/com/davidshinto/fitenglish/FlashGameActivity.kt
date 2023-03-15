package com.davidshinto.fitenglish

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.davidshinto.fitenglish.databinding.ActivityFlashGameBinding


class FlashGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFlashGameBinding
    private val navigationArgs : FlashGameActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val input = navigationArgs.game
        binding.tvCategoryName.text = input.category.name
    }
}