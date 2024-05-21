package com.korobeynikova.libro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.korobeynikova.libro.databinding.FragmentStartLiginOrSignBinding

class StartLiginOrSign : Fragment() {

    private lateinit var binding: FragmentStartLiginOrSignBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartLiginOrSignBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val container = findNavController()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Конфигурация Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Замените на ваш идентификатор клиента
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.logBtn.setOnClickListener {
            container.navigate(R.id.loginUpLibro)
        }
        binding.signBtn.setOnClickListener {
            container.navigate(R.id.signUpLibro)
        }
        // Обработчик нажатия на кнопку Google Sign-In
        binding.googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
    }
    private fun signInWithGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                Log.w("SignUpLibro", "Google sign in failed", e)
                Toast.makeText(requireContext(), "Google sign in failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d("SignUpLibro", "firebaseAuthWithGoogle:" + account.id!!)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("SignUpLibro", "signInWithCredential:success")
                    val user = auth.currentUser
                    if (user != null) {
                        val userReference = database.reference.child("users").child(user.uid)
                        userReference.get().addOnCompleteListener { snapshotTask ->
                            if (!snapshotTask.result.exists()) {
                                val userData = hashMapOf(
                                    "username" to user.displayName,
                                    "email" to user.email,
                                    "stars" to 100
                                )
                                userReference.setValue(userData)
                                    .addOnSuccessListener {
                                        Log.d("FirebaseDebug", "Данные успешно записаны в базу данных")
                                        Toast.makeText(requireContext(), "Регистрация с Google прошла успешно", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(context, MainActivity::class.java)
                                        startActivity(intent)
                                        MainLog().finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(requireContext(), "Ошибка при записи данных в базу данных: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Log.d("FirebaseDebug", "Пользователь уже существует в базе данных")
                                Toast.makeText(requireContext(), "Добро пожаловать", Toast.LENGTH_SHORT).show()
                                val intent = Intent(context, MainActivity::class.java)
                                startActivity(intent)
                                MainLog().finish()
                            }
                        }
                    }
                } else {
                    Log.w("SignUpLibro", "signInWithCredential:failure", task.exception)
                    Toast.makeText(requireContext(), "Аутентификация с Google не удалась.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}