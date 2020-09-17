package com.wolkorp.petrescue.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.wolkorp.petrescue.R
import kotlinx.android.synthetic.main.fragment_welcome.*


class WelcomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }


    override fun onStart() {
        super.onStart()

        registrarseButton.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_welcomeFragment_to_registerFragment)
        )

        ingresarButton.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_welcomeFragment_to_loginFragment)
        )
    }
}