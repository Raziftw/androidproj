package com.example.fouristen

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaPlayer
import androidx.core.content.ContextCompat
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {
    //Shared Preferences:
    private val fileName: String = "level_preferences"
    private val fileNamePreferences by lazy { getSharedPreferences(fileName, Context.MODE_PRIVATE) }

    private lateinit var alertDialog: AlertDialog

    var mediaPlayer: MediaPlayer? = null

    //views containers:
    private lateinit var operationPlaces: Array<Place>
    private lateinit var digits: Array<Digit>
    private lateinit var operations: Array<Operation>
    private lateinit var mathExpressionContainer: ConstraintLayout

    private lateinit var resetButton: Button
    private lateinit var checkButton: Button
    private lateinit var hintButton: Button

    private lateinit var resultTextView: TextView
    private lateinit var levelTextView: TextView
    private lateinit var timerTextView: TextView

    private lateinit var menuButton: ImageView
    private lateinit var muteButton: ImageView
    private lateinit var unMuteButton: ImageView

    //Timer variables
    private val timerHandler = android.os.Handler()
    private var timerRunnable: Runnable? = null
    private var levelStartTime: Long = 0
    private var levelDuration: Long = 0

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // === Find views:===

        resetButton = findViewById(R.id.button_reset)
        checkButton = findViewById(R.id.button_check)
        hintButton = findViewById(R.id.button_hint)

        resultTextView = findViewById(R.id.text_view_result_display)
        levelTextView = findViewById(R.id.text_view_level)
        timerTextView = findViewById(R.id.text_view_timer)

        menuButton = findViewById(R.id.image_view_menu_button)
        muteButton = findViewById(R.id.image_view_mute_button)
        unMuteButton = findViewById(R.id.image_view_unmute_button)

        operations = arrayOf(
            Operation(findViewById(R.id.text_view_plus)),
            Operation(findViewById(R.id.text_view_minus)),
            Operation(findViewById(R.id.text_view_multiply)),
            Operation(findViewById(R.id.text_view_divide)),
            Operation(findViewById(R.id.text_view_open_bracket)),
            Operation(findViewById(R.id.text_view_close_bracket))
        )

        operationPlaces = arrayOf(
            Place(findViewById(R.id.text_view_operation_place1)),
            Place(findViewById(R.id.text_view_operation_place2)),
            Place(findViewById(R.id.text_view_operation_place3)),
            Place(findViewById(R.id.text_view_operation_place4)),
            Place(findViewById(R.id.text_view_operation_place5)),
            Place(findViewById(R.id.text_view_operation_place6)),
            Place(findViewById(R.id.text_view_operation_place7)),
            Place(findViewById(R.id.text_view_operation_place8))
        )

        digits = arrayOf(
            Digit(findViewById(R.id.text_view_digit_place1)),
            Digit(findViewById(R.id.text_view_digit_place2)),
            Digit(findViewById(R.id.text_view_digit_place3)),
            Digit(findViewById(R.id.text_view_digit_place4))
        )

        //*********************************  Main Activity Logic  **************************************************************************************
        mediaPlayerStartAndListen()
        // Initialize timer
        resetTimer()
        startTimer()

        val savedPreferences =
            applicationContext.getSharedPreferences(fileName, Context.MODE_PRIVATE)

        var level = getSavedLevel(savedPreferences)
        displayLevel(level)

        //first generate digits
        getSavedDigits(savedPreferences, level)

        //Listeners for buttons and Views
        menuButton.setOnClickListener {
            saveDigitsAndLevel(
                savedPreferences, digits[0].getText(), digits[1].getText(),
                digits[2].getText(), digits[3].getText(), level
            )
            val menuIntent = Intent(this, MenuActivity::class.java)
            startActivity(menuIntent)
        }

        resetButton.setOnClickListener {
            resetOperatorsOnly()
            checkTheResult(level)
        }

        hintButton.setOnClickListener {
            val solution = getSolution(level)
            showSolutionDialog(solution)
        }

        checkButton.setOnClickListener {
            if (checkButton.text.toString() == "NEXT") {
                level++
                resetDigitsAndPlaces(level)
                fileNamePreferences.edit {
                    putInt("level", level)
                }
                displayLevel(level)
                checkTheResult(level)
                resetTimer()
                startTimer()
            } else {
                checkTheResult(level)
            }
        }

        //listen to  operation symbols
        for (operation in operations) {
            operation.textView.setOnClickListener() {
                setOperationListener(operation, operations)
            }
        }
        //listen to  digits
        for (digit in digits) {
            digit.setListener(digits)
        }

    }




    override fun onResume() {
        super.onResume()
        mediaPlayer?.start()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }


    //**************************************************************************
    //**************************  Functions  ***********************************
    //**************************************************************************

    private fun showSolutionDialog(solution: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_solution, null)
        dialogBuilder.setView(dialogView)

        val solutionTextView = dialogView.findViewById<TextView>(R.id.text_view_solution)
        val text = "Solution:\n $solution"

        solutionTextView.text = text

        val closeButton = dialogView.findViewById<Button>(R.id.button_solution_close)
        closeButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    //                      ===Timer Functions===
    private fun resetTimer() {
        timerRunnable = Runnable { updateTimer() }
        levelStartTime = System.currentTimeMillis()
    }
    private fun updateTimer() {
        val currentTime = System.currentTimeMillis()
        levelDuration = currentTime - levelStartTime

        // Update the UI with the elapsed time
        timerTextView.text = formatTime(levelDuration)

        // Continue updating the timer every second
        timerHandler.postDelayed(timerRunnable!!, 1000)
    }
    private fun startTimer() {
        timerHandler.postDelayed(timerRunnable!!, 0)
    }

    private fun stopTimer() {
        timerHandler.removeCallbacks(timerRunnable!!)
    }
    private fun formatTime(duration: Long): String {
        val seconds = (duration / 1000) % 60
        val minutes = (duration / (1000 * 60)) % 60
        val hours = (duration / (1000 * 60 * 60)) % 24

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    //  ---------------------------------------------------------------
    //                      ===MediaPlayer Function===
    private fun mediaPlayerStartAndListen() {
        mediaPlayer = MediaPlayer.create(this, R.raw.song1)
        mediaPlayer?.start()
        unMuteButton.visibility = View.GONE
        muteButton.visibility = View.VISIBLE

        unMuteButton.setOnClickListener {
            unMuteButton.visibility = View.GONE
            muteButton.visibility = View.VISIBLE
            mediaPlayer?.start()
        }
        muteButton.setOnClickListener {
            unMuteButton.visibility = View.VISIBLE
            muteButton.visibility = View.GONE
            mediaPlayer?.pause()
        }
    }
    //  ---------------------------------------------------------------

    private fun getSolution(level: Int):String {
        when (level) {
            in 1 until 10 -> {
                return findEasyExpression(
                    digits[0].getText().toInt(),
                    digits[1].getText().toInt(),
                    digits[2].getText().toInt(),
                    digits[3].getText().toInt()
                )
            }
            in 10 until 20 -> {
                return findMediumExpression(
                    digits[0].getText().toInt(),
                    digits[1].getText().toInt(),
                    digits[2].getText().toInt(),
                    digits[3].getText().toInt()
                )
            }
            else -> {
                return findHardExpression(
                    digits[0].getText().toInt(),
                    digits[1].getText().toInt(),
                    digits[2].getText().toInt(),
                    digits[3].getText().toInt()
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayLevel(level: Int) {
        levelTextView.text = "${level}/30"
    }
    private fun getSavedDigits(savedPreferences: SharedPreferences, level: Int) {
        val savedDigits = arrayOf(
            savedPreferences.getString("digit0", "0"),
            savedPreferences.getString("digit1", "0"),
            savedPreferences.getString("digit2", "0"),
            savedPreferences.getString("digit3", "0")
        )
        if (savedDigits[0] == "0") {
            resetDigitsAndPlaces(level)
        } else {
            resetOperatorsOnly()
            for ((index, digit) in digits.withIndex()) {
                digit.setText(savedDigits[index])
            }
        }
    }

    private fun saveDigitsAndLevel(
        savedPreferences: SharedPreferences,
        d0: String,
        d1: String,
        d2: String,
        d3: String,
        level: Int
    ) {
        val maxLevel = savedPreferences.getInt("maxLevel", 0)
        if (level > maxLevel) {
            fileNamePreferences.edit {
                putInt("maxLevel", level)
            }
        }
        fileNamePreferences.edit {
            putInt("level", level)
            putString("digit0", d0)
            putString("digit1", d1)
            putString("digit2", d2)
            putString("digit3", d3)
            apply()
        }
    }

    private fun getSavedLevel(savedPreferences: SharedPreferences) =
        savedPreferences.getInt("level", 1)

    private fun checkTheResult(currentLevel:Int) {
        val mathString: Float? = evaluateMathExpression(getMathExpression())
        if (mathString != null) {
            resultTextView.text = mathString.toString()
            if (mathString == 10.0f) {
                convertToNext(currentLevel)
            }
        } else {
            resultTextView.text = "?"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun convertToNext(currentLevel:Int) {
        stopTimer()
        checkButton.text = "NEXT"
        checkButton.setTextColor(Color.GREEN)
        saveTimer(currentLevel)
    }

    private fun saveTimer(level:Int) {
        // Save levelDuration to shared preferences
        fileNamePreferences.edit {
            putString("level${level}Duration", formatTime(levelDuration))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun convertToCheck() {
        checkButton.setTextColor(ContextCompat.getColor(this, R.color.digitPlaceTextColor))
        checkButton.text = "CHECK"
    }

    private fun resetDigitsAndPlaces(level: Int) {
        convertToCheck()
        for (place in operationPlaces) {
            place.cancelListener()
            place.setText("")
            place.hide()
        }
        //generate digits
        var digitsList: List<Int>? = null
        when (level) {
            in 1 until 10 -> {
                digitsList = generateEasyDigits()
            }
            in 10 until 20 -> {
                digitsList = generateMediumDigits()
            }
            else -> {
                digitsList = generateHardDigits()
            }
        }

        for ((index, digit) in digits.withIndex()) {
            digit.textView.text = digitsList[index].toString()
        }
    }

    private fun resetOperatorsOnly() {
        for (place in operationPlaces) {
            if (!(place.isDigit())) {
                place.cancelListener()
                place.setText("")
                place.hide()
            }
        }
    }


    private fun setOperationListener(clickedOperation: Operation, operations: Array<Operation>) {
        for (operation in operations) {//highlightOFF for previous clicks
            operation.highlightOFF()
        }
        clickedOperation.highlightOn()
        clickedOperation.showPlacesToInsertSymbol(
            mathExpressionContainerToPlaceArray(),
            clickedOperation,
            digits
        )
    }


    private fun getMathExpression(): String? {    // collect all inserted digits and operations from activity
        val mathString = StringBuilder("")
        mathExpressionContainer = findViewById(R.id.constraint_layout_math_expression_container)
        fun traverseViews(view: View) {
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    val child = view.getChildAt(i)
                    traverseViews(child)
                }
            } else if (view is TextView) {
                val text = view.text.toString()
                mathString.append(text)
            }
        }
        traverseViews(mathExpressionContainer)
        return mathString.toString()
    }

    private fun mathExpressionContainerToPlaceArray(): MutableList<Place> {
        mathExpressionContainer = findViewById(R.id.constraint_layout_math_expression_container)
        val expressionContainerArray: MutableList<Place> = mutableListOf()

        fun traverseViews(view: View) {
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    val child = view.getChildAt(i)
                    traverseViews(child)
                }
            } else if (view is TextView) {
                expressionContainerArray.add(Place(view))
            }
        }
        traverseViews(mathExpressionContainer)
        return expressionContainerArray
    }
}
