package com.wolkorp.petrescue.fragments

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.adapters.CommentsAdapter
import com.wolkorp.petrescue.models.Comentario
import com.wolkorp.petrescue.models.ComentarioFull
import com.wolkorp.petrescue.models.User
import kotlinx.android.synthetic.main.fragment_post_detail.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log


class PostDetailFragment : Fragment() {

    private lateinit var fragmentView: View
    private lateinit var commentsRecyclerView: RecyclerView
    var commentsList = ArrayList<Comentario>()
    var commentsFullList = ArrayList<ComentarioFull>()

    private lateinit var profile_img : ImageView
    private lateinit var profile_img_current : ImageView

    private lateinit var txt_username : TextView
    private lateinit var txt_fecha : TextView
    private lateinit var editTextComment : EditText
    private lateinit var btnComentar : Button


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_post_detail, container, false)
        commentsRecyclerView = fragmentView.findViewById(R.id.recyclerView_comments)
        profile_img = fragmentView.findViewById(R.id.profile_img)
        profile_img_current = fragmentView.findViewById(R.id.profile_img_current)
        txt_username = fragmentView.findViewById(R.id.txt_username)
        txt_fecha = fragmentView.findViewById(R.id.txt_fecha)
        editTextComment = fragmentView.findViewById(R.id.editText_comment)
        btnComentar = fragmentView.findViewById(R.id.btn_enviarComment)

        return fragmentView
    }



    override fun onStart() {
        super.onStart()
        val post  = PostDetailFragmentArgs.fromBundle(requireArguments()).selectedPost
        configureRecyclerView()
        getCommentsFromFirebase(post.id)

        post_detail_textview.text = post.texto
        txt_fecha.text = getTime(post.hora)

        updateCurrentImage()
        getUserFromFirebase(post.idUsuario)

        setButton(post.id)


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    private fun getUserFromFirebase(idUsuario : String){
        val query = FirebaseFirestore.getInstance().collection("Users").document(idUsuario)
        query.addSnapshotListener { document, error ->
            if (error != null) {
                Log.d(ContentValues.TAG, "Listen failed.", error)
                return@addSnapshotListener
            }
            if (document != null) {
                val user: User = document.toObject()!!
                updateImage(user.profileImageUrl)
                txt_username.text ="${user.userName} ${user.userLastName}"

            }
        }

    }


    // funcion para crear una lista de comentarios con la informacion del usuario, lista que se usa en el recycler
    private fun getUserFromFirebase(idUsuario : String,comment : Comentario){
        val query = FirebaseFirestore.getInstance().collection("Users").document(idUsuario)
        query.addSnapshotListener { document, error ->
            if (error != null) {
                Log.d(ContentValues.TAG, "Listen failed.", error)
                return@addSnapshotListener
            }
            if (document != null) {
                val user: User = document.toObject()!!
                val userName = "${user.userName} ${user.userLastName}"
                commentsFullList.add(ComentarioFull(comment.texto,idUsuario,comment.hora,userName,user.profileImageUrl,comment.id))

            }

            configureRecyclerView()
        }


    }

    private fun updateImage(link : String){
        Glide
            .with(requireContext())
            .load(link)
            .into(profile_img)
    }

    //Actualiza la imagen del usuario que esta usando la aplicacion en el sector para hacer un nuevo comentario
    private fun updateCurrentImage(){
        val currentUser = Firebase.auth.currentUser!!.uid
        val query = FirebaseFirestore.getInstance().collection("Users").document(currentUser)
        query.addSnapshotListener{ document, error ->
            if (error != null) {
                Log.d(ContentValues.TAG, "Listen failed.", error)
                return@addSnapshotListener
            }
            if (document != null) {
                val user: User = document.toObject()!!
                Glide
                    .with(requireContext())
                    .load(user.profileImageUrl)
                    .into(profile_img_current)

            }

        }


    }

    //setea el boton para enviar el comentario
    private fun setButton(idPost : String){
        btnComentar.setOnClickListener{
            val textoComentario = editTextComment.text.toString()
            if(textoComentario.isEmpty()){
                Toast.makeText(context, "Ingrese algun mensaje", Toast.LENGTH_SHORT).show()
            }else{


            val idUsuario = Firebase.auth.currentUser!!.uid

            val idComment = FirebaseFirestore.getInstance().collection("Posts").document(idPost).collection("comments").document().getId()
            val comment = Comentario(textoComentario, idComment, idUsuario, Timestamp(Date()))
            FirebaseFirestore.getInstance().collection("Posts").document(idPost).collection("comments").document(idComment).set(comment)

            commentsList.add(comment)
            editTextComment.text.clear()
            Toast.makeText(context, "Comentario enviado", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun configureRecyclerView(){
        commentsRecyclerView.setHasFixedSize(true)
        commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        commentsRecyclerView.adapter = CommentsAdapter(commentsFullList,requireContext(), Firebase.auth.currentUser!!.uid){ idComentario -> deleteComment(idComentario )}
    }

    private fun getCommentsFull(){

        for (comment in commentsList){
            getUserFromFirebase(comment.idUsuario, comment)
        }

    }

    private fun getCommentsFromFirebase(idPost : String){

        val query = FirebaseFirestore
            .getInstance()
            .collection("Posts").document(idPost).collection("comments")
            .orderBy("hora")
        query.addSnapshotListener { snapshot, error ->

            if (error != null) {
                Log.d(ContentValues.TAG, "Listen failed.", error)
                return@addSnapshotListener
            }
            commentsList.clear()
            commentsFullList.clear()
            if (snapshot != null ) {

                for (post in snapshot) {
                    commentsList.add(post.toObject())

                }
                // Se tiene que llamar despues de que la lista post obtenga todos los post de firebase, sino rompe
                configureRecyclerView()

            } else {
                Log.d(ContentValues.TAG, "Current data: null")
            }
            getCommentsFull()
        }

    }

    //Funcion para enviar al recycler, para eliminar un comentario
    private fun deleteComment(idComentario : String){
        Log.d(TAG, "deleteComment: borrando comentario con id $idComentario")

        val post = PostDetailFragmentArgs.fromBundle(requireArguments()).selectedPost
        Log.d(TAG, "deleteComment: en el post con id ${post.id}")
        FirebaseFirestore.getInstance().collection("Posts").document(post.id).collection("comments").document(idComentario).delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

    }

}