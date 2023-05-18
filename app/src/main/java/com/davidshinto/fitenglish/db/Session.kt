package com.davidshinto.fitenglish.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davidshinto.fitenglish.Game
import java.time.OffsetDateTime

@Entity(tableName = "session_data_table")
data class Session(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "session_id")
    val id: Int,
    @ColumnInfo(name = "session_game")
    val game: Game,
    @ColumnInfo(name = "session_date")
    val date: OffsetDateTime)