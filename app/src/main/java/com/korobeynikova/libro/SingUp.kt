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
import com.korobeynikova.libro.databinding.FragmentSingUpBinding

class SingUp : Fragment() {

    private lateinit var binding: FragmentSingUpBinding
    private lateinit var  firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSingUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        val exitBtn = view.findViewById<ImageButton>(R.id.exitImage)
        val controller = findNavController()
        exitBtn.setOnClickListener { controller.navigate(R.id.startSingOrLogin) }

        binding.textLoginGo.setOnClickListener {
            controller.navigate(R.id.loginUp)
        }

        binding.singUpBnt.setOnClickListener {
            if (binding.editTextTextEmailAddress.text.isEmpty() ||
                binding.editTextTextPassword.text.isEmpty() ||
                binding.editTextTextPassword2.text.isEmpty()){
                Toast.makeText(requireContext(), "Пожалуйста заполните все поля", Toast.LENGTH_SHORT).show()
            } else {
                val singUserEmail = binding.editTextTextEmailAddress.text.toString()
                val singUserPassword = binding.editTextTextPassword.text.toString()
                val singUserConfigPassword = binding.editTextTextPassword2.text.toString()

                if (singUserPassword == singUserConfigPassword){
                    firebaseAuth.createUserWithEmailAndPassword(singUserEmail, singUserPassword)
                        .addOnCanceledListener {
                            Toast.makeText(requireContext(),
                                "Регестрация была прервана", Toast.LENGTH_SHORT).show()
                        }
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(),
                                "Вы успешно зарегестрировались", Toast.LENGTH_SHORT).show()
                            firebaseAuth.signInWithEmailAndPassword(singUserEmail, singUserPassword)
                            controller.navigate(R.id.bookLibrary)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(),
                                e.toString(), Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}