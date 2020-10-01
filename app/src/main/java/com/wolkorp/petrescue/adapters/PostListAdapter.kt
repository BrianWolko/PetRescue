package com.wolkorp.petrescue.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.models.Post

class PostListAdapter(private var postList : MutableList<Post>,var context: Context): RecyclerView.Adapter<PostListAdapter.PostHolder>() {


    companion object {
        private val TAG = "PostListAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent, false)
        return (PostHolder(view))
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    fun setData(newData : ArrayList<Post>){
        this.postList = newData
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.setName(postList[position].nombre)
        holder.setHora(postList[position].hora)
        holder.setTexto(postList[position].texto)

        Glide
            .with(context)
            .load(postList[position].urlImg)
            .into(holder.getImageView());




    }

    class PostHolder (v: View) : RecyclerView.ViewHolder (v){
        private var view : View

        init {
            this.view = v
        }

        fun setName(name:String){
            val txt : TextView=view.findViewById(R.id.txt_item)
            txt.text = name
        }

        fun setHora(hora:String){
            val txt: TextView= view.findViewById(R.id.txt_hora)
            txt.text = hora
        }

        fun setTexto(txtPost:String){
            val txt: TextView= view.findViewById(R.id.txt_post)
            txt.text = txtPost
        }


        fun getCardLayout(): CardView {
            return view.findViewById(R.id.card_package_item)
        }

        fun getImageView () : ImageView {
           return view.findViewById(R.id.img_post)
       }

    }
}