package com.korobeynikova.libro

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchBooksDialog(private val onSearch: (String?, String?, List<String>) -> Unit) : DialogFragment() {

    private lateinit var autoCompleteTitle: AutoCompleteTextView
    private lateinit var autoCompleteAuthor: AutoCompleteTextView
    private lateinit var database: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.card_add, container, false)

        autoCompleteTitle = view.findViewById(R.id.autoCompleteTitle)
        autoCompleteAuthor = view.findViewById(R.id.autoCompleteAuthor)
        val class11CheckBox: CheckBox = view.findViewById(R.id.class11CheckBox)
        val class10CheckBox: CheckBox = view.findViewById(R.id.class10CheckBox)
        val class9CheckBox: CheckBox = view.findViewById(R.id.class9CheckBox)
        val class8CheckBox: CheckBox = view.findViewById(R.id.class8CheckBox)
        val class7CheckBox: CheckBox = view.findViewById(R.id.class7CheckBox)
        val class6CheckBox: CheckBox = view.findViewById(R.id.class6CheckBox)
        val class5CheckBox: CheckBox = view.findViewById(R.id.class5CheckBox)

        database = FirebaseDatabase.getInstance().reference

        setupAutoCompleteTextView(autoCompleteTitle, "title")
        setupAutoCompleteTextView(autoCompleteAuthor, "author")

        val searchButton = view.findViewById<Button>(R.id.buttonSearch)
        searchButton.setOnClickListener {
            val title = if (autoCompleteTitle.text.isNotEmpty()) autoCompleteTitle.text.toString() else null
            val author = if (autoCompleteAuthor.text.isNotEmpty()) autoCompleteAuthor.text.toString() else null

            val classes = mutableListOf<String>()

            if (class11CheckBox.isChecked) classes.add("eleven")
            if (class10CheckBox.isChecked) classes.add("ten")
            if (class9CheckBox.isChecked) classes.add("nine")
            if (class8CheckBox.isChecked) classes.add("eight")
            if (class7CheckBox.isChecked) classes.add("seven")
            if (class6CheckBox.isChecked) classes.add("six")
            if (class5CheckBox.isChecked) classes.add("five")

            onSearch(title, author, classes)
            dismiss()
        }

        return view
    }

    private fun setupAutoCompleteTextView(autoCompleteTextView: AutoCompleteTextView, field: String) {
        val suggestions = mutableListOf<String>()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions)
        autoCompleteTextView.setAdapter(adapter)

        database.child("books").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                suggestions.clear()
                for (bookSnapshot in snapshot.children) {
                    val suggestion = bookSnapshot.child(field).getValue(String::class.java)
                    suggestion?.let {
                        if (!suggestions.contains(it)) {
                            suggestions.add(it)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибок
            }
        })

        autoCompleteTextView.threshold = 2 // Показывать предложения после ввода одного символа
    }
}