package com.korobeynikova.libro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.korobeynikova.libro.databinding.FragmentSettingsProfileBinding

class MyDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.card_main, container, false)
    }
}

class SettingsProfile : Fragment() {

    private lateinit var binding: FragmentSettingsProfileBinding
    private lateinit var  firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var dialog: MyDialogFragment
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
        val editProfile = view.findViewById<ConstraintLayout>(R.id.editLayout)
        val delliteProfile = view.findViewById<ConstraintLayout>(R.id.delliteLayout)
        val exit = view.findViewById<ImageView>(R.id.exitImage)

        dialog = MyDialogFragment()
        val yesButton = view.findViewById<Button>(R.id.yesBtn)
        val noButton = view.findViewById<Button>(R.id.noBtn)

        val controller = findNavController()

        exit.setOnClickListener { controller.navigate(R.id.profile) }

        editProfile.setOnClickListener { controller.navigate(R.id.editProfile) }

        logUotBtn.setOnClickListener {
            firebaseAuth.signOut()
            Toast.makeText(requireContext(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, MainLog::class.java)
            startActivity(intent)
            MainActivity().finish()
        }

        delliteProfile.setOnClickListener {
            dialog.show(childFragmentManager, "MyDialogFragment")
            yesButton.setOnClickListener {
                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                val user = FirebaseAuth.getInstance().currentUser
                user?.delete()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            database.child("users").child(uid).removeValue()
                            Toast.makeText(requireContext(), "Аккаунт успешно удален", Toast.LENGTH_SHORT).show()
                            val intent = Intent(requireContext(), MainLog::class.java)
                            startActivity(intent)
                            MainActivity().finish()
                        } else {
                            Toast.makeText(requireContext(), "Ошибка при удалении аккаунта: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                dialog.dismiss() // Закрыть диалоговое окно
            }

            noButton.setOnClickListener {
                dialog.dismiss() // Закрыть диалоговое окно
            }
        }
    }
}
