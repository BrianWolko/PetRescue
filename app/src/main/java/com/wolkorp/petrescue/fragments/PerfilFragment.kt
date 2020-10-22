package com.wolkorp.petrescue.fragments

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.models.User
import kotlinx.android.synthetic.main.fragment_perfil.*
import java.util.*


class PerfilFragment : Fragment() {

    private  var dbAuth = FirebaseAuth.getInstance()
    private val dbFS = FirebaseFirestore.getInstance()
    lateinit var v : View
    lateinit var nombre : TextView
    lateinit var pais : TextView
    lateinit var email : TextView
    lateinit var numero: TextView
    lateinit var user : User
    lateinit var imagen : ImageView
    lateinit var link : String
    lateinit var uid : String
    private lateinit var btnEliminar : Button
    private lateinit var  btnEditar : Button





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_perfil, container, false)
        nombre = v.findViewById(R.id.nombre)
        pais = v.findViewById(R.id.pais)
        email = v.findViewById(R.id.email)
        numero = v.findViewById(R.id.numero)
        btnEliminar = v.findViewById(R.id.btn_eliminar)
        imagen = v.findViewById(R.id.profile_img)
        btnEditar = v.findViewById(R.id.btn_editar)

        updateCurrentUser()

        return v


    }


    override fun onStart() {
        super.onStart()


       loadData()
        btnEliminar.setOnClickListener{
            it.findNavController().navigate(R.id.action_perfilFragment_to_misPostsFragment)
        }

        btnEditar.setOnClickListener{
            it.findNavController().navigate(R.id.action_perfilFragment_to_editarPerfilFragment)
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



    private fun updateImage(link : String){
        Glide
            .with(v)
            .load(link)

            .into(imagen)
    }

    private fun updateCurrentUser(){
        val currentUser = dbAuth.currentUser
        if(currentUser!=null){
            val query =  dbFS.collection("Users")
                .whereEqualTo("email",currentUser.email)

            query.addSnapshotListener() { snapshot, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    user = snapshot.documents[0].toObject()!!
                }
                nombre.text =user.userName
                pais.text = user.pais
                email.text = user.email
                numero.text = user.phoneNumber
                link = user.profileImageUrl
                uid = user.uid
                updateImage(user.profileImageUrl)



            }

        }

    }


}