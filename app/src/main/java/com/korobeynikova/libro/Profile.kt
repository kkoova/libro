package com.korobeynikova.libro

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.korobeynikova.libro.databinding.FragmentProfileBinding


class Profile : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingsBtn = view.findViewById<ImageView>(R.id.settingsBtn)
        val exit = view.findViewById<ImageView>(R.id.exitImage)

        val controller = findNavController()

        settingsBtn.setOnClickListener { controller.navigate(R.id.settingsProfile) }
        exit.setOnClickListener { controller.navigate(R.id.bookLibrary) }

        binding.progressBar.visibility = View.VISIBLE

        database = Firebase.database.reference
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("users").child(uid).get()
            .addOnSuccessListener {
                val login = it.child("username").value.toString()
                val all = it.child("all").value.toString()
                val like = it.child("like").value.toString()
                binding.textProfName.text = login
                binding.textBook.text = all
                binding.textLikeBook.text = like
                binding.progressBar.visibility = View.GONE
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Данные не были загруженны", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }


        binding.settingsBtn.setOnClickListener {
            val dialog = MyDialogEdit()
            dialog.setButtons(
                "Сохранить",
                "Отмена",
                { email: String, login: String ->
                    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
                    val database: DatabaseReference = FirebaseDatabase.getInstance().reference

                    val uid = FirebaseAuth.getInstance().currentUser!!.uid

                    if (email.isEmpty() || login.isEmpty()) {
                        Toast.makeText(requireContext(), "Заполните поля", Toast.LENGTH_SHORT).show()
                    } else {
                        val user = firebaseAuth.currentUser
                        user?.updatePassword(email)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(requireContext(), "Пароль успешно изменен", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(requireContext(), "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                                dialog.dismiss()
                            }

                        database.child("users").child(uid).child("username").setValue(login)
                        database.child("users").child(uid).child("password").setValue(email)

                        Toast.makeText(requireContext(), "Данные успешно сохранены", Toast.LENGTH_SHORT).show()
                    }
                },
                { }
            )
            dialog.show(childFragmentManager, "MyDialogEdit")
        }
    }
}