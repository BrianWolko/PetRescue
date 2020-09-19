package com.wolkorp.petrescue.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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


    private fun setUpRegisterButton(){



        registerButton.setOnClickListener {

            val email = registerEmailEditText.text.toString()
            val password = registerPasswordEditText.text.toString()

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser

                    Toast.makeText(context, "Bieeeeeeeeen .", Toast.LENGTH_SHORT).show()
                    //Aca hace funcion que navegue hacia activity main
                    // updateUI(user)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()

                    //updateUI(null)
                }

                // ...
            }
        }
    }

    /*
       //funcion de registro
        signUpButton.setOnClickListener{
            if( emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty() ){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailEditText.text.toString(),
                    passwordEditText.text.toString()).addOnCompleteListener{

                    if(it.isSuccessful){
                        showHome(it.result?.user?.email ?:"" , ProviderType.BASIC )
                    } else {
                        showAlert()
                    }
                }
            }
        }
     */

}