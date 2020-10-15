package com.wolkorp.petrescue.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.adapters.CategoriesAdapter
import com.wolkorp.petrescue.models.Category
import kotlinx.android.synthetic.main.fragment_categorias.*
import kotlin.collections.ArrayList
import kotlin.math.abs


class CategoriasFragment : Fragment() {


    private lateinit var fragmentView: View
    //Este es el objeto que permite deslizar las categorias
    private lateinit var categoriesPager: ViewPager2
    //Una lista simple con los objetos que va a mostrar categoriesPager
    private lateinit var categoriesList: ArrayList<Category>




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Inicializo los atributos  y views del Fragment
        fragmentView = inflater.inflate(R.layout.fragment_categorias, container, false)
        categoriesPager = fragmentView.findViewById(R.id.categoriesViewPager)
        categoriesList = ArrayList()

        return fragmentView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createAndAddCategories()
        configurePager()
        addUserName()
    }


    private fun createAndAddCategories() {
        //Category es la clase que esta en la carpeta models y simplemente contiene dos atributos string
        //Aca el url de la imagen esta hardcodeado, porque si va a mostrar siempre las mismas imagenes y solo son 3 categorias quizas es mas simple asi
        val categoriaBuscarFamilia = Category("https://firebasestorage.googleapis.com/v0/b/pet-rescue-4f2a1.appspot.com/o/imagenesCategorias%2Fbuscar_familia.jpg?alt=media", "Buscar Familia", "Publicaciones relacionadas con ")
        val categoriaBuscarChofer = Category("https://firebasestorage.googleapis.com/v0/b/pet-rescue-4f2a1.appspot.com/o/imagenesCategorias%2Fbuscar_chofer.jpg?alt=media", "Buscar Chofer", "Busqueda de choferes para transportar mascotas")
        val categoriaAsesoria = Category("https://firebasestorage.googleapis.com/v0/b/pet-rescue-4f2a1.appspot.com/o/imagenesCategorias%2Fasesoria_mascotas.jpg?alt=media", "Asesoria", "Informacion general par ayudar ....")

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


    private fun addUserName() {
        val prefs = requireContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val savedUserName = prefs.getString("userName",null)

        mensaje_bienvenida.text = "${mensaje_bienvenida.text} $savedUserName !"
    }


    //Funcion que se llama cuando el usuario toca una categoria
    //aca es donde se trabajara despues para cargar las historias de la categoria selecionada
    private fun onItemClick(position: Int) {
        val selectedCategory = categoriesList[position].categoryName

        //Guarda la categoria en las clases autogeneradas del navgraph para pasar a otro fragment
        val action = CategoriasFragmentDirections.actionCategoriasFragmentToHistoriasFragment(selectedCategory)
        fragmentView.findNavController().navigate(action)
    }


}