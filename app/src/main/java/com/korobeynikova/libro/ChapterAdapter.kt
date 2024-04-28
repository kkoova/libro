package com.korobeynikova.libro

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChapterAdapter(private var chapters: List<String>) :
    RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chapter_item, parent, false)
        return ChapterViewHolder(view)
    }

    inner class ChapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chapterTextView: TextView = itemView.findViewById(R.id.textBookRes)

        fun bind(chapter: String) {
            Log.d("ReadBook", "chapters2: $chapter")
            chapterTextView.text = chapter
        }
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapter = chapters[position]
        holder.bind(chapter)
    }

    override fun getItemCount(): Int {
        return chapters.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(chapters: List<String>) {
        this.chapters = chapters
        notifyDataSetChanged()
    }

    fun getData(): List<String> {
        return chapters
    }
}
