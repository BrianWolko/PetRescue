package com.wolkorp.petrescue.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Patterns.EMAIL_ADDRESS
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


class  RegisterFragment : Fragment() {

    //Es un atributo porque se necesita para navegar a MainActivity
    private lateinit var fragmentView: View

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
    private fun setUpRegisterButton() {

        registerButton.setOnClickListener {

            val email = registerEmailEditText.text.toString()
            val password = registerPasswordEditText.text.toString()
            val userName = registerNameEditText.text.toString()
            val userLastName = registerLastNameEditText.text.toString()

            //Si alguno de los campos esta vacio, sale de la funcion
            if(email.isEmpty() || password.isEmpty() || userName.isEmpty() || userLastName.isEmpty()) {
                Toast.makeText(context, "Los campos no pueden estar vacios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }




            //Validación de datos

            if(password.length < 6 ){
                Toast.makeText(context, "La contraseña debe tener 6 caracteres como mínimo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(validarEmail(email) ){
                Toast.makeText(context, "El formato del mail es incorrecto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            Toast.makeText(context, "Creando usuario...", Toast.LENGTH_SHORT).show()


            FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //Registro exitoso, guarda datos y navega hacia MainActivity
                        saveUserLocally(email, userName,userLastName)
                        saveUserToFirestore(email, userName, userLastName)

                    } else {
                        // No se pudo registrar, mostrar mensaje de fallo.
                        //todo Podria mejorarse y hacer mas cosas que solo mostrar un mensaje
                        Toast.makeText(context, "No se pudo crear tu cuenta. \nIntenta de nuevo", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }



    private fun validarEmail(email: String): Boolean {
        val pattern = EMAIL_ADDRESS
        return !pattern.matcher(email).matches()
    }

    private fun saveUserLocally(email: String, userName: String, userLastName: String) {
        val prefs = requireContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("userName", userName)
        prefs.putString("userLastName", userLastName)
        prefs.apply()
    }


    //Crea un objeto usuario y lo envia a firebase
    private fun saveUserToFirestore(email: String, userName: String, userLastName: String) {

        //Ocupa el mismo uid de FirebaseAuth como id del documento de firestore
        val uid = FirebaseAuth.getInstance().uid ?: "No id"
        //Al momento de registrarse el numero de telefono y el url de la imagen van vacios
        //val profileImageStock = "https://firebasestorage.googleapis.com/v0/b/pet-rescue-4f2a1.appspot.com/o/ProfImgs%2Fstock.png?alt=media&token=f76e8822-a47d-4091-b237-5ce3f3ff9ca0"
        val user = User(uid, userName, userLastName, email, "", "", "")

        FirebaseFirestore
            .getInstance()
            .collection("Users")
            .document(uid)
            .set(user)
            .addOnSuccessListener {

                Toast.makeText(context, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()
                navigateToMainActivity()
            }
            .addOnFailureListener { error ->

                //todo podria mejorarse el manejo de errores
                Toast.makeText(context, "No se pudo crear tu cuenta. \nIntenta de nuevo", Toast.LENGTH_SHORT).show()
                Log.d("RegisterFragment", "Error creando usuario $error")
            }
    }


    private fun navigateToMainActivity() {
        //Solo debe llamarse si se registro con exito al usuario
        fragmentView.findNavController().navigate(R.id.action_registerFragment_to_homeActivity)
    }

}