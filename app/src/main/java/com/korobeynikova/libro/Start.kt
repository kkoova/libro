package com.korobeynikova.libro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.korobeynikova.libro.databinding.FragmentStartBinding

class Start : Fragment() {

    private lateinit var binding: FragmentStartBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root

        val container = findNavController()

        binding.button.setOnClickListener {
            container.navigate(R.id.startSingOrLogin)
        }
    }
}