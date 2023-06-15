package com.davidshinto.fitenglish.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.davidshinto.fitenglish.R
import com.davidshinto.fitenglish.db.Session

class HistorySessionRecyclerViewAdapter : RecyclerView.Adapter<HistorySessionHolder>() {
    private var sessionList = ArrayList<Session>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorySessionHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listGames = layoutInflater.inflate(R.layout.list_sessions, parent, false)
        return HistorySessionHolder(listGames)
    }

    override fun onBindViewHolder(holder: HistorySessionHolder, position: Int) {
        holder.bind(sessionList[position])
    }

    override fun getItemCount(): Int {
        return sessionList.size
    }

    fun setList(sessions: List<Session>) {
        sessionList.clear()
        sessionList.addAll(sessions)
        notifyDataSetChanged()
    }
}


class HistorySessionHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(session: Session) {
        val tvSessionName = view.findViewById<TextView>(R.id.tvSessionName)
        val tvAccuracy = view.findViewById<TextView>(R.id.tvAccuracy)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val date = "${session.date.dayOfMonth} / ${session.date.month} / ${session.date.year}"
        tvSessionName.text = "Session - " + session.id.toString()
        tvAccuracy.text = session.accuracy.toString() + "%"
        tvDate.text = date
    }
}