package com.example.fouristen

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
class Place(var textView: TextView) {
    var cardView: CardView = textView.parent as CardView
    var isHighlighted: Boolean = false
    var isSelected: Boolean = false

    fun isEmpty(): Boolean {
        return textView.text.toString().isEmpty()
    }

    fun isDigit(): Boolean {
        val text = this.getText()
        if (text.isEmpty()) {
            return false
        }
        val digitsArray = arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        return text.all { digitsArray.contains(it) }
    }

    fun getText(): String {
        return this.textView.text.toString()
    }

    fun setText(text: String) {
        textView.text = text
    }

    fun highlightOn() {
        this.isHighlighted = true
        val backgroundColor = ContextCompat.getColor(
            cardView.context,
            R.color.operationPlaceCardViewBackgroundColor
        )
        cardView.visibility = View.VISIBLE
        cardView.setCardBackgroundColor(backgroundColor)
        this.textView.width = 50
    }

    fun hide() {
        this.isHighlighted = false
        this.cardView.visibility = View.GONE
    }

    fun hideBackground() {
        this.isHighlighted = false
        this.cardView.setCardBackgroundColor(Color.TRANSPARENT)
    }

    //------
    fun setListener(chosenOperation: Operation, operationPlaces: Array<Place>) {
        this.textView.setOnClickListener { view ->
            val digits = mutableListOf<Digit>()
            val text = chosenOperation.getText()
            chosenOperation.highlightOFF()
            if (text.isNotEmpty()) {
                chosenOperation.highlightOFF()
                (view as TextView).text = text
                for (place in operationPlaces) {
                    place.hideBackground()
                    place.hideIfEmpty()
                    place.cancelListener()
                    //let listen to digits
                    if(place.isDigit()){
                        digits.add(Digit(place.textView))
                    }
                }
            }
            for(digit in digits){
                digit.setListener(digits.toTypedArray())
            }
        }
    }


    fun cancelListener() {
        this.isHighlighted=false
        this.textView.setOnClickListener(null)
    }

    private fun hideIfEmpty() {
        if (this.getText() == "") {
            this.hide()
        }
    }
}