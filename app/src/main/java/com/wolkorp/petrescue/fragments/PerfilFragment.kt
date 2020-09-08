package com.wolkorp.petrescue.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.wolkorp.petrescue.R


class PerfilFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil, container, false)


        /* Hay que modificar unas cosas para que funcione en el fragment

        //setup
        val bundle: Bundle?= intent.extras
        val email: String? = bundle?.getString("email")
        val provider: String? = bundle?.getString("provider")
        setup(email ?:"", provider ?:"")

        //Guardado de datos BW 18/8/2020
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider",provider)
        prefs.apply()

        */

    }


    /* Hay que modificar unas cosas para que funcione en el fragment
    private fun setup(email: String, provider: String) {

        title = "Inicio"
        emailTextView.text= email
        providerTextView.text = provider

        logOutButton.setOnClickListener{
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()


            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }

     */

}