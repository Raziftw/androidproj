package com.example.fouristen

import androidx.core.content.ContextCompat
import android.widget.TextView
import androidx.cardview.widget.CardView

class Operation(var textView: TextView) {
    var cardView: CardView = textView.parent as CardView

    fun highlightOn() {
        val color = ContextCompat.getColor(textView.context, R.color.operationTextColorHighlight)
        textView.setTextColor(color)
    }

    fun highlightOFF() {
        val color = ContextCompat.getColor(textView.context, R.color.operationTextColor)
        textView.setTextColor(color)
    }

    fun getText(): String = textView.text.toString()
    fun setText(text: String) {
        textView.text = text
    }

    fun showPlacesToInsertSymbol(
        expressionPlaces: MutableList<Place>,
        chosenOperation: Operation,
        digits: Array<Digit>
    ) {

        var symbolToInsert = textView.text.toString()
        //get string of places and insert "_" instead of empty places:
        var expression = StringBuilder("")
        for (place in expressionPlaces) {
            var text = place.getText()
            if (text == "") {
                expression.append("_")
            } else {
                expression.append(text)
            }
        }
// hide all places
        hideAllOperationPlaces(expressionPlaces)

        when (symbolToInsert) {
            "+", "-", "x", "/" -> showPlacesIfBasicOperator(
                expressionPlaces,
                expression,
                chosenOperation
            )
            "(" -> showPlacesIfOpenBracket(expressionPlaces, expression, chosenOperation)
            ")" -> showPlacesIfCloseBracket(expressionPlaces, expression, chosenOperation)
        }
    }

    private fun hideAllOperationPlaces(expressionPlaces: MutableList<Place>) {
        for (place in expressionPlaces) {
            if (!place.isDigit() && place.isEmpty()) {
                place.cancelListener()
                place.hide()
            }
        }
    }

    private fun showPlacesIfBasicOperator(
        expressionPlaces: MutableList<Place>,
        expression: StringBuilder,
        chosenOperation: Operation
    ) {
        hideAllOperationPlaces(expressionPlaces)
        for (place in expressionPlaces) {
            place.hideBackground()
            place.cancelListener()
        }
        for (index in 1 until expression.length - 1) {
            if (
                !expression[index].isDigit()
                && ((expression[index - 1]) !in charArrayOf('(', '+', '-', 'x', '/'))
                && ((expression[index + 1]) !in charArrayOf(')', '+', '-', 'x', '/'))
            ) {
                if (expression[index + 1] == '_') {
                    expressionPlaces[index + 1].highlightOn()
                    expressionPlaces[index + 1].setListener(
                        chosenOperation,
                        expressionPlaces.toTypedArray()
                    )
                } else {
                    expressionPlaces[index].highlightOn()
                    expressionPlaces[index].setListener(
                        chosenOperation,
                        expressionPlaces.toTypedArray()
                    )
                }
            }
        }
    }

    private fun showPlacesIfOpenBracket(
        expressionPlaces: MutableList<Place>,
        expression: StringBuilder,
        chosenOperation: Operation
    ) {
        hideAllOperationPlaces(expressionPlaces)
        for (place in expressionPlaces) {
            place.hideBackground()
            place.cancelListener()
        }
        for (index in 0 until expression.length - 1) {
            if (!expression[index].isDigit()
                && (expression[index + 1].isDigit())
                && (!expression.contains("("))
                && (')' !in expression.slice(0 until index))
                && (index < expression.length - 3 && expression[index + 2] != ')')
            ) {
                expressionPlaces[index].highlightOn()
                expressionPlaces[index].setListener(
                    chosenOperation,
                    expressionPlaces.toTypedArray()
                )
            }
        }

    }

    private fun showPlacesIfCloseBracket(
        expressionPlaces: MutableList<Place>,
        expression: StringBuilder,
        chosenOperation: Operation
    ) {
        hideAllOperationPlaces(expressionPlaces)
        for (place in expressionPlaces) {
            place.hideBackground()
            place.cancelListener()
        }
        for (index in 1 until expression.length) {
            if (!expression[index].isDigit()
                && (expression[index - 1].isDigit())
                && (!expression.contains(")"))
                && ('(' !in expression.slice(index until expression.length - 1))
                && (index > 2 && expression[index - 2] != '(')
            ) {
                expressionPlaces[index].highlightOn()
                expressionPlaces[index].setListener(
                    chosenOperation,
                    expressionPlaces.toTypedArray()
                )
            }
        }
    }

}