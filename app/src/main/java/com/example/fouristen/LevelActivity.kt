package com.example.fouristen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView

class LevelActivity : AppCompatActivity(), LevelAdapter.OnItemClickListener {
    // Shared Preferences:
    private val fileName: String = "level_preferences"
    private lateinit var fileNamePreferences: SharedPreferences
    private var savedMaxLevel: Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level)

        fileNamePreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE)

        savedMaxLevel = getSavedMaxLevel(fileNamePreferences)
        val savedCurrentLevel = getSavedLevel(fileNamePreferences)

        val list: RecyclerView = findViewById(R.id.recycler_view_levels)

        val levels: MutableList<Level> = mutableListOf()
        for (levelNumber in 1..30) {
            val level = Level(levelNumber)
            if (levelNumber < savedMaxLevel) {
                level.timer = getSavedLevelTimer(fileNamePreferences,levelNumber).toString()
                level.isPassed = true
            }
            levels.add(level)
        }

        val adapter: LevelAdapter = LevelAdapter(levels, savedCurrentLevel)
        adapter.setOnItemClickListener(this)

        list.adapter = adapter
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        //back to MainActivity
        val intent = Intent(this, MainActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


    override fun onItemClick(level: Level) {
        // Handling clicks on RecyclerView(level) and pass to selected level (by saving him to shared preferences
        if (level.levelNumber <= savedMaxLevel) {
            fileNamePreferences.edit {
                putInt("level",level.levelNumber)
                apply()
            }
            startActivity(Intent(this, MainActivity::class.java))
        }else{
            Toast.makeText(this, "You haven't completed this level yet", Toast.LENGTH_SHORT).show()
        }
    }

    // Functions
    private fun getSavedLevel(savedPreferences: SharedPreferences) =
        savedPreferences.getInt("level", 1)

    private fun getSavedMaxLevel(savedPreferences: SharedPreferences): Int =
        savedPreferences.getInt("maxLevel", 1)
    private fun getSavedLevelTimer(savedPreferences: SharedPreferences,levelNumber: Int): String? {
        return savedPreferences.getString("level${levelNumber}Duration","kkm")
    }
}
