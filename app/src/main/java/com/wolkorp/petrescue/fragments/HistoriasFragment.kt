package com.wolkorp.petrescue.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.adapters.CategoriesAdapter
import com.wolkorp.petrescue.models.Category
import kotlinx.android.synthetic.main.fragment_historias.*


class HistoriasFragment : Fragment() {


    //LOS ARIBUTOS DEL FRAGEMNT

    //Este es el objeto que permite deslizar las categorias
    private lateinit var categoriesPager: ViewPager2

    //Una lista simple con los objetos que va a utilizar categoriesPager
    private lateinit var categoriesList : ArrayList<Category>

    private lateinit var fragmentView: View



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentView =  inflater.inflate(R.layout.fragment_historias, container, false)
        return fragmentView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Inicializo los atributos del Fragment
        categoriesPager = view.findViewById(R.id.categoriesViewPager)
        categoriesList = ArrayList()

        createAndAddCategories()
        setUpPager()

    }


    override fun onStart() {
        super.onStart()

        addUserName()
        Toast.makeText(context, "Bienvenido!", Toast.LENGTH_LONG).show()
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

        mensaje_bienvenida.text = "${mensaje_bienvenida.text} $savedUserName"
    }


}