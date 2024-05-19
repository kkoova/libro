package com.korobeynikova.libro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.korobeynikova.libro.databinding.FragmentSignUpLibroBinding

class SignUpLibro : Fragment() {

    private lateinit var binding: FragmentSignUpLibroBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpLibroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val container = findNavController()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val exit = view.findViewById<ImageView>(R.id.exitImage)

        exit.setOnClickListener { container.navigate(R.id.startLiginOrSign) }

        binding.textLoginGo.setOnClickListener {
            container.navigate(R.id.loginUpLibro)
        }

        binding.singUpBnt.setOnClickListener {
            val singUserEmail = binding.editTextTextEmailAddress.text.toString()
            val singUserPassword = binding.editTextTextPassword.text.toString()
            val singUserConfigPassword = binding.editTextTextPassword2.text.toString()
            val login = binding.editTextLogin.text.toString()

            if (singUserEmail.isEmpty() || singUserPassword.isEmpty() ||
                singUserConfigPassword.isEmpty() || login.isEmpty()) {
                Toast.makeText(requireContext(), R.string.fill_all_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (singUserPassword != singUserConfigPassword) {
                Toast.makeText(requireContext(), R.string.passwords_not_match, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(singUserEmail, singUserPassword)
                .addOnSuccessListener { authResult ->
                    val uid = authResult.user?.uid
                    val userReference = database.reference.child("users").child(uid!!)
                    val userData = hashMapOf(
                        "username" to login,
                        "email" to singUserEmail,
                        "stars" to 100,
                        "all" to 0,
                        "password" to singUserPassword
                    )

                    userReference.setValue(userData)
                        .addOnSuccessListener {
                            Log.d("FirebaseDebug", "Данные успешно записаны в базу данных")
                            auth.signInWithEmailAndPassword(singUserEmail, singUserPassword)
                                .addOnSuccessListener {
                                    Log.d("FirebaseDebug", "Пользователь успешно вошел в систему")
                                    Toast.makeText(requireContext(), R.string.successful_registration, Toast.LENGTH_SHORT).show()
                                    val intent = Intent(context, MainActivity::class.java)
                                    startActivity(intent)
                                    MainLog().finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("FirebaseDebug", "Ошибка при входе пользователя: ${e.localizedMessage ?: "Unknown error"}")
                                    Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirebaseDebug", "Ошибка при записи данных в базу данных: ${e.localizedMessage ?: "Unknown error"}")
                            Toast.makeText(requireContext(), e.localizedMessage ?: getString(R.string.registration_failed), Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), e.localizedMessage ?: getString(R.string.registration_failed), Toast.LENGTH_SHORT).show()
                }
        }
    }
}
