package com.korobeynikova.libro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.korobeynikova.libro.databinding.FragmentLoginUpBinding

class LoginUp : Fragment() {

    private lateinit var binding: FragmentLoginUpBinding
    private lateinit var  firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        val exitBtn = view.findViewById<ImageButton>(R.id.exitImage)
        val controller = findNavController()
        exitBtn.setOnClickListener { controller.navigate(R.id.startSingOrLogin) }

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
                        controller.navigate(R.id.bookLibrary)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(),
                            "Вход не осуществлен", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}