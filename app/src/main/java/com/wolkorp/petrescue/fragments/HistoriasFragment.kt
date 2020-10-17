package com.wolkorp.petrescue.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.adapters.CategoriesAdapter
import com.wolkorp.petrescue.adapters.PostListAdapter
import com.wolkorp.petrescue.models.Post
import java.util.*
import kotlin.collections.ArrayList


class HistoriasFragment : Fragment() {

    //LOS ARIBUTOS DEL FRAGEMNT

    private lateinit var fragmentView: View
    private lateinit var postsRecyclerView: RecyclerView
    //Una lista simple con los objetos que va a mostrar postsRecyclerView
    private var postsList: ArrayList<Post> = ArrayList()

    private lateinit var popupWindow: PopupWindow
    private lateinit var textoPost: TextView
    private lateinit var btnSalir: Button
    private lateinit var btnEnviar: Button
    private lateinit var btnFoto: Button
    private lateinit var categoria : Spinner

    private var selectedPhotoUri: Uri? = null
    private val PICK_IMAGE_CODE = 1000
    private lateinit var selectedCategory: String

    //Listener que escucha cambio en la base de datos
    private lateinit var registrationListener: ListenerRegistration



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inicializar las views
        fragmentView = inflater.inflate(R.layout.fragment_historias, container, false)
        postsRecyclerView = fragmentView.findViewById(R.id.rec_posts)
        postsRecyclerView.setHasFixedSize(true)


        val popupView = LayoutInflater.from(activity).inflate(R.layout.popup_addpost, null)
        popupWindow = PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        btnSalir = popupView.findViewById(R.id.btnSalir)
        btnEnviar = popupView.findViewById(R.id.btnEnviar)
        btnFoto = popupView.findViewById(R.id.btnFoto)
        textoPost = popupView.findViewById(R.id.txtTexto)
        categoria = popupView.findViewById(R.id.spinnerCategorias)

        return fragmentView
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.historias_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onStart() {
        super.onStart()
        updateActionBarTitle()
        getPostsFromFirebase()
    }


    private fun updateActionBarTitle() {
        // Argumento tipo string cargado en CategoriasFragment con el nombre de la categoria
        selectedCategory = HistoriasFragmentArgs.fromBundle(requireArguments()).selectedCategory

        // Barra superior de la activity que aparece encima del fragment
        val actionBar: ActionBar? = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar?.title =  selectedCategory
    }


    private fun getPostsFromFirebase() {
        // Devuelve los post en firebase que coincidan con la categoria que se seleccionó, ordenados por hora
        val query = FirebaseFirestore
                            .getInstance()
                            .collection("Posts")
                            .whereEqualTo("categoria", selectedCategory)
                            .orderBy("hora", Query.Direction.DESCENDING)

        //El registration listener lo guardo porque se necesita para desactivar el listener cuando el fragment no esta activo
        registrationListener = query.addSnapshotListener { snapshot, error ->

            if (error != null) {
                //todo Falta mejorar codigo para manejar errores caso de que exista uno
                Toast.makeText(context, "Error getting posts", Toast.LENGTH_SHORT).show()
                Log.d("HistoriasFragment", "ESTE ES EL ERROR DE FIREBASE $error")
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
        //Esta es la linea de codigo que une el adapter con el recyclerView y permite que funcinonen juntos
        postsRecyclerView.adapter  = PostListAdapter(postsList, requireContext()) { selectedPost -> onPostClick(selectedPost) }
    }


    //Funcion que se llama cuando el usuario toca un post
    private fun onPostClick(selectedPost: Post) {


        //Guarda el post en las clases autogeneradas del navgraph para pasar a otro fragment
        val action = HistoriasFragmentDirections.actionHistoriasFragmentToPostDetailFragment(selectedPost)
        fragmentView.findNavController().navigate(action)
    }


    // Se llama cuando se toca el botton de la barra superior
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            // Boton para abrir el popup
            R.id.menu_add_post -> {

                popupWindow.isFocusable = true
                popupWindow.showAsDropDown(postsRecyclerView)

                btnFoto.setOnClickListener{
                    selectImageFromGallery()
                }

                // Boton para enviar el post
                btnEnviar.setOnClickListener{
                    uploadToFirebase()
                    popupWindow.dismiss()
                }

                // Boton para salir del popup
                btnSalir.setOnClickListener{
                    popupWindow.dismiss()
                }
            }
        }
        return super.onOptionsItemSelected(item)
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
            selectedPhotoUri = data.data
        }
    }


    private fun uploadToFirebase() {
        val fileName = UUID.randomUUID().toString()
        val refStorage = FirebaseStorage
                                           .getInstance()
                                           .reference
                                           .child("ImgsPost/$fileName")

        // Si se selecciono una imagen entra a este if
        if(selectedPhotoUri != null) {
            // Esta linea sube solo la imagen a Firebase Storage
            refStorage.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    // Link con la localizacion de la foto en Firebase Storage
                    refStorage.downloadUrl.addOnSuccessListener { firestoreUrl ->
                        // Solo una vez subida la imagen con exito aStorage se sube el post a Firestore
                        uploadPostFirebase(firestoreUrl.toString())
                    }
                }

        // No se elegio una foto, subir post sin url de foto
        } else {
            uploadPostFirebase("post sin foto")
        }

    }


    private fun uploadPostFirebase(imageUrl: String) {
        val fullName = getCurrentUserName()
        val horaPost =  Timestamp(Date())
        val textoPost = textoPost.text.toString()
        val categoriaSeleccionada = categoria.selectedItem.toString()
        val idUsuario = FirebaseAuth.getInstance().uid ?: "No id"

        val post = Post(fullName, horaPost, textoPost, imageUrl, categoriaSeleccionada, idUsuario, false)
        FirebaseFirestore
            .getInstance()
            .collection("Posts")
            .add(post)
    }


    private fun getCurrentUserName() : String {
        val prefs = requireContext().getSharedPreferences(
            getString(R.string.prefs_file),
            Context.MODE_PRIVATE
        )
        val userName = prefs.getString("userName", null)
        val userLastName = prefs.getString("userLastName", null)

        //todo: manejar el caso de que savedUserName sea null, aunque nunca deberia porque RegisterFragment
        //todo: no deberia permitir registrado de un usuario sino guarda su nombre
        return "$userName $userLastName"
    }


    override fun onStop() {
        super.onStop()
        registrationListener.remove()
    }

}