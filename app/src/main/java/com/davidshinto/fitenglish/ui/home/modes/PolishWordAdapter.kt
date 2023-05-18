package com.davidshinto.fitenglish.ui.home.modes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.davidshinto.fitenglish.R

class PolishWordsAdapter(private val words: MutableList<String>, private val onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<PolishWordsAdapter.ViewHolder>() {

    private var selectedItemPosition: Int = RecyclerView.NO_POSITION

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordTextView: TextView = itemView.findViewById(R.id.wordTextView)
        val cardView: CardView = itemView.findViewById(R.id.cardView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val previousPosition = selectedItemPosition
                    selectedItemPosition = position

                    notifyItemChanged(position)
                    if (previousPosition != RecyclerView.NO_POSITION) {
                        notifyItemChanged(previousPosition)
                    }
                    onItemClick(words[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.flashcard_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.wordTextView.text = words[position]
        holder.cardView.setCardBackgroundColor(
            if (position == selectedItemPosition) {
                ContextCompat.getColor(holder.itemView.context, R.color.teal_200)
            } else {
                ContextCompat.getColor(holder.itemView.context, R.color.white)
            }
        )
    }

    fun removeWord(word: String) {
        val position = words.indexOf(word)
        if (position != -1) {
            words.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int {
        return words.size
    }

    fun resetSelection() {
        selectedItemPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }
}


