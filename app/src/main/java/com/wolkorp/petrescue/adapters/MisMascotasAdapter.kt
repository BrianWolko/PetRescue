package com.wolkorp.petrescue.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.models.Pet
import java.text.DateFormat

class MisMascotasAdapter(private var petsList: MutableList<Pet>,
                         var context: Context, val deletePet : (String) -> Unit
): RecyclerView.Adapter<MisMascotasAdapter.MisMascotasHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MisMascotasHolder {

        // todo: Por ahora ocupa el mismo layout que MisPostFragment en vez de
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_pet,parent, false)
        return (MisMascotasHolder(view))
    }

    override fun getItemCount(): Int {
        return petsList.size
    }


    override fun onBindViewHolder(holder: MisMascotasHolder, position: Int) {

        holder.setFecha(petsList[position].fecha)
        holder.setTexto(petsList[position].descripcion)

        Glide
            .with(context)
            .load(petsList[position].imageURL)
            .into(holder.getImageView())


        holder.getButtonDelete().setOnClickListener{
            deletePet(petsList[position].id)
        }

    }




    class MisMascotasHolder (val holderView: View) : RecyclerView.ViewHolder (holderView) {

        fun setFecha(fecha: Timestamp) {
            //Modifica el Timestamp para mostrarlo formateado en un post
            val txt: TextView= holderView.findViewById(R.id.txt_hora)
            val formattedTimeStamp = DateFormat.getDateInstance(DateFormat.MEDIUM).format(fecha.toDate())
            txt.text = formattedTimeStamp
        }


        fun setTexto(txtPost:String){
            val txt: TextView= holderView.findViewById(R.id.txt_my_pet)
            txt.text = txtPost
        }


        fun getImageView() : ImageView {
            return holderView.findViewById(R.id.img_my_pet)
        }


        fun getButtonDelete(): Button {
            return holderView.findViewById(R.id.btn_delete)
        }

    }

}

