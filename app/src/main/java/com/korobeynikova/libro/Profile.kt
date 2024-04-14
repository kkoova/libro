package com.korobeynikova.libro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.korobeynikova.libro.databinding.FragmentProfileBinding


class Profile : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingsBtn = view.findViewById<ImageView>(R.id.settingsBtn)
        val libraryBtn = view.findViewById<ImageView>(R.id.bookBnt)

        val controller = findNavController()

        settingsBtn.setOnClickListener { controller.navigate(R.id.settingsProfile) }
        libraryBtn.setOnClickListener { controller.navigate(R.id.bookLibrary) }
    }
}