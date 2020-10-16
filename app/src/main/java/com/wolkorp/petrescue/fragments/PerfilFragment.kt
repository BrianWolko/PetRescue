package com.wolkorp.petrescue.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.models.User
import kotlinx.android.synthetic.main.fragment_perfil.*


class PerfilFragment : Fragment() {

    private  var dbAuth = FirebaseAuth.getInstance()
    private val dbFS = FirebaseFirestore.getInstance()
    lateinit var v : View
    lateinit var nombre : TextView
    lateinit var pais : TextView
    lateinit var email : TextView
    lateinit var numero: TextView
    lateinit var user : User
    private lateinit var btnEliminar : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_perfil, container, false)
        nombre = v.findViewById(R.id.nombre)
        pais = v.findViewById(R.id.pais)
        email = v.findViewById(R.id.email)
        numero = v.findViewById(R.id.numero)
        btnEliminar = v.findViewById(R.id.btn_eliminar)
        val currentUser = dbAuth.currentUser
        if(currentUser!=null){
            dbFS.collection("Users")
                .whereEqualTo("email",currentUser.email)
                .get()
                .addOnSuccessListener { snapshot ->
                    user = snapshot.documents[0].toObject()!!
                    nombre.text =user.userName
                    pais.text = user.pais
                    email.text ="Mail: " +  user.email
                    numero.text ="Celular: " + user.phoneNumber


                }
        }


        return v


    }


    override fun onStart() {
        super.onStart()


       loadData()
        btnEliminar.setOnClickListener{
            it.findNavController().navigate(R.id.action_perfilFragment_to_misPostsFragment)
        }
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


    }






}