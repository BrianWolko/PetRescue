package com.wolkorp.petrescue.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wolkorp.petrescue.R
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : Fragment() {


    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }


    override fun onStart() {
        super.onStart()
        setUpRegisterButton()
    }


    //Funcion de Registro, captura el mail y la contraseña y crea un nuevo usuario en Firebase
    private fun setUpRegisterButton(){

        registerButton.setOnClickListener {

            //Por hacer: Aesegurarse de que estas no sean null
            val email = registerEmailEditText.text.toString()
            val password = registerPasswordEditText.text.toString()

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Registro exitoso, navega hacia activity main
                    it.findNavController().navigate(R.id.action_registerFragment_to_homeActivity)

                } else {
                    // No se pudo registrar, mostrar mensaje de fallo.
                    // Podria mejorarse y hacer mas cosas que solo mostrar un mensaje
                    Toast.makeText(context, "No se pudo registrar tu cuenta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}