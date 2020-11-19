package com.wolkorp.petrescue.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.models.Post
import com.wolkorp.petrescue.models.User
import java.text.DateFormat

class PostListAdapter(private var postList: MutableList<Post>, var context: Context, var onPostClick: (Post) -> Unit): RecyclerView.Adapter<PostListAdapter.PostHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return (PostHolder(view))
    }

    override fun getItemCount(): Int {
        return postList.size
    }


    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.setName(postList[position].nombreUsuario)
        holder.setHora(postList[position].hora)
        holder.setTexto(postList[position].texto)



        Glide
            .with(context)
            .load(postList[position].urlImg)
            .into(holder.getImageView())

        holder.getCardLayout().setOnClickListener {
            onPostClick(postList[position])
        }

        val query = FirebaseFirestore.getInstance().collection("Users").document(postList[position].idUsuario)



        query.addSnapshotListener{ document,e ->
            if (document != null) {
                val user : User = document.toObject()!!
                Glide
                    .with(context)
                    .load(user.profileImageUrl)
                    .into(holder.getProfileImageView())
            }
        }



    }


    class PostHolder(v: View) : RecyclerView.ViewHolder(v){

        private var view : View

        init {
            this.view = v
        }


        fun setName(name: String){
            val userName : TextView=view.findViewById(R.id.txt_user_name)
            userName.text = name
        }


        fun setHora(postTimeStamp: Timestamp) {
            val time: TextView= view.findViewById(R.id.txt_hora)

            //MOdifica el Timestamp para mostrarlo formateado en un post
            val formattedTimeStamp = DateFormat.getDateInstance(DateFormat.MEDIUM).format(postTimeStamp.toDate())
            time.text = formattedTimeStamp
        }


        fun setTexto(txtPost: String){
            val txt: TextView= view.findViewById(R.id.txt_post)
            txt.text = txtPost
        }


        fun getCardLayout(): CardView {
            return view.findViewById(R.id.card_package_item)
        }


        fun getImageView () : ImageView {
           return view.findViewById(R.id.img_post)
       }

        fun getProfileImageView() : ImageView{
            return view.findViewById(R.id.img_profile_comment)
        }

    }
}