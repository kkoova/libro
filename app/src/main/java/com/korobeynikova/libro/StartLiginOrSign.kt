package com.korobeynikova.libro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.korobeynikova.libro.databinding.FragmentStartLiginOrSignBinding

class StartLiginOrSign : Fragment() {

    private lateinit var binding: FragmentStartLiginOrSignBinding
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

        binding.logBtn.setOnClickListener {
            container.navigate(R.id.loginUpLibro)
        }
        binding.signBtn.setOnClickListener {
            container.navigate(R.id.signUpLibro)
        }
    }
}