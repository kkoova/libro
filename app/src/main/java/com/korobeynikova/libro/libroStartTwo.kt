package com.korobeynikova.libro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.korobeynikova.libro.databinding.FragmentLibroStartTwoBinding

class libroStartTwo : Fragment() {

    private lateinit var binding: FragmentLibroStartTwoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLibroStartTwoBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val container = findNavController()

        binding.goTwoBtn.setOnClickListener {
            container.navigate(R.id.libroStartThree)
        }
    }
}