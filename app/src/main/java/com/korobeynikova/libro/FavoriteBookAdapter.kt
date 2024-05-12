package com.korobeynikova.libro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.korobeynikova.libro.databinding.BookLikeItemBinding

class FavoriteBookAdapter(
    private var books: List<Book>,
    private val listener: BookItemLikeClick
) : RecyclerView.Adapter<FavoriteBookAdapter.BookViewHolder>() {

    private val backgroundImages: IntArray = getBackgroundImagesArray()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = BookLikeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.bind(book)
        holder.itemView.setOnClickListener {
            listener.onBookItemLikeClick(book)
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }

    fun updateData(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }

    inner class BookViewHolder(private val binding: BookLikeItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.titleTextView.text = book.title
            val randomBackground = backgroundImages.random()
            binding.bookLikeItem.setBackgroundResource(randomBackground)
        }
    }
}

private fun getBackgroundImagesArray(): IntArray {
    return intArrayOf(R.drawable.fon__1, R.drawable.fon__2, R.drawable.fon__3, R.drawable.fon__4,
        R.drawable.fon__5, R.drawable.fon__6, R.drawable.fon__7, R.drawable.fon__8, R.drawable.fon__9,
        R.drawable.fon__10)
}