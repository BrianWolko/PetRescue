package com.wolkorp.petrescue.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.models.Pet

class PetsAdapter(private var petsList: ArrayList<Pet>, private val context: Context): RecyclerView.Adapter<PetsAdapter.PetViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {

        val petViewHolder  = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.pet_container, parent, false) as View

        return PetViewHolder(petViewHolder)
    }


    override fun getItemCount(): Int {
        return petsList.size
    }


    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {

        val pet = petsList[position]

        //Libreria que carga imagenes
        Glide
            .with(context)
            .load(pet.imageURL)
            .into(holder.getImageView())


    }




    class PetViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val petImage: ImageView


        init {
            this.petImage = view.findViewById(R.id.pet_image)
        }


        fun getImageView(): ImageView  {
            return petImage
        }

    }

}
