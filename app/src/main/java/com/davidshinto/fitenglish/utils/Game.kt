package com.davidshinto.fitenglish.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Game(
    val id: Int,
    val mode: Mode,
    val category: @RawValue Category,
    val distance: Int,
    val distanceAfterTest: Int,
    val questionsPerTest: Int
) : Parcelable

enum class Mode {
    Match, Flash, Writer
}