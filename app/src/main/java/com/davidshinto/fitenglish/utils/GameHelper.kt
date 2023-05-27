package com.davidshinto.fitenglish.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameHelper(
    var breakDistance: Int,
    val totalDistance: Int,
    var nowDistance: Int = 0
) : Parcelable