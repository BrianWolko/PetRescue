package com.wolkorp.petrescue.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.models.Post
import kotlinx.coroutines.tasks.await

class MiPostsAdapter(private var postList : MutableList<Post>,var context: Context): RecyclerView.Adapter<MiPostsAdapter.MiPostHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var idPost : String

    companion object {
        private val TAG = "PostListAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiPostHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mi_post,parent, false)
        return (MiPostHolder(view))
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    fun setData(newData : ArrayList<Post>){
        this.postList = newData
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MiPostHolder, position: Int) {

        holder.setHora(postList[position].hora)
        holder.setTexto(postList[position].texto)

        Glide
            .with(context)
            .load(postList[position].urlImg)
            .into(holder.getImageView());


        holder.getButtonDelete().setOnClickListener{

            editPost(postList[position].id)
            Log.d(TAG, "onBindViewHolder: " + postList[position].id)
        }

    }




    private fun editPost(postId: String){
        Log.d(TAG, "id = "+ postId)
        val ref = db.collection("Post").document(postId)
        ref.update("activo",false)
    }


    class MiPostHolder (v: View) : RecyclerView.ViewHolder (v){
        private var view : View

        init {
            this.view = v
        }


        fun setHora(hora:String){
            val txt: TextView= view.findViewById(R.id.txt_hora)
            txt.text = hora
        }

        fun setTexto(txtPost:String){
            val txt: TextView= view.findViewById(R.id.txt_post)
            txt.text = txtPost
        }


        fun getButtonDelete(): Button {
            return view.findViewById(R.id.btn_delete)
        }

        fun getImageView () : ImageView {
            return view.findViewById(R.id.img_post)
        }

    }
}

