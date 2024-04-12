package com.korobeynikova.libro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class BookLibrary : Fragment() {

    private lateinit var profileBtn: ImageView
    private lateinit var  firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_library, container, false)

        profileBtn = view.findViewById(R.id.profileBtn)
        firebaseAuth = FirebaseAuth.getInstance()

        buttonClick()

        return view
    }

    private fun buttonClick(){

        val container = findNavController()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // Пользователь вошел в аккаунт

        } else {
            // Пользователь не вошел в аккаунт
            container.navigate(R.id.start2)
        }

        profileBtn.setOnClickListener{
            container.navigate(R.id.profile)
        }
    }
}