package com.example.fouristen

class Level(var levelNumber: Int) {

    var image: Int = putImage(levelNumber)
    var isPassed: Boolean = false
    var timer:String = ""

    private fun putImage(levelNumber: Int): Int {
        return when (levelNumber) {
            in 1 until 10 -> return R.drawable.level_plus_minus
            in 10 until 20 -> return R.drawable.level_plus_minus_mult_div
            in 20 until 30 -> return R.drawable.level_plus_minus_mult_div_brackets
            else -> {
                R.drawable.level_plus_minus_mult_div_brackets
            }
        }
    }
}