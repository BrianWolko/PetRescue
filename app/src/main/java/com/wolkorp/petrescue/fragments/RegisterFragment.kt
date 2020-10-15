package com.wolkorp.petrescue.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.models.User
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : Fragment() {


    private lateinit var auth: FirebaseAuth
    private lateinit var fragmentView: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_register, container, false)
        return fragmentView
    }


    override fun onStart() {
        super.onStart()
        setUpRegisterButton()
    }


    //Funcion de Registro, captura el mail y la contraseña y crea un nuevo usuario en Firebase
    private fun setUpRegisterButton(){

        registerButton.setOnClickListener {

            val email = registerEmailEditText.text.toString()
            val password = registerPasswordEditText.text.toString()
            val userName = registerNameEditText.text.toString()

            //Si alguno de los campos esta vacio, sale de la funcion
            if(email.isEmpty() || password.isEmpty() || userName.isEmpty()) {
                Toast.makeText(context, "Los campos no pueden estar vacios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Registro exitoso, guarda datos y navega hacia activity main
                    saveUserLocally(email, userName)
                    saveUserToFirestore(email, userName)

                } else {
                    // No se pudo registrar, mostrar mensaje de fallo.
                    // Podria mejorarse y hacer mas cosas que solo mostrar un mensaje
                    Toast.makeText(context, "No se pudo crear tu cuenta. \nIntenta de nuevo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun saveUserLocally(email: String, userName: String) {
        val prefs = requireContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("userName", userName)
        prefs.apply()
    }


    private fun saveUserToFirestore(email: String, userName: String) {

        //todo esta no es la correcta
        val uid = FirebaseAuth.getInstance().uid ?: "No id"
        //Por ahora el numero de telefono y el url de la imagen van vacios
        val user = User(uid, userName, email, "", "")

        FirebaseFirestore
            .getInstance()
            .collection("Users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(context, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()
                //Si se agrega con exito el usuario se Navega al MainActivity
                fragmentView.findNavController().navigate(R.id.action_registerFragment_to_homeActivity)

            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "No se pudo crear tu cuenta. \nIntenta de nuevo", Toast.LENGTH_SHORT).show()
            }
    }

}