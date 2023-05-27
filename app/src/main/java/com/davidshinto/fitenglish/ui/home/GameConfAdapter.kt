package com.davidshinto.fitenglish.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.davidshinto.fitenglish.utils.Mode
import com.davidshinto.fitenglish.R

class GameConfAdapter : RecyclerView.Adapter<GameConfHolder>() {

    private val resources = listOf(
        GameMode(R.drawable.en1, Mode.Flash),
        GameMode(R.drawable.en2, Mode.Writer),
        GameMode(R.drawable.en3, Mode.Match),
    )

    var currentItem = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameConfHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listGameMode = layoutInflater.inflate(R.layout.list_game_mode, parent, false)
        return GameConfHolder(listGameMode)
    }

    override fun onBindViewHolder(holder: GameConfHolder, position: Int) {
        val item = resources[position % resources.size]
        holder.bind(item)
    }

    override fun getItemCount() = resources.size*30

    fun getCurrentGameMode(): Mode {
        return resources[currentItem % resources.size].mode
    }
}

class GameConfHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(gameMode: GameMode) {
        val ivMode = view.findViewById<ImageView>(R.id.ivMode)
        ivMode.setImageResource(gameMode.image)
    }
}

data class GameMode(val image: Int, val mode: Mode)