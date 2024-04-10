package com.korobeynikova.libro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController

class BookLibrary : Fragment() {

    private lateinit var profileBtn: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_library, container, false)

        profileBtn = view.findViewById(R.id.profileBtn)

        return view
    }

    private fun buttonClick(){

        val container = findNavController()

        profileBtn.setOnClickListener{
            container.navigate(R.id.profile)
        }
    }
}