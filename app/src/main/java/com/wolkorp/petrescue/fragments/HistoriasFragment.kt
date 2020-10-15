package com.wolkorp.petrescue.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.adapters.PostListAdapter
import com.wolkorp.petrescue.models.Post
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoriasFragment : Fragment() {


    //LOS ARIBUTOS DEL FRAGEMNT
    private val db = FirebaseFirestore.getInstance()
    //Listener que escucha cambio en la base de datos
    private lateinit var registrationListener: ListenerRegistration

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

    private var linkImagen =""
    private val PICK_IMAGE_CODE = 1000



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val fragmentView = inflater.inflate(R.layout.fragment_historias, container, false)
        postsRecyclerView = fragmentView.findViewById(R.id.rec_posts)
        btnAddPost = fragmentView.findViewById(R.id.btnAddPost)
        storageReference  = FirebaseStorage.getInstance().reference

        // Boton para abrir el popup
        btnAddPost.setOnClickListener{

            val popupView = LayoutInflater.from(activity).inflate(R.layout.popup_addpost, null)
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
                val fullName = getCurrentUserName()
                val horaPost = getDateAndTime()
                val textoPost = texto.text.toString()
                val categoriaSeleccionada = categoria.selectedItem.toString()
                val idUsuario = FirebaseAuth.getInstance().uid ?: "No id"

                val post = Post(fullName, horaPost, textoPost,linkImagen, categoriaSeleccionada, idUsuario)
                db.collection("Post").add(post)

                popupWindow.dismiss()
            }
            popupWindow.showAsDropDown(btnAddPost)
        }
        return fragmentView
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
            uploadImageToFirebase(file_uri!!)
        }
    }


    private fun uploadImageToFirebase(fileUri: Uri) {
        val fileName = UUID.randomUUID().toString() +".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("ImgsPost/$fileName")
        refStorage.putFile(fileUri)

        val APP_NAME = "pet-rescue-4f2a1"
        linkImagen = "https://firebasestorage.googleapis.com/v0/b/" + APP_NAME + ".appspot.com/o/ImgsPost%2F" + fileName + "?alt=media"
    }


    private fun getCurrentUserName() : String {
        val prefs = requireContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val userName = prefs.getString("userName",null)
        val userLastName = prefs.getString("userLastName",null)

        //todo: manejar el caso de que savedUserName sea null, aunque nunca deberia porque RegisterFragment
        //todo: no deberia permitir registrado de un usuario sino guarda su nombre
        return "$userName $userLastName"
    }


    private fun getDateAndTime(): String {
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date())
        return dateFormat

    }


    override fun onStart() {
        super.onStart()
        getPostsFromFirebase()
    }


    private fun getPostsFromFirebase() {

        //Argumento obtenido con clases autogeneradas del navgraph
        val selectedCategory = HistoriasFragmentArgs.fromBundle(requireArguments()).selectedCategory

        //Devuelve los post en firebase que coincidan con la categoria que se toco
        val query =  db
                            .collection("Post")
                            .whereEqualTo("categoria", selectedCategory)
                           // .orderBy("hora", Query.Direction.DESCENDING)

        //El registration listener lo guardo porque se necesita para desactivar el listener cuando el fragment no esta activo
        registrationListener = query.addSnapshotListener { snapshot, error  ->
            if (error != null) {
                //todo handle error
                Toast.makeText(context, "Error getting posts", Toast.LENGTH_SHORT).show()
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()

                return@addSnapshotListener
            }

            postsList.clear()
            for (post in snapshot!!) {
                postsList.add(post.toObject())
            }

            //Es importante que este metodo se llame despues de haber llenado la lista
            //con los posts, sino no se muestra nada en el recyclerView
            updateRecyclerView()
        }
    }


    private fun updateRecyclerView() {
        postsRecyclerView.setHasFixedSize(true)
        postsRecyclerView.adapter  = PostListAdapter(postsList,requireContext())
    }


    override fun onStop() {
        super.onStop()
        registrationListener.remove()
    }

}