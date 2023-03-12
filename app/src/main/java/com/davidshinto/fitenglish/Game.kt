package com.davidshinto.fitenglish

data class Game(val id: Int, val category: Category, val questionsPerTest: Int, val distance: Int,
val distanceAfterTest: Int, val mode: Mode)

enum class Mode{
    Match,
    Flash,
    Writer
}