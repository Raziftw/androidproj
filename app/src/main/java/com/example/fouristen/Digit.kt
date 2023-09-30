package com.example.fouristen

import android.graphics.Color
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat

class Digit(var textView: TextView) {
    var cardView: CardView = textView.parent as CardView
    var isHighlighted: Boolean = false
    companion object {
        var sharedText:String = ""
    }
    fun setText(text: String?) {
        textView.text = text
    }
    fun getText(): String {
        return this.textView.text.toString()
    }

    private fun highlightOn() {
        this.isHighlighted = true
        val backgroundColor = ContextCompat.getColor(
            cardView.context,
            R.color.operationPlaceCardViewBackgroundColor
        )
        cardView.setCardBackgroundColor(backgroundColor)
    }
    private fun hideBackground() {
        this.isHighlighted = false
        this.cardView.setCardBackgroundColor(Color.TRANSPARENT)
    }

    fun setListener(digits: Array<Digit>) {
        this.textView.setOnClickListener {

            if (!this.isHighlighted) {
                sharedText = this.getText()
                for (otherDigit in digits) {
                    if (otherDigit != this) {
                        otherDigit.highlightOn()
                    }
                }
            }else{
                for(selectedDigit in digits){
                    if(!selectedDigit.isHighlighted){
                        selectedDigit.setText(this.getText())
                        this.setText(sharedText)
                    }
                    selectedDigit.hideBackground()
                }
            }
        }
    }

    fun cancelListener() {
        this.isHighlighted = false
        this.cardView.setOnClickListener(null)
    }

}