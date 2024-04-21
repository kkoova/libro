package com.korobeynikova.libro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.korobeynikova.libro.databinding.FragmentSettingsProfileBinding

class SettingsProfile : Fragment() {

    private lateinit var binding: FragmentSettingsProfileBinding
    private lateinit var  firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val logUotBtn = view.findViewById<ConstraintLayout>(R.id.exitLayout)
        val controller = findNavController()
        val exit = view.findViewById<ImageView>(R.id.exitImage)

        exit.setOnClickListener { controller.navigate(R.id.startLiginOrSign) }

        logUotBtn.setOnClickListener {
            firebaseAuth.signOut()
            Toast.makeText(requireContext(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, MainLog::class.java)
            startActivity(intent)
            MainActivity().finish()
        }

        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            database.child("users").child(uid).get()
                .addOnSuccessListener {
                    val login = it.child("username").value.toString()
                    val email = it.child("email").value.toString()
                    binding.loginText.text = login
                    binding.emailText.text = email
                }.addOnFailureListener {
                    //Toast.makeText(requireContext(), "Данные не были загружены", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Пользователь не вошел в аккаунт
            val intent = Intent(context, MainLog::class.java)
            startActivity(intent)
            MainActivity().finish()
        }
    }
}