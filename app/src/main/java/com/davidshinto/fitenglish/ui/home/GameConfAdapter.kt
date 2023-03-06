package com.davidshinto.fitenglish.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.davidshinto.fitenglish.R

class GameConfAdapter : RecyclerView.Adapter<GameConfHolder>() {

    private val numbers = listOf(1,2,3)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameConfHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listGameMode = layoutInflater.inflate(R.layout.list_game_mode, parent, false)
        return GameConfHolder(listGameMode)
    }

    override fun onBindViewHolder(holder: GameConfHolder, position: Int) {
        val item = numbers[position % numbers.size]
        holder.bind(item)
    }

    //Can issue memory leaks
    override fun getItemCount() = Int.MAX_VALUE
}

class GameConfHolder(private val view: View): RecyclerView.ViewHolder(view){
    fun bind(selected: Int){
        val tvInt = view.findViewById<TextView>(R.id.tvMode)
        tvInt.text = selected.toString()
    }
}
