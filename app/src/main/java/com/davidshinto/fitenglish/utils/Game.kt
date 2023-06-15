package com.davidshinto.fitenglish.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Game(
    val id: Int,
    val mode: Mode,
    val category: String,
    val distance: Int,
    val distanceAfterTest: Int,
    val questionsPerTest: Int
) : Parcelable

enum class Mode {
    Match, Flash, Writer
}