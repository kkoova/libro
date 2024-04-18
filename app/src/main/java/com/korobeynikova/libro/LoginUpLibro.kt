package com.korobeynikova.libro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.korobeynikova.libro.databinding.FragmentLoginUpLibroBinding

class LoginUpLibro : Fragment() {

    private lateinit var binding: FragmentLoginUpLibroBinding
    private lateinit var  firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginUpLibroBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val container = findNavController()

        val exit = view.findViewById<ImageView>(R.id.exitImage)

        exit.setOnClickListener { container.navigate(R.id.startLiginOrSign) }

        firebaseAuth = FirebaseAuth.getInstance()
        binding.textPassGo.setOnClickListener {
            if (binding.editTextTextEmailAddress.text.isEmpty()){
                Toast.makeText(requireContext(),
                    "Введите почту", Toast.LENGTH_SHORT).show()
            } else {
                firebaseAuth.sendPasswordResetEmail(binding.editTextTextEmailAddress.text.toString())
                Toast.makeText(requireContext(),
                    "Проверьте почту", Toast.LENGTH_SHORT).show()
            }
        }
        binding.loginUpBnt.setOnClickListener {
            if (binding.editTextTextEmailAddress.text.isEmpty() || binding.editTextTextPassword2.text.isEmpty())
            {
                Toast.makeText(requireContext(), "Пожалуйста заполните все поля", Toast.LENGTH_SHORT).show()
            } else {
                val emailUser = binding.editTextTextEmailAddress.text.toString()
                val passwordUser = binding.editTextTextPassword2.text.toString()

                firebaseAuth.signInWithEmailAndPassword(emailUser, passwordUser)
                    .addOnCanceledListener {
                        Toast.makeText(requireContext(),
                            "Вход был прерван", Toast.LENGTH_SHORT).show()
                    }
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(),
                            "Вы успешно вошли в аккаунт", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        MainLog().finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(),
                            "Вход не осуществлен", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}