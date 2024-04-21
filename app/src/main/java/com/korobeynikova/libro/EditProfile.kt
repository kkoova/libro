package com.korobeynikova.libro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.korobeynikova.libro.databinding.FragmentEditProfileBinding

class EditProfile : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val controller = findNavController()
        val exit = view.findViewById<ImageView>(R.id.exitImage)
        val text = view.findViewById<TextView>(R.id.textView15)

        exit.setOnClickListener { controller.navigate(R.id.settingsProfile) }
        //text.text = "настройки"

        val currentUser = firebaseAuth.currentUser
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        if (currentUser != null) {
            database.child("users").child(uid).get()
                .addOnSuccessListener {
                    val login = it.child("username").value.toString()
                    val password = it.child("password").value.toString()
                    binding.loginTextText.setText(login)
                    binding.emalTextText.setText(password)
                }.addOnFailureListener {
                    //Toast.makeText(requireContext(), "Данные не были загружены", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Пользователь не вошел в аккаунт
            val intent = Intent(context, MainLog::class.java)
            startActivity(intent)
            MainActivity().finish()
        }

        binding.saveBtn.setOnClickListener {
            if (binding.emalTextText.text.isEmpty() || binding.loginTextText.text.isEmpty()){
                Toast.makeText(requireContext(), "Заполните поля", Toast.LENGTH_SHORT).show()
            } else {

                val login = binding.loginTextText.text.toString()
                val password = binding.emalTextText.text.toString().trim()

                var user = firebaseAuth.currentUser
                user?.updatePassword(password)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Адрес электронной почты успешно обновлен
                            Toast.makeText(requireContext(), "Пароль успешно изменен", Toast.LENGTH_SHORT).show()
                        } else {
                            // Ошибка при обновлении адреса электронной почты
                            Toast.makeText(requireContext(), "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                database.child("users").child(uid).child("username").setValue(login)
                database.child("users").child(uid).child("password").setValue(password)

                Toast.makeText(requireContext(), "Данные успешно сохранены", Toast.LENGTH_SHORT).show()
            }
        }

        binding.deliteBtn.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            user?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Аккаунт успешно удален
                        database.child("users").child(uid).removeValue()
                        Toast.makeText(requireContext(), "Аккаунт успешно удален", Toast.LENGTH_SHORT).show()
                        // Перенаправление на экран входа или другую подходящую страницу
                        val intent = Intent(requireContext(), MainLog::class.java)
                        startActivity(intent)
                        MainActivity().finish()
                    } else {
                        // Ошибка при удалении аккаунта
                        Toast.makeText(requireContext(), "Ошибка при удалении аккаунта: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
