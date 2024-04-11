package com.korobeynikova.libro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class StartSingOrLogin : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start_sing_or_login, container, false)

        val loginBtn = view.findViewById<Button>(R.id.loginBtn)
        val singBtn = view.findViewById<Button>(R.id.singBtn)
        val exitBtn = view.findViewById<ImageView>(R.id.exitImage)
        val chitText = view.findViewById<TextView>(R.id.textReg)

        val controller = findNavController()

        loginBtn.setOnClickListener { controller.navigate(R.id.loginUp) }
        singBtn.setOnClickListener { controller.navigate(R.id.singUp) }
        exitBtn.setOnClickListener { controller.navigate(R.id.start2) }
        chitText.setOnClickListener { controller.navigate(R.id.bookLibrary) }

        return view
    }
}