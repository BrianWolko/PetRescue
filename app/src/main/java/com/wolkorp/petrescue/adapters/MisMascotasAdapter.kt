package com.wolkorp.petrescue.adapters

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.models.Pet
import com.wolkorp.petrescue.models.Post
import java.text.DateFormat

class MisMascotasAdapter(private var petsList: MutableList<Pet>,
                         var context: Context, val deleteComment : (String) -> Unit
): RecyclerView.Adapter<MisMascotasAdapter.MisMascotasHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MisMascotasHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mi_post,parent, false)
        return (MisMascotasHolder(view))
    }

    override fun getItemCount(): Int {
        return petsList.size
    }


    override fun onBindViewHolder(holder: MisMascotasHolder, position: Int) {

        holder.setHora(petsList[position].hora)
        holder.setTexto(petsList[position].texto)

        Glide
            .with(context)
            .load(postList[position].urlImg)
            .into(holder.getImageView())


        holder.getButtonDelete().setOnClickListener{
            Log.d(TAG, "onBindViewHolder: ${postList[position].idUsuario}")
            deleteComment(postList[position].id)


        }

    }




    class MisMascotasHolder (val holderView: View) : RecyclerView.ViewHolder (holderView){


        fun setFecha(fecha: DateTime) {
            //Modifica el Timestamp para mostrarlo formateado en un post
            val txt: TextView= holderView.findViewById(R.id.txt_hora)
            val formattedTimeStamp = DateFormat.getDateInstance(DateFormat.MEDIUM).format(hora.toDate())
            txt.text = formattedTimeStamp
        }

        fun setTexto(txtPost:String){
            val txt: TextView= holderView.findViewById(R.id.txt_post)
            txt.text = txtPost
        }

        fun getButtonDelete(): Button {
            return holderView.findViewById(R.id.btn_delete)
        }

        fun getImageView () : ImageView {
            return holderView.findViewById(R.id.img_post)
        }
    }

}

