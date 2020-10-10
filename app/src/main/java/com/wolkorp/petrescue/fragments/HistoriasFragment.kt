package com.wolkorp.petrescue.fragments

import android.content.Context
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.adapters.CategoriesAdapter
import com.wolkorp.petrescue.adapters.PostListAdapter
import com.wolkorp.petrescue.models.Category
import kotlinx.android.synthetic.main.fragment_historias.*
import com.wolkorp.petrescue.models.Post
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.PI


class HistoriasFragment : Fragment() {


    //LOS ARIBUTOS DEL FRAGEMNT

    //Este es el objeto que permite deslizar las categorias
    private lateinit var categoriesPager: ViewPager2

    //Una lista simple con los objetos que va a utilizar categoriesPager
    private lateinit var categoriesList: ArrayList<Category>

    private lateinit var fragmentView: View

    val db = FirebaseFirestore.getInstance()


    lateinit var recPosts: RecyclerView


    var posts: MutableList<Post> = ArrayList<Post>()
    var posts2: MutableList<String> = ArrayList<String>()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var postListAdapter: PostListAdapter
    private lateinit var mensaje: TextView
    private lateinit var texto: TextView
    private lateinit var btnAddPost : Button
    private lateinit var btnSalir: Button
    private lateinit var btnEnviar: Button
    private lateinit var btnFoto: Button
    private var link =""
    private lateinit var categoria : Spinner
    private lateinit var storageReference : StorageReference
    private final var GALLERY_INTENT = 1

    companion object {
        fun newInstance() = HistoriasFragment()
        private val PICK_IMAGE_CODE = 1000
        private val APP_NAME = "pet-rescue-4f2a1"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        fragmentView = inflater.inflate(R.layout.fragment_historias, container, false)
        recPosts = fragmentView.findViewById(R.id.rec_posts)
        mensaje = fragmentView.findViewById(R.id.mensaje_bienvenida)
        btnAddPost = fragmentView.findViewById(R.id.btnAddPost)
        storageReference  = FirebaseStorage.getInstance().getReference()

        // Boton para abrir el popup
        btnAddPost.setOnClickListener{
        val popupView: View = LayoutInflater.from(activity).inflate(R.layout.popup_addpost, null)
        val popupWindow = PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        btnSalir = popupView.findViewById(R.id.btnSalir)
        btnEnviar = popupView.findViewById(R.id.btnEnviar)
        btnFoto = popupView.findViewById(R.id.btnFotos)
        texto = popupView.findViewById(R.id.txtTexto)
        categoria = popupView.findViewById(R.id.spinnerCategorias)

        popupWindow.setFocusable(true)

            // Boton para salir del popup
        btnSalir.setOnClickListener{
            popupWindow.dismiss()
        }

        btnFoto.setOnClickListener{
            selectImageFromGallery()
        }

            // Boton para enviar el post
        btnEnviar.setOnClickListener{

            val post = Post(getCurrentUser(),obtenerHora(),texto.text.toString(),"2",link,categoria.selectedItem.toString())
            db.collection("Post").add(post)
            popupWindow.dismiss()
        }

            popupWindow.showAsDropDown(btnAddPost)
        }
        return fragmentView
    }

    private fun selectImageFromGallery() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Please select..."), PICK_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null)
        {

            // Get the Uri of data
            val file_uri = data.data
                uploadImageToFirebase(file_uri!!)

        }
    }


    private fun uploadImageToFirebase(fileUri: Uri) {
        val fileName = UUID.randomUUID().toString() +".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("ImgsPost/$fileName")
        refStorage.putFile(fileUri)
        link = "https://firebasestorage.googleapis.com/v0/b/" + APP_NAME + ".appspot.com/o/ImgsPost%2F" + fileName + "?alt=media"


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Inicializo los atributos del Fragment
        categoriesPager = view.findViewById(R.id.categoriesViewPager)
        categoriesList = ArrayList()

        createAndAddCategories()
        setUpPager()
        addUserName()


    }


    override fun onStart() {
        super.onStart()
         posts.add(Post("Lucas Perez","hace una hora","Texto del post texto del post texto del post","1","https://firebasestorage.googleapis.com/v0/b/pet-rescue-4f2a1.appspot.com/o/taking_your_dog_to_the_vet.jpeg?alt=media&token=ec10310a-69ae-493b-9f74-37c4269c2dd6","Familia"))



        recPosts.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        recPosts.layoutManager = linearLayoutManager

        postListAdapter = PostListAdapter(posts,requireContext())


        recPosts.adapter = postListAdapter

        createAndAddCategories()
        setUpPager()

        //Devuelve todos los post en firebase y los agrega a la lista que despues se muestra
        db.collection("Post")
            .orderBy("hora", Query.Direction.DESCENDING)
            .get()

            .addOnSuccessListener { snapshot ->
                if (snapshot != null) {
                    for (post in snapshot) {
                        posts.add(post.toObject())
                         mensaje.text = posts.toString()
                        mensaje.text = ""

                    }
                }
            }

    }



    private fun createAndAddCategories() {

        //Category es la clase que esta en la carpeta models y simplemente contiene dos atributos string
        //Aca el url de la imagen esta hardcodeado, porque si va a mostrar siempre las mismas imagenes y solo son 3 categorias quizas es mas simple asi
        val categoriaBuscarFamilia = Category("https://www.publinews.gt/gt/wp-content/uploads/2016/05/24/mascotas-en-familia-1.jpg", "Buscar Familia")
        val categoriaBuscarChofer = Category("https://cadenaser00.epimg.net/ser/imagenes/2014/08/04/espana/1407109825_740215_0000000000_noticia_normal.jpg", "Buscar Chofer")
        val categoriaAsesoria = Category("https://s.hdnux.com/photos/01/12/16/42/19458232/3/920x920.jpg", "Asesoria")


        categoriesList.add(categoriaBuscarFamilia)
        categoriesList.add(categoriaBuscarChofer)
        categoriesList.add(categoriaAsesoria)
    }


    private fun setUpPager() {

        //Esta es la linea de codigo que une el adapter con el pager y permite que funcinonen juntos
        categoriesPager.adapter = CategoriesAdapter(categoriesList, requireContext()) {position -> onItemClick(position)}


        //El resto de lineas en esta funcion solo estan haciendo cosas para
        // modificar como se ve el pager, pero no son escenciales para que funcione
        categoriesPager.clipToPadding = false
        categoriesPager.clipChildren = false
        categoriesPager.offscreenPageLimit = 3
        categoriesPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER


        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))

        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.95f + r * 0.05f
        }

        categoriesPager.setPageTransformer(compositePageTransformer)
    }


    //Funcion que se llama cuando el usuario toca una categoria
    //aca es donde se trabajara despues para cargar las historias de la categoria selecionada
    fun onItemClick(position: Int): Unit {

        val selectedCategory = categoriesList[position]
        val message = "La categoria seleccionda es: ${selectedCategory.categoryName}"

        Snackbar.make(fragmentView, message, Snackbar.LENGTH_LONG).show()
    }


    fun addUserName() {
        val prefs  = requireContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val savedUserName = prefs.getString("userName",null)

        mensaje_bienvenida.text = "${mensaje_bienvenida.text} $savedUserName !"
    }


    fun obtenerHora():String{
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val currentDateandTime: String = simpleDateFormat.format(Date())
        return currentDateandTime
    }


    fun getCurrentUser() : String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.email
    }
}