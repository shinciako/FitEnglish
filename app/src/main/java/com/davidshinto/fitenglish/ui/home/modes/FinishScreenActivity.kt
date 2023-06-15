package com.davidshinto.fitenglish.ui.home.modes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import androidx.lifecycle.lifecycleScope
import com.davidshinto.fitenglish.MainActivity
import com.davidshinto.fitenglish.databinding.ActivityFinishScreenBinding
import com.davidshinto.fitenglish.db.Session
import com.davidshinto.fitenglish.db.SessionDatabase
import kotlinx.coroutines.launch

class FinishScreenActivity(context: Context, private val session: Session) :
    AppCompatDialog(context) {
    private lateinit var binding: ActivityFinishScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishScreenBinding.inflate(layoutInflater)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)
        binding.tvFinishScreenCategory.text = "Category - " + session.category
        binding.tvFinishScreenMode.text = "Mode - " + session.mode.name
        binding.tvFinishScreenAccuracy.text =
            "Accuracy - " + session.accuracy.toInt().toString() + "%"
        binding.tvFinishScreenDistance.text = "Distance - " + session.distance.toString()
        binding.tvFinishScreenNumberQuestions.text =
            "Number of questions - " + session.numberOfQuestions.toString()

        setupButton()
    }

    private fun setupButton() {
        binding.btnSaveSession.setOnClickListener {
            lifecycleScope.launch {
                val dao = SessionDatabase.getInstance(context).sessionDao()
                dao.insertSession(session)
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
                dismiss()
            }
        }
    }
}