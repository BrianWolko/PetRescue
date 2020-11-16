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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.models.Comentario
import com.wolkorp.petrescue.models.ComentarioFull
import com.wolkorp.petrescue.models.Pet
import com.wolkorp.petrescue.models.User

import java.text.DateFormat
import java.util.*

class CommentsAdapter(private var commentsList : MutableList<ComentarioFull>, var context: Context, val idUsuario: String, val deleteComment : (String) -> Unit): RecyclerView.Adapter<CommentsAdapter.CommentsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comentario,parent, false)
        return (CommentsHolder(view))
    }

    override fun getItemCount(): Int {
        return commentsList.size
    }


    override fun onBindViewHolder(holder: CommentsHolder, position: Int) {


        holder.setTexto(commentsList[position].texto)
        holder.setName(commentsList[position].userName)
        holder.setTime(commentsList[position].hora)
        val button = holder.getButtonDelete()
        //Solo muestra el boton en los comentarios del user logueado
        if(commentsList[position].idUsuario==idUsuario){
            button.visibility = View.VISIBLE
            button.setOnClickListener{
                deleteComment(commentsList[position].id)
            }
        }


        Glide
            .with(context)
            .load(commentsList[position].userImageLink)
            .into(holder.getImageView())



    }





    class CommentsHolder (val holderView: View) : RecyclerView.ViewHolder (holderView){


        fun setTexto(txtPost:String){
            val txt: TextView= holderView.findViewById(R.id.txt_comment)
            txt.text = txtPost
        }

        fun setName(txtName : String){
            val txt : TextView = holderView.findViewById(R.id.txt_user_name)
            txt.text = txtName
        }

        fun setTime(time : Timestamp){
            val txt: TextView= holderView.findViewById(R.id.txt_hora)
            txt.text = getTime(time)
        }


        fun getImageView () : ImageView {
            return holderView.findViewById(R.id.img_comment)
        }

        fun getButtonDelete () : Button {
            return holderView.findViewById(R.id.btn_de)
        }

        // Funcion para pasar la hora del post a un string que indica hace cuando fue creado
        private fun getTime(timePost : Timestamp): String {
            var timeString : String
            val timeNow = Timestamp(Date())
            val diff : Long = timeNow.toDate().time - timePost.toDate().time
            val seconds = diff / 1000
            val minutes = seconds / 60
            if(minutes.toInt() == 0) timeString = "hace un momento" else timeString = "hace ${minutes.toInt()} minutos/s"

            if(minutes > 60){
                val hours = minutes / 60
                timeString = "hace ${hours.toInt()} hora/s"
                if(hours > 24){
                    val days = hours / 24
                    timeString = "hace ${days.toInt()} dia/s"
                }
            }

            return timeString
        }
    }

}

