package com.wolkorp.petrescue.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flaviofaria.kenburnsview.KenBurnsView
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.models.Category
import kotlinx.android.synthetic.*

class CategoriesAdapter(private var categoriesList: ArrayList<Category>, private val context: Context): RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {



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

        //Libreria que carga
        Glide
            .with(context)
            .load(categoria.imageURL)
            .into(holder.getImageView());


        holder.setName(categoria)
    }




    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        //KenBurnsView es en el fondo una ImageView pero que tiene ese efecto que mueve la imagen
        private val categoryImage: KenBurnsView
        private val categoryName: TextView


        init {
            this.categoryImage = view.findViewById(R.id.category_image)
            this.categoryName = view.findViewById(R.id.category_name)
        }


        fun setName(category: Category) {
            categoryName.text = category.categoryName
        }

        fun getImageView(): KenBurnsView {
            return categoryImage
        }

    }

}