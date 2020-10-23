package com.wolkorp.petrescue.fragments

import android.app.Activity
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
import android.widget.Toast
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.models.User
import java.util.*


class EditarPerfilFragment : Fragment() {


    lateinit var btn_cambiar_img : Button
    lateinit var fragmentView : View
    lateinit var profileImgUrl : String
    lateinit var nombre : TextView
    lateinit var celular : TextView
    lateinit var btnEnviar: Button
    lateinit var btnCancelar: Button
    lateinit var imagen : ImageView

    private val PICK_IMAGE_CODE = 1000



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentView =  inflater.inflate(R.layout.fragment_editar_perfil, container, false)
        btn_cambiar_img = fragmentView.findViewById(R.id.btn_cambiar_foto)
        nombre = fragmentView.findViewById(R.id.nombre)
        celular = fragmentView.findViewById(R.id.celular)
        imagen = fragmentView.findViewById(R.id.profile_img)
        btnEnviar = fragmentView.findViewById(R.id.btnEnviar)
        btnCancelar = fragmentView.findViewById(R.id.btnCancelar)

        return fragmentView
    }


    override fun onStart() {
        super.onStart()
        showCurrentUser()

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


    private fun showCurrentUser() {
        // todo: mejorar esta funcion, no es necesario llamar a firebase para obtener el usuario, lo puedo obtener del fragment anterior. Gasta datos
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if(currentUserId != null) {
            val query =  FirebaseFirestore
                .getInstance()
                .collection("Users")
                .document(currentUserId)

            query.get().addOnSuccessListener { document ->

                if (document != null) {
                    val user: User = document.toObject()!!
                    nombre.text =user.userName
                    celular.text = user.phoneNumber
                    profileImgUrl = user.profileImageUrl
                    loadImage(user.profileImageUrl)

                    Toast.makeText(context, "Exito obteniendo el usuario", Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(context, "No existe el usuario con id $currentUserId", Toast.LENGTH_LONG).show()
                }
            }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }

    }


    private fun loadImage(link : String){
        Glide
            .with(fragmentView)
            .load(link)
            .into(imagen)
    }


    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Please select..."), PICK_IMAGE_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Get the Uri of data
            val file_uri = data.data
            updateImageToFirebaseStorage(file_uri!!)
        }
    }


    private fun updateImageToFirebaseStorage(fileUri: Uri) {
        // todo: Averiguar si es mejor actualizar desde Firebase, no desde Storage, y tambien falta borrar la foto antigua

        val fileName = UUID.randomUUID().toString()
        val refStorage = FirebaseStorage.getInstance().reference.child("ProfImgs/$fileName")

        refStorage.putFile(fileUri).addOnSuccessListener {
            refStorage.downloadUrl.addOnSuccessListener { firestoreUrl ->
                profileImgUrl = firestoreUrl.toString()
            }
        }
    }


    private fun updateUser() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        val ref = FirebaseFirestore.getInstance().collection("Users").document(currentUserId!!)
        ref.update("userName",nombre.text.toString())
        ref.update("phoneNumber",celular.text.toString())
        ref.update("profileImageUrl",profileImgUrl)

    }

}