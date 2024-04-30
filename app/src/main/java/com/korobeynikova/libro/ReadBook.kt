package com.korobeynikova.libro

import android.animation.ValueAnimator
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
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
    private var isMenuVisible = true
    private lateinit var scrollView: ScrollView
    private lateinit var bookText: RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentReadBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scrollView = view.findViewById(R.id.menu)
        bookText = view.findViewById(R.id.bookText)
        val bookPath = arguments?.getString("bookPath")

        chapterAdapter = ChapterAdapter(emptyList())
        recyclerView = binding.bookText
        recyclerView.adapter = chapterAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        fetchBookTextFromFirebase(bookPath.toString())

        binding.noMenuImage.setOnClickListener {
            toggleMenu()
        }
    }

    private fun toggleMenu() {
        val newWidth: Int
        val menuLayoutParams = scrollView.layoutParams

        if (!isMenuVisible) {
            newWidth = 70.dpToPx()
        } else {
            newWidth = 1.dpToPx()
        }

        val menuAnimation = ValueAnimator.ofInt(scrollView.width, newWidth)
        menuAnimation.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            menuLayoutParams.width = animatedValue
            scrollView.layoutParams = menuLayoutParams
        }
        menuAnimation.duration = 300
        menuAnimation.interpolator = AccelerateDecelerateInterpolator()

        menuAnimation.start()

        isMenuVisible = !isMenuVisible
    }
    fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
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
        val inflater = LayoutInflater.from(requireContext())
        val chapters = chapterAdapter.getData()
        for ((index, chapter) in chapters.withIndex()) {
            val chapterView = inflater.inflate(R.layout.chapter_template, null)

            val chapterTitleTextView = chapterView.findViewById<TextView>(R.id.chapterTitle)

            chapterTitleTextView.text = "Глава ${index + 1}"

            chapterMenu.addView(chapterView)
        }
    }


    private fun setChapterMenuListeners() {
        val chapters = chapterAdapter.getData()

        for ((index, _) in chapters.withIndex()) {
            val view = binding.chapterMenu.getChildAt(index)

            if (view is LinearLayout) {
                // Находим внутренний TextView в LinearLayout
                val textView = view.findViewById<TextView>(R.id.chapterTitle)

                textView.setOnClickListener {
                    smoothScrollToChapter(index)
                }
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