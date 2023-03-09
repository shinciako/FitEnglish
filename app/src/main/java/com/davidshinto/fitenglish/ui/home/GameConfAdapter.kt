package com.davidshinto.fitenglish.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.davidshinto.fitenglish.R

class GameConfAdapter : RecyclerView.Adapter<GameConfHolder>() {

    private val resources = listOf(
        R.drawable.en1,
        R.drawable.en2,
        R.drawable.en3,
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameConfHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listGameMode = layoutInflater.inflate(R.layout.list_game_mode, parent, false)
        return GameConfHolder(listGameMode)
    }

    override fun onBindViewHolder(holder: GameConfHolder, position: Int) {
        val item = resources[position % resources.size]
        holder.bind(item)
    }

    //Can issue memory leaks
    override fun getItemCount() = Int.MAX_VALUE
}

class GameConfHolder(private val view: View): RecyclerView.ViewHolder(view){
    fun bind(mode: Int){
        val ivMode = view.findViewById<ImageView>(R.id.ivMode)
        ivMode.setImageResource(mode)
    }
}
