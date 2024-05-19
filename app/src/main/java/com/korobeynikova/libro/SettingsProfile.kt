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
    private lateinit var firebaseAuth: FirebaseAuth
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


        val deleteProfile = view.findViewById<ConstraintLayout>(R.id.deleteLayout)
        val exitProfile = view.findViewById<ConstraintLayout>(R.id.exitLayout)
        val editProfile = view.findViewById<ConstraintLayout>(R.id.editLayout)

        val exit = view.findViewById<ImageView>(R.id.exitImage)

        val controller = findNavController()

        editProfile.setOnClickListener {
            val dialog = MyDialogEdit()
            dialog.setButtons(
                "Сохранить",
                "Отмена",
                { pass: String, login: String ->
                    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
                    val database: DatabaseReference = FirebaseDatabase.getInstance().reference

                    val uid = FirebaseAuth.getInstance().currentUser!!.uid

                    if (pass.isNotEmpty() || login.isNotEmpty()){
                        if (pass.isNotEmpty()) {
                            val user = firebaseAuth.currentUser
                            user?.updatePassword(pass)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(requireContext(), "Пароль успешно изменен", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(requireContext(), "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                    dialog.dismiss()
                                }

                            database.child("users").child(uid).child("password").setValue(pass)

                            Toast.makeText(requireContext(), "Данные успешно сохранены", Toast.LENGTH_SHORT).show()

                        }

                        if (login.isNotEmpty()) {
                            database.child("users").child(uid).child("username").setValue(login)
                            Toast.makeText(requireContext(), "Данные логина успешно сохранены", Toast.LENGTH_SHORT).show()
                        }
                    } else { Toast.makeText(requireContext(), "Поля пустые", Toast.LENGTH_SHORT).show() }
                },
                { }
            )
            dialog.show(childFragmentManager, "MyDialogEdit")
        }

        exit.setOnClickListener { controller.navigate(R.id.bookLibrary) }

        deleteProfile.setOnClickListener {
            val dialog = MyDialogFragment()
            dialog.setButtons(
                "Удалить",
                "Отмена",
                "Подтверждение удаления",
                "Вы точно хотите удалить аккаунт?",
                {
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.delete()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                database.child("users").child(uid).removeValue()
                                Toast.makeText(
                                    requireContext(),
                                    "Аккаунт успешно удален",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(requireContext(), MainLog::class.java)
                                startActivity(intent)
                                MainActivity().finish()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Ошибка при удалении аккаунта: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                },
                {

                }
            )
            dialog.show(childFragmentManager, "MyDialogFragment")
        }

        exitProfile.setOnClickListener {
            val dialog = MyDialogFragment()
            dialog.setButtons(
                "Выйти",
                "Отмена",
                "Подтверждение выхода",
                "Вы точно хотите выйти из аккаунта?",
                {
                    firebaseAuth.signOut()
                    Toast.makeText(requireContext(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(context, MainLog::class.java)
                    startActivity(intent)
                    MainActivity().finish()
                }, { })
            dialog.show(childFragmentManager, "MyDialogFragment")
        }
    }
}
