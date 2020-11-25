package com.wolkorp.petrescue.fragments

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.adapters.PostListAdapter
import com.wolkorp.petrescue.models.Post
import com.wolkorp.petrescue.models.User
import kotlinx.android.synthetic.main.category_container.view.*
import kotlinx.android.synthetic.main.fragment_historias.*
import java.util.*
import kotlin.collections.ArrayList


class HistoriasFragment : Fragment() {

    //LOS ARIBUTOS DEL FRAGEMNT

    private lateinit var fragmentView: View
    private lateinit var postsRecyclerView: RecyclerView
    // Lista con los objetos que va a mostrar postsRecyclerView
    private var postsList: ArrayList<Post> = ArrayList()

    private lateinit var textoPost: TextView
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

        // Argumento cargado en CategoriasFragment con el nombre de la categoria
        selectedCategory = HistoriasFragmentArgs.fromBundle(requireArguments()).selectedCategory
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inicializar las views
        fragmentView = inflater.inflate(R.layout.fragment_historias, container, false)
        postsRecyclerView = fragmentView.findViewById(R.id.rec_posts)
        postsRecyclerView.setHasFixedSize(true)

        return fragmentView
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.historias_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

  
    override fun onStart() {
        super.onStart()
        updateActionBarTitle()
        updateCollapsingToolBar()
        getPostsFromCategory(selectedCategory)
    }


    private fun updateActionBarTitle() {
        // Barra superior de la activity que aparece encima del fragment
        val actionBar: ActionBar? = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar?.title =  selectedCategory
    }


    private fun updateCollapsingToolBar() {
        val imageUrl = HistoriasFragmentArgs.fromBundle(requireArguments()).categoryImageUrl
        Glide
            .with(requireContext())
            .load(imageUrl)
            .into(collapsing_Toolbar_image)

        val categoryDescription: String
        val categoryName =  HistoriasFragmentArgs.fromBundle(requireArguments()).selectedCategory
        categoryDescription = when (categoryName) {
            "Buscar Familia" -> getString(R.string.descripcion_categoria_buscar_familia)

            "Buscar Chofer" -> getString(R.string.descripcion_categoria_buscar_chofer)

            "Asesoria" -> getString(R.string.descripcion_categoria_aesoria)

            else -> { // Note the block
                "Sin descripcion"
            }
        }


        texto_descripcion_categoria.text = categoryDescription

    }


    // Devuelve los post en firebase que coincidan con la categoria que se pasa, ordenados por hora
    private fun getPostsFromCategory(category: String) {
        val query = FirebaseFirestore
                            .getInstance()
                            .collection("Posts")
                            .whereEqualTo("categoria", category)
                            .orderBy("hora", Query.Direction.DESCENDING)

        // El registration es un listener que solo se asigna para despues poder descativarlo este listener
        // cuando el fragment no esta activo, sino gasta recursos
        registrationListener = query.addSnapshotListener { snapshot, error ->

            if (error != null) {
                //todo Falta mejorar codigo para manejar errores caso de que exista uno
                Toast.makeText(
                    context,
                    "No se pudieron obtener las historias.\nIntente de nuevo",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("HistoriasFragment", "Error getting posts: $error")
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
        postsRecyclerView.adapter  = PostListAdapter(postsList, requireContext()) { selectedPost -> onPostClick(
            selectedPost
        ) }
    }


    //Se llama cuando el usuario toca un post
    private fun onPostClick(selectedPost: Post) {
        //Guarda el post en las clases autogeneradas del navgraph que permiten pasar argumentos entre fragments
        val action = HistoriasFragmentDirections.actionHistoriasFragmentToPostDetailFragment(
            selectedPost
        )
        fragmentView.findNavController().navigate(action)
    }


    // Se llama cuando se toca el botton de la barra superior
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Boton para mostrar el BottomSheet
            R.id.menu_edit_perfil -> {
                showBottomSheetView()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showBottomSheetView() {
        // Cargar y mostrar el BottomSheet
        var addPetBottomSheet = LayoutInflater
                                        .from(requireContext().applicationContext)
                                        .inflate(
                                            R.layout.bottom_sheet_add_post,
                                            fragmentView.findViewById(
                                                R.id.bottomSheetContainer
                                            )
                                        )

        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
        // Permite que se muestre completo el BottomsSheetDialog sino no se muestra por completo
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        // Cargar atributos del bottomSheet
        textoPost = addPetBottomSheet.findViewById(R.id.texto_post)
        btnFoto = addPetBottomSheet.findViewById(R.id.btnFoto)
        categoria = addPetBottomSheet.findViewById(R.id.spinnerCategorias)
        btnEnviar = addPetBottomSheet.findViewById(R.id.btn_subir_post)


        // Boton para seleccionar una imagen del telefono
        btnFoto.setOnClickListener{
            selectImageFromGallery()
        }

        // Boton para enviar el post
        btnEnviar.setOnClickListener{
            //uploadPostToFirebase()
            //bottomSheetDialog.dismiss()

            val resultado = uploadPostToFirebase()
            if (resultado == true) {
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.setContentView(addPetBottomSheet)
        bottomSheetDialog.show()
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


    private fun uploadPostToFirebase(): Boolean {
        val fileName = UUID.randomUUID().toString()
        val refStorage = FirebaseStorage
                                           .getInstance()
                                           .reference
                                           .child("ImgsPost/$fileName")


        if(textoPost.text.toString().isEmpty()) {
            Toast.makeText(context, "La publicacion no puede estar vacia", Toast.LENGTH_LONG).show()
            return false
        }

        // Si se selecciono una imagen entra a este if
        if(selectedPhotoUri != null) {
            // Esta linea sube solo la imagen a Firebase Storage
            refStorage.putFile(selectedPhotoUri!!).addOnSuccessListener {
                // Link con la localizacion de la foto en Firebase Storage
                refStorage.downloadUrl.addOnSuccessListener { firestoreUrl ->
                    // Solo una vez subida la imagen con exito a Storage se sube el post a Firestore
                    uploadPostToFirebase(firestoreUrl.toString())
                }
            }

        // No se elegio una foto, subir post sin url de foto
        } else {
            uploadPostToFirebase("post sin foto")
        }

        return true
    }


    private fun uploadPostToFirebase(imageUrl: String) {

        val fullName = getCurrentUserName()
        val horaPost =  Timestamp(Date())
        val textoPost = textoPost.text.toString()
        val categoriaSeleccionada = categoria.selectedItem.toString()
        val idUsuario = FirebaseAuth.getInstance().uid ?: "No id"


        val id = FirebaseFirestore.getInstance().collection( "Posts").document().getId()
        val post = Post(
            id,
            fullName,
            horaPost,
            textoPost,
            imageUrl,
            categoriaSeleccionada,
            idUsuario,
            true
        )

        FirebaseFirestore.getInstance().collection("Posts").document(id).set(post)

        Log.d(TAG, "uploadPostToFirebase: $id")
        //Reseteo el uri de la foto seleccionada
        selectedPhotoUri = null
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

    private fun getUserImage(idUser : String): String {
        var user: User? = null

        val query = FirebaseFirestore.getInstance().collection("User").document(idUser)
        query.get().addOnSuccessListener{document ->
            if (document != null) {
                user = document.toObject()!!

            }

        }

        if (user != null) {
            return user!!.profileImageUrl
        } else {
            return ""
        }
    }

}