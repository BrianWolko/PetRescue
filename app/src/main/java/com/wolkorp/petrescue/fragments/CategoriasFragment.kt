package com.wolkorp.petrescue.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.adapters.CategoriesAdapter
import com.wolkorp.petrescue.models.Category
import com.wolkorp.petrescue.models.User
import kotlinx.android.synthetic.main.fragment_categorias.*
import kotlin.collections.ArrayList
import kotlin.math.abs


class CategoriasFragment : Fragment() {

    private lateinit var fragmentView: View
    //Este es el objeto que permite deslizar las categorias
    private lateinit var categoriesPager: ViewPager2
    //Una lista simple con los objetos que va a mostrar categoriesPager
    private var categoriesList: ArrayList<Category> = ArrayList()

    private lateinit var profileImage: ImageView
    private lateinit var helloMessage: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Inicializo los atributos  y views del Fragment
        fragmentView = inflater.inflate(R.layout.fragment_categorias, container, false)
        categoriesPager = fragmentView.findViewById(R.id.categoriesViewPager)
        profileImage = fragmentView.findViewById(R.id.foto_perfil)
        helloMessage = fragmentView.findViewById(R.id.mensaje_bienvenida)
        return fragmentView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createAndAddCategories()
        configurePager()
        addUserInfo()
    }


    private fun createAndAddCategories() {
        //Category es la clase que esta en la carpeta models y simplemente contiene dos atributos string
        //Aca el url de la imagen esta hardcodeado, porque si va a mostrar siempre las mismas imagenes y solo son 3 categorias quizas es mas simple asi
        val categoriaBuscarFamilia = Category("https://firebasestorage.googleapis.com/v0/b/pet-rescue-4f2a1.appspot.com/o/imagenesCategorias%2Fbuscar_familia.jpg?alt=media", "Buscar Familia", "Publicaciones relacionadas con ")
        val categoriaBuscarChofer = Category("https://firebasestorage.googleapis.com/v0/b/pet-rescue-4f2a1.appspot.com/o/imagenesCategorias%2Fbuscar_chofer.jpg?alt=media", "Buscar Chofer", "Busqueda de choferes para transportar mascotas")
        val categoriaAsesoria = Category("https://firebasestorage.googleapis.com/v0/b/pet-rescue-4f2a1.appspot.com/o/imagenesCategorias%2Fasesoria_mascotas.jpg?alt=media", "Asesoria", "Informacion de uso general para ....")

        categoriesList.add(categoriaBuscarFamilia)
        categoriesList.add(categoriaBuscarChofer)
        categoriesList.add(categoriaAsesoria)
    }


    private fun configurePager() {
        //Esta es la linea de codigo que une el adapter con el pager y permite que funcinonen juntos
        categoriesPager.adapter = CategoriesAdapter(categoriesList, requireContext()) { position -> onItemClick(position) }

        //El resto de lineas en esta funcion solo estan haciendo cosas para
        // modificar como se ve el pager, pero no son escenciales para que funcione
        categoriesPager.clipToPadding = false
        categoriesPager.clipChildren = false
        categoriesPager.offscreenPageLimit = 3
        categoriesPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.95f + r * 0.05f
        }

        categoriesPager.setPageTransformer(compositePageTransformer)
    }


    private fun addUserInfo() {
        /*
        // Obtiene el usuario con datos locales
        val prefs = requireContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val savedUserName = prefs.getString("userName",null)

         */

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        // Busco el usuario en firebase
        val query =  FirebaseFirestore
            .getInstance()
            .collection("Users")
            .document(currentUserId!!)


        // Actualizo nombre y foto del usuario
        query.addSnapshotListener{ document, e ->
            if (document != null) {

                val user: User = document.toObject()!!
                helloMessage.text = "Hola, ${user.userName} !"

                // Solo cargar imagen con glide si existe url de imagen del usuario
                if(user.profileImageUrl.isNotEmpty()) {
                    Glide
                        .with(fragmentView)
                        .load(user.profileImageUrl)
                        .into(profileImage)
                }

            }
        }
    }


    //Funcion que se llama cuando el usuario toca una categoria
    private fun onItemClick(position: Int) {
        val selectedCategory = categoriesList[position].categoryName
        val categoryImageUrl = categoriesList[position].imageURL

        //Guarda la categoria en las clases autogeneradas del navgraph para pasar a otro fragment
        val action = CategoriasFragmentDirections.actionCategoriasFragmentToHistoriasFragment(selectedCategory, categoryImageUrl)
        fragmentView.findNavController().navigate(action)
    }


}