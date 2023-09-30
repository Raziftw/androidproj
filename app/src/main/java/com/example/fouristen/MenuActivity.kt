package com.example.fouristen

import android.content.Context
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.text.Spanned
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit

class MenuActivity : AppCompatActivity(), View.OnClickListener {
    private val fileName:String = "level_preferences"
    private val fileNamePreferences by lazy{getSharedPreferences(fileName,Context.MODE_PRIVATE)}

    private lateinit var alertDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // ===== Find all the buttons =====
        val newGameButton = findViewById<AppCompatButton>(R.id.button_new_game)
        val continueButton = findViewById<AppCompatButton>(R.id.button_continue)
        val howToPlayButton = findViewById<AppCompatButton>(R.id.button_how_to_play)
        val levelsButton = findViewById<AppCompatButton>(R.id.button_levels)
        val aboutButton = findViewById<AppCompatButton>(R.id.button_about)

        // Set click listeners for all the buttons
        newGameButton.setOnClickListener(this)
        continueButton.setOnClickListener(this)
        howToPlayButton.setOnClickListener(this)
        levelsButton.setOnClickListener(this)
        aboutButton.setOnClickListener(this)
    }
    override fun onBackPressed() {
        //back to MainActivity
        val intent = Intent(this, MainActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_new_game -> {
                fileNamePreferences.edit {
                    putInt("level", 1)
                    putInt("maxLevel", 1)
                    putString("digit0","0")
                    putString("digit1","0")
                    putString("digit2","0")
                    putString("digit3","0")
                    for (level in 1 until 30){
                        remove("\"level${level}Duration\"")
                    }
                    apply()
                }
                // move to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            }
            R.id.button_continue -> {
                // Handle the "Continue" button click
                startActivity(Intent(this, MainActivity::class.java))
            }
            R.id.button_how_to_play -> {
                // Handle the "How to Play" button click
                showHowToPlayDialog(view)
            }
            R.id.button_levels -> {
                // Handle the "Levels" button click
                val levelActivityIntent = Intent(this,LevelActivity::class.java)
                startActivity(levelActivityIntent)
            }
            R.id.button_about -> {
                // Handle the "About" button click
                showAboutDialog(view)

            }
        }
    }

    private fun showAboutDialog(view: View) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_about, null)
        dialogBuilder.setView(dialogView)

        val howToPlayTextView = dialogView.findViewById<TextView>(R.id.text_view_about)
        val text: Spanned? = Html.fromHtml(view.context.getString(R.string.about_text),FROM_HTML_MODE_COMPACT)

        howToPlayTextView.text = text

        val closeButton = dialogView.findViewById<Button>(R.id.button_about_close)
        closeButton.setOnClickListener {
            alertDialog.dismiss()
        }

        val sendEmailButton = dialogView.findViewById<Button>(R.id.button_send_email)
        sendEmailButton.setOnClickListener {
            // Send email with a predefined address
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:contact@example.com") // Replace "contact@example.com" with the desired email address
            }
            startActivity(emailIntent)
        }
        alertDialog = dialogBuilder.create() // Initialize alertDialog here
        alertDialog.show()
    }

    private fun showHowToPlayDialog(view: View) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_how_to_play, null)
        dialogBuilder.setView(dialogView)

        val howToPlayTextView = dialogView.findViewById<TextView>(R.id.text_view_how_to_play)
        val text: Spanned? = Html.fromHtml(view.context.getString(R.string.how_to_play_text),FROM_HTML_MODE_COMPACT)

        howToPlayTextView.text = text

        val closeButton = dialogView.findViewById<Button>(R.id.button_how_to_play_close)
        closeButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog = dialogBuilder.create() // Initialize alertDialog
        alertDialog.show()
    }

}
