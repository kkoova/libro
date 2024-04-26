package com.korobeynikova.libro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class BookAdapter(private val books: List<Book>,
                  private val backgroundImages: IntArray,
                  private val listener: BookItemClickListener) : RecyclerView.Adapter<BookAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return books.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewBookTitle: TextView = itemView.findViewById(R.id.textViewBookTitle)

        fun bind(book: Book) {
            textViewBookTitle.text = book.title
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = books[position]
        holder.bind(book)
        holder.itemView.setBackgroundResource(backgroundImages.random())
        holder.itemView.setOnClickListener {
            listener.onBookItemClick(book)
        }
    }
}
