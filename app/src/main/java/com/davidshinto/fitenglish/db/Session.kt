package com.davidshinto.fitenglish.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davidshinto.fitenglish.utils.Mode
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime

@Parcelize
@Entity(tableName = "session_data_table")
data class Session(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "session_id")
    val id: Int,

    @ColumnInfo(name = "session_accuracy")
    val accuracy: Double,

    @ColumnInfo(name = "session_numberOfQuestions")
    val numberOfQuestions: Int,

    @ColumnInfo(name = "session_date")
    val date: OffsetDateTime,

    @ColumnInfo(name = "session_mode")
    val mode: Mode,

    @ColumnInfo(name = "session_category")
    val category: String,

    @ColumnInfo(name = "session_distance")
    val distance: Int,

) : Parcelable
