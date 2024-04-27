package com.korobeynikova.libro

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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

    private var positiveText: String? = null
    private var negativeText: String? = null
    private var mainText: String? = null
    private var noMainText: String? = null
    private var positiveAction: (() -> Unit)? = null
    private var negativeAction: (() -> Unit)? = null

    fun setButtons(
        positiveText: String,
        negativeText: String,
        mainText: String,
        noMainText: String,
        positiveAction: () -> Unit,
        negativeAction: () -> Unit
    ) {
        this.positiveText = positiveText
        this.negativeText = negativeText
        this.mainText = mainText
        this.noMainText = noMainText
        this.positiveAction = positiveAction
        this.negativeAction = negativeAction
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.card_main, container, false)

        view.findViewById<TextView>(R.id.mainText).text = mainText
        view.findViewById<TextView>(R.id.noMainText).text = noMainText

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yesButton = view.findViewById<Button>(R.id.yesBtn)
        val noButton = view.findViewById<Button>(R.id.noBtn)

        yesButton.text = positiveText
        noButton.text = negativeText

        yesButton.setOnClickListener {
            positiveAction?.invoke()
            dismiss()
        }

        noButton.setOnClickListener {
            negativeAction?.invoke()
            dismiss()
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}

class SettingsProfile : Fragment() {

    private lateinit var binding: FragmentSettingsProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
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
        val controller = findNavController()

        exit.setOnClickListener { controller.navigate(R.id.profile) }

        editProfile.setOnClickListener { controller.navigate(R.id.editProfile) }

        logUotBtn.setOnClickListener {
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

        delliteProfile.setOnClickListener {
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
    }
}
