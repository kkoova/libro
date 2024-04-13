package com.korobeynikova.libro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.korobeynikova.libro.databinding.FragmentBookLibraryBinding
import com.korobeynikova.libro.databinding.FragmentLoginUpBinding

class BookLibrary : Fragment() {

    private lateinit var profileBtn: ImageView
    private lateinit var  firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentBookLibraryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileBtn = view.findViewById(R.id.profileBtn)
        firebaseAuth = FirebaseAuth.getInstance()

        buttonClick()
    }
    private fun buttonClick(){

        val container = findNavController()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // Пользователь вошел в аккаунт
            binding.helloTextLibr.text = "Привет" + currentUser.tenantId.toString()
        } else {
            // Пользователь не вошел в аккаунт
            container.navigate(R.id.start2)
        }

        profileBtn.setOnClickListener{
            container.navigate(R.id.profile)
        }
    }
}