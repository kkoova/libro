package com.korobeynikova.libro

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.korobeynikova.libro.databinding.FragmentStartBookBinding

class StartBook : Fragment() {


    private lateinit var binding: FragmentStartBookBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fonCont.setBackgroundResource(getBackgroundImagesArray().random())
        val bookTitle = arguments?.getString("bookTitle")

        if (bookTitle != null) {
            settings(bookTitle)
        }

        val exit = view.findViewById<ImageView>(R.id.exitImage)
        val like = view.findViewById<ImageView>(R.id.likeImage)
        val controller = findNavController()
        exit.setOnClickListener { controller.navigate(R.id.bookLibrary) }
    }

    private fun getBackgroundImagesArray(): IntArray {
        return intArrayOf(R.drawable.fon_book_1, R.drawable.fon_book_2, R.drawable.fon_book_3)
    }

    private fun settings(title: String) {
        binding.titleText.text = title
    }
}