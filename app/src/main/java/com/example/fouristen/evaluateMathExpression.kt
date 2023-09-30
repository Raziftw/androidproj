package com.example.fouristen

import android.util.Log
import java.util.*


// evaluate math expression

fun evaluateMathExpression(expression: String?): Float? {
    if(hasEmptySpaces(expression)){
        return null
    }
    val postfixExpression = try {
        infixToPostfix(expression)
    } catch (e: Exception) {
        Log.e("evaluateMathExpression problem","infixToPostfix crashed:|$expression|")
        return null
    }
    return try {
        evaluatePostfix(postfixExpression)
    } catch (e: Exception) {
        Log.d("evaluateMathExpression problem","infixToPostfix crashed:|$expression|")
        null
    }
}
private fun hasEmptySpaces(expression: String?): Boolean {
    val digitPattern = "\\d".toRegex()

    if (expression != null) {
        for (i in 0 until expression.length - 1) {
            val currentChar = expression[i]
            val nextChar = expression[i + 1]

            if (currentChar.toString().matches(digitPattern) && nextChar.toString().matches(digitPattern)) {
                return true
            }
        }
    }

    return false
}

private fun infixToPostfix(expression: String?): String {
    val postfix = StringBuilder()
    val operatorStack = Stack<Char>()

    if (expression != null) {
        for (char in expression) {
            when {
                char.isDigit() -> postfix.append(char)
                char == '(' -> operatorStack.push(char)
                char == ')' -> {
                    while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                        postfix.append(operatorStack.pop())
                    }
                    operatorStack.pop() // Discard the '('
                }
                else -> {
                    while (!operatorStack.isEmpty() && getPrecedence(char) <= getPrecedence(operatorStack.peek())) {
                        postfix.append(operatorStack.pop())
                    }
                    operatorStack.push(char)
                }
            }
        }
    }

    while (!operatorStack.isEmpty()) {
        postfix.append(operatorStack.pop())
    }

    return postfix.toString()
}

private fun getPrecedence(operator: Char): Int {
    return when (operator) {
        '+', '-' -> 1
        'x', '/' -> 2
        else -> 0
    }
}

private fun evaluatePostfix(postfixExpression: String): Float {
    val operandStack = Stack<Float>()

    for (char in postfixExpression) {
        if (char.isDigit()) {
            operandStack.push(char.toString().toFloat())
        } else {
            val operand2 = operandStack.pop()
            val operand1 = operandStack.pop()
            val result = when (char) {
                '+' -> operand1 + operand2
                '-' -> operand1 - operand2
                'x' -> operand1 * operand2
                '/' -> operand1 / operand2
                else -> throw IllegalArgumentException("Invalid operator: $char")
            }
            operandStack.push(result)
        }
    }

    return operandStack.pop()
}
