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
import android.widget.EditText
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
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoader

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


        val delliteProfile = view.findViewById<ConstraintLayout>(R.id.delliteLayout)
        val exit = view.findViewById<ImageView>(R.id.exitImage)

        val controller = findNavController()

        exit.setOnClickListener { controller.navigate(R.id.profile) }

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
