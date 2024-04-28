package com.korobeynikova.libro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
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

                    createChapterMenu()
                    setChapterMenuListeners()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка при выполнении запроса", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createChapterMenu() {
        val chapterMenu = binding.chapterMenu
        chapterMenu.removeAllViews()

        val chapters = chapterAdapter.getData()
        for ((index, chapter) in chapters.withIndex()) {
            val chapterTextView = TextView(requireContext())
            chapterTextView.text = "Глава ${index + 1}"
            chapterTextView.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.special_grey))
            chapterTextView.setOnClickListener {
                chapterTextView.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.black))
            }
            chapterMenu.addView(chapterTextView)
        }
    }


    private fun setChapterMenuListeners() {
        val chapters = chapterAdapter.getData()

        for ((index, _) in chapters.withIndex()) {
            val chapterTextView = binding.chapterMenu.getChildAt(index) as TextView
            chapterTextView.setOnClickListener {
                smoothScrollToChapter(index)
            }
        }
    }

    private fun smoothScrollToChapter(position: Int) {
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        val smoothScroller = object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = position
        linearLayoutManager.startSmoothScroll(smoothScroller)
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
        return chapters
    }
    private fun updateRecyclerView(chapters: List<String>) {
        chapterAdapter.setData(chapters)
    }
}