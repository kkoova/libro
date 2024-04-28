package com.korobeynikova.libro

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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

        chapterAdapter = ChapterAdapter(emptyList())
        recyclerView = binding.bookText
        recyclerView.adapter = chapterAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchBookTextFromFirebase(bookPath.toString())

        createChapterMenu()

        setChapterMenuListeners()
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

    private fun createChapterMenu() {
        binding.chapterMenu.removeAllViews()

        val chapters = chapterAdapter.getData()
        for ((index, chapter) in chapters.withIndex()) {
            val chapterButton = Button(requireContext())
            chapterButton.text = "Глава ${index + 1}"
            binding.chapterMenu.addView(chapterButton)
        }
    }

    private fun setChapterMenuListeners() {
        val chapters = chapterAdapter.getData()

        for ((index, _) in chapters.withIndex()) {
            val chapterButton = binding.chapterMenu.getChildAt(index) as Button
            chapterButton.setOnClickListener {
                scrollToChapter(index)
            }
        }
    }

    private fun scrollToChapter(chapterIndex: Int) {
        recyclerView.scrollToPosition(chapterIndex)
    }

    private fun splitTextIntoChapters(text: String?): List<String> {
        val chapters = mutableListOf<String>()

        text?.let {

            val regex = Regex("(Глава \\d+)(.*?)(?=Глава \\d+|$)")
            val matches = regex.findAll(it)

            matches.forEach { matchResult ->
                chapters.add(matchResult.value.trim())
            }
        }
        Log.d("ReadBook", "int: $chapters")
        return chapters
    }
    private fun updateRecyclerView(chapters: List<String>) {
        chapterAdapter.setData(chapters)
    }
}