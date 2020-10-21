package com.wolkorp.petrescue.fragments

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.models.User
import kotlinx.android.synthetic.main.fragment_editar_perfil.*
import java.util.*


class EditarPerfilFragment : Fragment() {

    companion object {
        private val PICK_IMAGE_CODE = 1000
        private val APP_NAME = "pet-rescue-4f2a1"
    }

    lateinit var btn_cambiar_img : Button
    lateinit var v : View
    lateinit var link : String
    private  var dbAuth = FirebaseAuth.getInstance()
    private val dbFS = FirebaseFirestore.getInstance()
    lateinit var uid : String
    lateinit var nombre : TextView
    lateinit var celular : TextView
    lateinit var user : User
    lateinit var btnEnviar: Button
    lateinit var btnCancelar: Button
    lateinit var imagen : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_editar_perfil, container, false)
        btn_cambiar_img = v.findViewById(R.id.btn_cambiar_foto)
        nombre = v.findViewById(R.id.nombre)
        celular = v.findViewById(R.id.celular)
        imagen = v.findViewById(R.id.profile_img)
        btnEnviar = v.findViewById(R.id.btnEnviar)
        btnCancelar = v.findViewById(R.id.btnCancelar)
        updateCurrentUser()

        return v
    }

    override fun onStart() {
        super.onStart()
        btn_cambiar_img.setOnClickListener{
            selectImageFromGallery()

        }

        btnEnviar.setOnClickListener{
            updateUser()
            it.findNavController().navigate(R.id.action_editarPerfilFragment_to_perfilFragment)
        }

        btnCancelar.setOnClickListener{
            it.findNavController().navigate(R.id.action_editarPerfilFragment_to_perfilFragment)
        }

        imagen.setOnClickListener{
            selectImageFromGallery()
        }

    }
    private fun selectImageFromGallery() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Please select..."),
            EditarPerfilFragment.PICK_IMAGE_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EditarPerfilFragment.PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null)
        {

            // Get the Uri of data
            val file_uri = data.data
            uploadImageToFirebase(file_uri!!)

        }
    }


    private fun uploadImageToFirebase(fileUri: Uri) {
        val fileName = UUID.randomUUID().toString() +".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("ProfImgs/$fileName")
        refStorage.putFile(fileUri)
        link = "https://firebasestorage.googleapis.com/v0/b/" + EditarPerfilFragment.APP_NAME + ".appspot.com/o/ProfImgs%2F" + fileName + "?alt=media"


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
                celular.text = user.phoneNumber
                link = user.profileImageUrl
                uid = user.uid
                updateImage(user.profileImageUrl)

            }

        }

    }

    private fun updateUser(){
        val ref = dbFS.collection("Users").document(uid)
        ref.update("userName",nombre.text.toString())
        ref.update("phoneNumber",celular.text.toString())
        ref.update("profileImageUrl",link)

    }
    private fun updateImage(link : String){
        Glide
            .with(v)
            .load(link)
            .into(imagen)
    }


}