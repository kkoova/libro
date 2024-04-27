package com.korobeynikova.libro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.korobeynikova.libro.databinding.FragmentReadBookBinding

class ReadBook : Fragment() {

    private lateinit var binding: FragmentReadBookBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var chapterAdapter: ChapterAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentReadBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookPath = arguments?.getString("bookPath")
        fetchBookTextFromFirebase(bookPath.toString())
    }

    private fun fetchBookTextFromFirebase(path: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        databaseReference.child(path).child("text").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (binding.progressBar != null) {
                    binding.progressBar.visibility = View.GONE
                }
                if (snapshot.exists()) {
                    val bookText = snapshot.getValue(String::class.java)
                    val chapters = splitTextIntoChapters(bookText)
                    updateRecyclerView(chapters)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка при выполнении запроса", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun splitTextIntoChapters(text: String?): List<String> {
        val chapters = mutableListOf<String>()

        text?.let {
            val chapterRegex = Regex("Глава \\d+")

            val matches = chapterRegex.findAll(text)
            var prevChapterIndex = 0

            matches.forEach { matchResult ->
                val chapterIndex = matchResult.range.first
                val chapterText = text.substring(prevChapterIndex, chapterIndex).trim()
                chapters.add(chapterText)
                prevChapterIndex = chapterIndex
            }

            if (prevChapterIndex < text.length) {
                val lastChapterText = text.substring(prevChapterIndex).trim()
                chapters.add(lastChapterText)
            }
        }

        return chapters
    }

    private fun updateRecyclerView(chapters: List<String>) {
        chapterAdapter.setData(chapters)
    }
}