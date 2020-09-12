package com.wolkorp.petrescue.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flaviofaria.kenburnsview.KenBurnsView
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.models.Category
import kotlinx.android.synthetic.*

class CategoriesAdapter(): RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {


    private lateinit var categoriesList: ArrayList<Category>
    private lateinit var context: Context
    private lateinit var onItemClick: (Int) -> Unit


    constructor(categoriesList: ArrayList<Category>, context: Context, onItemClick: (Int) -> Unit) : this() {
        this.categoriesList = categoriesList
        this.context = context
        this.onItemClick = onItemClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {

        val categoryViewHolder  = LayoutInflater
                                    .from(parent.context)
                                    .inflate(R.layout.category_container, parent, false) as View

        return CategoryViewHolder (categoryViewHolder)
    }


    override fun getItemCount(): Int {
        return categoriesList.size
    }


    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        val categoria = categoriesList[position]

        //Libreria que puede cargar imagenes pasandole el url de la imagen
        Glide
            .with(context)
            .load(categoria.imageURL)
            .into(holder.getImageView());


        holder.setName(categoria)

        holder.getCardLayout().setOnClickListener {
            onItemClick(position)
        }
    }




    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        //KenBurnsView es en el fondo una ImageView pero que tiene ese efecto que mueve la imagen
        private val categoryImage: KenBurnsView
        private val categoryName: TextView
        private val categoryCardView: CardView


        init {
            this.categoryImage = view.findViewById(R.id.category_image)
            this.categoryName = view.findViewById(R.id.category_name)
            this.categoryCardView = view.findViewById(R.id.category_cardView)
        }


        fun setName(category: Category) {
            categoryName.text = category.categoryName
        }

        fun getImageView(): KenBurnsView {
            return categoryImage
        }

        fun getCardLayout(): CardView {
            return categoryCardView
        }

    }

}