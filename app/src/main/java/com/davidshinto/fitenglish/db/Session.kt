package com.davidshinto.fitenglish.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davidshinto.fitenglish.utils.Game
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime

@Parcelize
@Entity(tableName = "session_data_table")
data class Session(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "session_id")
    val id: Int,
    @ColumnInfo(name = "session_game")
    val game: Game,
    @ColumnInfo(name = "session_accuracy")
    val accuracy: Double,
    @ColumnInfo(name = "session_numberOfQuestions")
    val numberOfQuestions: Int,
    @ColumnInfo(name = "session_date")
    val date: OffsetDateTime) : Parcelable