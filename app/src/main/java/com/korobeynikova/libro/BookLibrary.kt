package com.korobeynikova.libro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.korobeynikova.libro.databinding.FragmentBookLibraryBinding
import com.korobeynikova.libro.databinding.FragmentLoginUpBinding

class BookLibrary : Fragment() {

    private lateinit var profileBtn: ImageView
    private lateinit var  firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentBookLibraryBinding
    private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileBtn = view.findViewById(R.id.profileBtn)
        firebaseAuth = FirebaseAuth.getInstance()

        buttonClick()
    }
    private fun buttonClick(){

        val container = findNavController()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // Пользователь вошел в аккаунт
            database = Firebase.database.reference
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            database.child("users").child(uid).get()
                .addOnSuccessListener {
                    val login = it.child("username").value.toString()
                    val stars = it.child("stars").value.toString()
                    binding.starsCount.text = stars
                    binding.helloTextLibr.text = "Привет $login"
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Данные не были загруженны", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Пользователь не вошел в аккаунт
            container.navigate(R.id.start2)
        }

        profileBtn.setOnClickListener{
            container.navigate(R.id.profile)
        }
    }
}