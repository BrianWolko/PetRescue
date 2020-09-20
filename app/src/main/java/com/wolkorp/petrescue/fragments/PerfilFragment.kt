package com.wolkorp.petrescue.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wolkorp.petrescue.R
import kotlinx.android.synthetic.main.fragment_perfil.*


class PerfilFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil, container, false)




        /*
        //Estas lineas de abajo fueron hecha por brian en en MainActivity, hay que averiguar si es necesario mantenerlas
        // y como pasar informacion de una activity a un fragment

        //Guardado de datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider",provider)
        prefs.apply()

        */
    }


    override fun onStart() {
        super.onStart()

        //Sale de la cuenta del usuario
        logOutButton.setOnClickListener{
            Firebase.auth.signOut()
            it.findNavController().navigate(R.id.action_perfilFragment_to_authActivity)

        }
    }




    /* Esta funcion setea los valores de algunas vistas en perfilfragment
    //Estaba hecha en MainActivity por brian, hay que modificar unas cosas para que funcione en el fragment.
    private fun setup(email: String, provider: String) {

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