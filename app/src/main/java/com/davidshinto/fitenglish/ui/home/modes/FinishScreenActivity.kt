package com.davidshinto.fitenglish.ui.home.modes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import com.davidshinto.fitenglish.MainActivity
import com.davidshinto.fitenglish.databinding.ActivityFinishScreenBinding
import com.davidshinto.fitenglish.db.Session

class FinishScreenActivity(context: Context, private val session: Session) :
    AppCompatDialog(context) {
    private lateinit var binding: ActivityFinishScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishScreenBinding.inflate(layoutInflater)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)
        binding.tvFinishScreenCategory.text = "Category - " + session.game.category
        binding.tvFinishScreenMode.text = "Mode - " + session.game.mode.name
        binding.tvFinishScreenAccuracy.text =
            "Accuracy - " + session.accuracy.toInt().toString() + "%"
        binding.tvFinishScreenDistance.text = "Distance - " + session.game.distance.toString()
        binding.tvFinishScreenNumberQuestions.text =
            "Number of questions - " + session.numberOfQuestions.toString()

        binding.btnSaveSession.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            dismiss()
        }
    }
}