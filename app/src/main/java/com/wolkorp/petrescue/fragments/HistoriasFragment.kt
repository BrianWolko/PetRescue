package com.wolkorp.petrescue.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.adapters.PostListAdapter
import com.wolkorp.petrescue.models.Post
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoriasFragment : Fragment() {


    //LOS ARIBUTOS DEL FRAGEMNT
    private val db = FirebaseFirestore.getInstance()

    private lateinit var fragmentView: View

    private lateinit var postsRecyclerView: RecyclerView
    //Una lista simple con los objetos que va a mostrar postsRecyclerView
    private var postsList: ArrayList<Post> = ArrayList<Post>()

    private lateinit var texto: TextView
    private lateinit var btnAddPost : Button
    private lateinit var btnSalir: Button
    private lateinit var btnEnviar: Button
    private lateinit var btnFoto: Button
    private lateinit var categoria : Spinner
    private lateinit var storageReference : StorageReference

    private var link =""
    private val PICK_IMAGE_CODE = 1000



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        fragmentView = inflater.inflate(R.layout.fragment_historias, container, false)
        postsRecyclerView = fragmentView.findViewById(R.id.rec_posts)
        btnAddPost = fragmentView.findViewById(R.id.btnAddPost)
        storageReference  = FirebaseStorage.getInstance().reference

        // Boton para abrir el popup
        btnAddPost.setOnClickListener{

            val popupView: View = LayoutInflater.from(activity).inflate(R.layout.popup_addpost, null)
            val popupWindow = PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

            btnSalir = popupView.findViewById(R.id.btnSalir)
            btnEnviar = popupView.findViewById(R.id.btnEnviar)
            btnFoto = popupView.findViewById(R.id.btnFotos)
            texto = popupView.findViewById(R.id.txtTexto)
            categoria = popupView.findViewById(R.id.spinnerCategorias)

            popupWindow.isFocusable = true

            // Boton para salir del popup
            btnSalir.setOnClickListener{
                popupWindow.dismiss()
            }

            btnFoto.setOnClickListener{
                selectImageFromGallery()
            }

            // Boton para enviar el post
            btnEnviar.setOnClickListener{
                val post = Post(getCurrentUser(),getDateAndTime(),texto.text.toString(),"2",link,categoria.selectedItem.toString())
                db.collection("Post").add(post)
                popupWindow.dismiss()
            }

            popupWindow.showAsDropDown(btnAddPost)
        }

        return fragmentView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPostsFromFirebase()
        //configureRecyclerView()
    }





    private fun getPostsFromFirebase() {
        //Devuelve todos los post en firebase y los agrega a la lista que despues se muestra
        db.collection("Post")
            .orderBy("hora", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                for (post in snapshot) {
                    val newPost = post.toObject<Post>()
                    postsList.add(newPost)

                }
                configureRecyclerView()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error getting documents: $exception", Toast.LENGTH_SHORT).show()
            }
    }


    private fun configureRecyclerView() {
        postsRecyclerView.setHasFixedSize(true)
        postsRecyclerView.layoutManager = LinearLayoutManager(context)
        postsRecyclerView.adapter  = PostListAdapter(postsList,requireContext())
    }


    private fun selectImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Please select..."), PICK_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Get the Uri of data
            val file_uri = data.data
            uploadImageToFirebase(file_uri!!)
        }
    }


    private fun uploadImageToFirebase(fileUri: Uri) {
        val fileName = UUID.randomUUID().toString() +".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("ImgsPost/$fileName")
        refStorage.putFile(fileUri)

        val APP_NAME = "pet-rescue-4f2a1"

        link = "https://firebasestorage.googleapis.com/v0/b/" + APP_NAME + ".appspot.com/o/ImgsPost%2F" + fileName + "?alt=media"
    }


    private fun getDateAndTime(): String {
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val currentDateandTime: String = simpleDateFormat.format(Date())
        return currentDateandTime
    }


    private fun getCurrentUser() : String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.email
    }


}