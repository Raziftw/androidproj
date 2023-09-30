package com.example.fouristen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class LevelAdapter(private val levelList: List<Level>, var savedCurrentLevel: Int) :
    RecyclerView.Adapter<LevelAdapter.LevelViewHolder>() {

    private var listener: OnItemClickListener? = null

    override fun getItemCount(): Int {
        return levelList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_level, parent, false)
        return LevelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
        val level = levelList[position]
        holder.bind(level)

        holder.itemView.setOnClickListener {
            listener?.onItemClick(level)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class LevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val levelImage: ImageView = itemView.findViewById(R.id.image_view_level_icon)
        private val levelName: TextView = itemView.findViewById(R.id.text_view_level_title)
        private val levelIsPassed:TextView = itemView.findViewById(R.id.text_view_level_is_passed)
        private val levelItem:RelativeLayout = itemView.findViewById(R.id.relative_layout_item_level)

        @SuppressLint("SetTextI18n")
        fun bind(level: Level) {
            levelImage.setImageResource(level.image)
            levelName.text = "${itemView.context.getString(R.string.level)} ${level.levelNumber}"
            levelIsPassed.text = if (level.isPassed) "${itemView.context.getString(R.string.passed)} ${level.timer}" else ""

            // Reset element state in RecyclerView
            val color = ContextCompat.getColor(itemView.context, R.color.darkenedButtonColor)
            levelItem.setBackgroundColor(color)

            // Highlighting passed and current level
            if ((level.isPassed)) {
                val color = ContextCompat.getColor(itemView.context, R.color.regularButtonsColor)
                levelItem.setBackgroundColor(color)
            }
            if (level.levelNumber == savedCurrentLevel) {
                val selectedColor = ContextCompat.getColor(itemView.context, R.color.selectedButtonColor)
                levelItem.setBackgroundColor(selectedColor)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(level: Level)
    }
}