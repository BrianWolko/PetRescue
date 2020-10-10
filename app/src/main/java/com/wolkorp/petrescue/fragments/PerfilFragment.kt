package com.wolkorp.petrescue.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
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


        //Obtiene datos del usuario que se guardaron en AuthActivity
        val bundle: Bundle?= intent.extras
        val email: String? = bundle?.getString("email")
        val provider: String? = bundle?.getString("provider")
        // setup(email ?:"", provider ?:"")


        */
    }


    override fun onStart() {
        super.onStart()

       loadData()
       setUpLogOutButton()
    }


    //Sale de la cuenta del usuario
    private fun setUpLogOutButton() {
        logOutButton.setOnClickListener{

            val prefs = requireContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()


            Firebase.auth.signOut()
            it.findNavController().navigate(R.id.action_perfilFragment_to_authActivity)

        }
    }


    private fun loadData() {
        val prefs  = requireContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)

        val savedUserName = prefs.getString("userName",null)
        val savedEmail = prefs.getString("email",null)

        //Aca solo va a mostrar  el mail y el nombre cuando el usuario se registra por primera vez
        // si ya esta registrado, no va a mostrar los datos.
        //para solucionarlo habria que obtenerlos desde firebase
        emailTextView.text  = "${emailTextView.text}  $savedEmail"
        userNameTextView.text = "${userNameTextView.text} $savedUserName"

    }






}