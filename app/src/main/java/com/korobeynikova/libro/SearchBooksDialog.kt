package com.korobeynikova.libro

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
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

    private val titlesList = mutableListOf<String>()
    private val authorsList = mutableListOf<String>()

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

        setupAutoCompleteTextView()

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

    private fun setupAutoCompleteTextView() {
        val titleAdapter = ArrayAdapter(requireContext(), R.layout.custom_dropdown_item, R.id.textViewItem, titlesList)
        autoCompleteTitle.setAdapter(titleAdapter)

        val authorAdapter = ArrayAdapter(requireContext(), R.layout.custom_dropdown_item, R.id.textViewItem, authorsList)
        autoCompleteAuthor.setAdapter(authorAdapter)

        database.child("books").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                titlesList.clear()
                authorsList.clear()
                for (bookSnapshot in snapshot.children) {
                    for (classSnapshot in bookSnapshot.children) {
                        val title = classSnapshot.child("title").getValue(String::class.java)
                        val author = classSnapshot.child("author").getValue(String::class.java)
                        title?.let {
                            if (!titlesList.contains(it)) {
                                titlesList.add(it)
                            }
                        }
                        author?.let {
                            if (!authorsList.contains(it)) {
                                authorsList.add(it)
                            }
                        }
                    }
                }
                titleAdapter.notifyDataSetChanged()
                authorAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка при загрузке данных", Toast.LENGTH_SHORT).show()
            }
        })

        autoCompleteTitle.threshold = 1
        autoCompleteAuthor.threshold = 1
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}
