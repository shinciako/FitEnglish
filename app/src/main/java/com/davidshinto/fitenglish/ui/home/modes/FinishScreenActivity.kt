package com.davidshinto.fitenglish.ui.home.modes

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import com.davidshinto.fitenglish.databinding.ActivityFinishScreenBinding
import com.davidshinto.fitenglish.db.Session

class FinishScreenActivity(activity: Activity, private val session: Session) : AppCompatDialog(activity) {
    private lateinit var binding : ActivityFinishScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvFinishScreenCategory.text = session.game.category.name
        binding.tvFinishScreenMode.text = session.game.mode.name
        binding.tvFinishScreenDistance.text = session.game.distance.toString()
        binding.tvFinishScreenMode.text = session.accuracy.toInt().toString() +"%"
        binding.tvFinishScreenNumberQuestions.text = session.numberOfQuestions.toString()
    }
}