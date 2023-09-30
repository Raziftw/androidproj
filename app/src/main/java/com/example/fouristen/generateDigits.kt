package com.example.fouristen

import kotlin.random.Random

fun generateEasyDigits(): List<Int> {
    while (true) {
        val number1 = Random.nextInt(1, 10)
        val number2 = Random.nextInt(1, 10)
        val number3 = Random.nextInt(1, 10)
        val number4 = Random.nextInt(1, 10)

        val possibleSums = listOf(
            number1 + number2 + number3 + number4,
            number1 + number2 + number3 - number4,
            number1 + number2 - number3 + number4,
            number1 + number2 - number3 - number4,
            number1 - number2 + number3 + number4,
            number1 - number2 + number3 - number4,
            number1 - number2 - number3 + number4,
            number1 - number2 - number3 - number4
        )

        if (10 in possibleSums) {
            return listOf(number1, number2, number3, number4)
        }
    }
}

fun findHardExpression(number1: Int, number2: Int, number3: Int, number4: Int): String {
    val expression1 = "($number1 * $number2 - $number3) / $number4"
    val expression2 = "($number1 * $number2 + $number3) / $number4"

    if ((number1 * number2 - number3) / number4 == 10) {
        return expression1
    }

    if ((number1 * number2 + number3) / number4 == 10) {
        return expression2
    }

    return "Expression not found"
}

fun findMediumExpression(number1: Int, number2: Int, number3: Int, number4: Int): String {
    val expression1 = "$number1 * $number2 + $number3 - $number4"
    val expression2 = "$number1 * $number2 + $number3 + $number4"

    if (number1 * number2 + number3 - number4 == 10) {
        return expression1
    }

    if (number1 * number2 + number3 + number4 == 10) {
        return expression2
    }

    return "Expression not found"
}

fun findEasyExpression(a: Int, b: Int, c: Int, d: Int): String {
    val numbers = listOf(a, b, c, d)
    val operations = listOf("+", "-")

    // Generate all possible permutations of the numbers
    val numberPermutations = numbers.permutations()

    // Iterate through each permutation and check all combinations of operations
    for (permutation in numberPermutations) {
        val expression = StringBuilder()
        val combinations = operations.cartesianProduct(operations, operations)

        for (combination in combinations) {
            var result = permutation[0]
            expression.clear()
            expression.append(permutation[0].toString())

            for (i in 1 until permutation.size) {
                val operator = combination[i - 1]
                val operand = permutation[i]
                expression.append(" $operator $operand")

                if (operator == "+") {
                    result += operand
                } else {
                    result -= operand
                }
            }

            if (result == 10) {
                return expression.toString()
            }
        }
    }

    return "No expression found to obtain 10."
}

// Extension function to generate permutations of a list
fun <T> List<T>.permutations(): List<List<T>> {
    if (isEmpty()) return emptyList()
    if (size == 1) return listOf(this)
    val permutations = mutableListOf<List<T>>()
    val element = first()
    val subList = drop(1).permutations()
    for (perm in subList) {
        for (i in 0..perm.size) {
            val newPerm = perm.toMutableList()
            newPerm.add(i, element)
            permutations.add(newPerm)
        }
    }
    return permutations
}

// Extension function to generate the Cartesian product of multiple lists
fun <T> List<T>.cartesianProduct(vararg lists: List<T>): List<List<T>> {
    return if (lists.isEmpty()) {
        map { listOf(it) }
    } else {
        flatMap { element ->
            lists[0].cartesianProduct(*lists.drop(1).toTypedArray()).map { subList ->
                listOf(element) + subList
            }
        }
    }
}


//===Medium Mode===
fun generateMediumDigits(): List<Int> {
    var number1 = Random.nextInt(1, 6)
    var number2 = Random.nextInt(1, 6)
    var product = number1 * number2

    var operand1: Int
    var operand2: Int
    var result: Int
    var result2: Int

    val maxAttempts = 50
    var attempts = 0

    do {
        if (attempts > maxAttempts)
        {
            attempts = 0
            operand1 = Random.nextInt(1, 10)
            operand2 = Random.nextInt(1, 10)
            number1 = Random.nextInt(1, 6)
            number2 = Random.nextInt(1, 6)
            product = number1 * number2
        }

        operand1 = Random.nextInt(1, 10)
        operand2 = Random.nextInt(1, 10)
        result = product + operand1 - operand2
        result2 = product + operand1 + operand2
        if ((result == 10)|| (result2==10))
            break
        attempts++
    } while (result != 10)

    return listOf(number1, number2, operand1, operand2)
}

//===Hard Mode===
fun generateHardDigits(): List<Int> {
    var number1 = Random.nextInt(1, 10)
    var number2 = Random.nextInt(1, 10)
    var number3 = Random.nextInt(1, 10)
    var number4 = 1
    var result1: Int
    var result2: Int


    var flag = false


    do {
        result1 = number1 * number2 - number3
        result2 = number1 * number2 + number3

        if ((result1 == 10) || (result2 ==10))
        {
            number4 = 1
            break
        }
        if ((result1 == 20) || (result2 ==20))
        {
            number4 = 2
            break
        }
        if ((result1 == 30) || (result2 ==30))
        {
            number4 = 3
            break
        }
        if ((result1 == 40) || (result2 ==40))
        {
            number4 = 4
            break
        }
        if ((result1 == 50) || (result2 ==50))
        {
            number4 = 5
            break
        }
        if ((result1 == 60) || (result2 ==60))
        {
            number4 = 6
            break
        }
        if ((result1 == 70) || (result2 ==70))
        {
            number4 = 7
            break
        }
        if ((result1 == 80) || (result2 ==80))
        {
            number4 = 8
            break
        }
        if ((result1 == 90) || (result2 ==90))
        {
            number4 = 9
            break
        }
        number1 = Random.nextInt(1, 10)
        number2 = Random.nextInt(1, 10)
        number3 = Random.nextInt(1, 10)

    } while (!flag)

    return listOf(number1, number2, number3, number4)
}