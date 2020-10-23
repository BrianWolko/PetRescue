package com.wolkorp.petrescue.fragments

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
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

    lateinit var fragmentView : View
    lateinit var profileImage : ImageView
    lateinit var btn_cambiar_img : Button
    lateinit var nombre : TextView
    lateinit var celular : TextView
    lateinit var btnConfirmarCambios: Button
    lateinit var btnCancelar: Button

    private val PICK_IMAGE_CODE = 1000
    private var selectedPhotoUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentView =  inflater.inflate(R.layout.fragment_editar_perfil, container, false)
        profileImage = fragmentView.findViewById(R.id.profile_img)
        btn_cambiar_img = fragmentView.findViewById(R.id.btn_cambiar_foto)
        nombre = fragmentView.findViewById(R.id.nombre)
        celular = fragmentView.findViewById(R.id.celular)
        btnConfirmarCambios = fragmentView.findViewById(R.id.btn_confirmar_cambios)
        btnCancelar = fragmentView.findViewById(R.id.btnCancelar)

        return fragmentView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showCurrentUser()

    }

    override fun onStart() {
        super.onStart()

        btn_cambiar_img.setOnClickListener{
            selectImageFromGallery()
        }

        btnConfirmarCambios.setOnClickListener{
            updateUser()
            it.findNavController().navigate(R.id.action_editarPerfilFragment_to_perfilFragment)
        }

        btnCancelar.setOnClickListener{
            it.findNavController().navigate(R.id.action_editarPerfilFragment_to_perfilFragment)
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
                    loadProfileImage(user.profileImageUrl)
                    Toast.makeText(context, "Exito obteniendo el usuario", Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(
                        context,
                        "No existe el usuario con id $currentUserId",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting user", exception)
            }
        }

    }


    private fun loadProfileImage(profileImgUrl: String) {
        Glide
            .with(fragmentView)
            .load(profileImgUrl)
            .into(profileImage)
    }


    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Please select..."), PICK_IMAGE_CODE)
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Guarda la uri de la foto
            selectedPhotoUri = data.data

            // Cambia el fondo de la a imagenntigua por la seleccionada
            try {
                selectedPhotoUri?.let {

                    val source = ImageDecoder.createSource(requireActivity().contentResolver, selectedPhotoUri!!)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    profileImage.setImageBitmap(bitmap)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    private fun updateUser() {

        // Se selecciono una imagen
        if (selectedPhotoUri != null) {

            // Primero guardo la foto a Storage
            val fileName = UUID.randomUUID().toString()
            val refStorage = FirebaseStorage.getInstance().reference.child("ProfImgs/$fileName")


            // todo: Averiguar si es mejor actualizar desde Firebase en vez desde Storage. Tambien falta borrar la foto antigua
            refStorage.putFile(selectedPhotoUri!!).addOnSuccessListener {

                refStorage.downloadUrl.addOnSuccessListener { firestoreUrl ->
                    val profileImgUrl = firestoreUrl.toString()

                    // Ahora que tenoo el link de Storage de la nueva imagen guardo en Firebase
                    updateUserToFirebase(profileImgUrl)
                }
            }

         // No se selecciono imagen
        } else {
            updateUserToFirebase(null)
        }




    }


    private fun updateUserToFirebase(profileImgUrl: String?) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseFirestore.getInstance().collection("Users").document(currentUserId!!)

        // Se selecciono una imagen actualizo todos los atributos
        // todo: Podria mejorarse. Ser mas eficiente
        if (profileImgUrl != null) {
            ref.update("userName", nombre.text.toString())
            ref.update("phoneNumber", celular.text.toString())
            ref.update("profileImageUrl", profileImgUrl)

        // No se selecciono imagen, solo actualizo nombre y telefono
        } else {
            ref.update("userName", nombre.text.toString())
            ref.update("phoneNumber", celular.text.toString())
        }


    }

}