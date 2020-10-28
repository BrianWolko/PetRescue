package com.wolkorp.petrescue.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.adapters.MiPostsAdapter
import com.wolkorp.petrescue.models.Post


class MisPostsFragment : Fragment() {

    private lateinit var fragmentView: View
    private lateinit var myPostsRecyclerView: RecyclerView
    var posts = ArrayList<Post>()
    private lateinit var registrationListener: ListenerRegistration



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateActionBarTitle()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_mis_posts, container, false)
        myPostsRecyclerView = fragmentView.findViewById(R.id.recyclerView_mis_posts)

        return fragmentView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
    }


    private fun configureRecyclerView(){
        myPostsRecyclerView.setHasFixedSize(true)
        myPostsRecyclerView.layoutManager = LinearLayoutManager(context)
        myPostsRecyclerView.adapter = MiPostsAdapter(posts,requireContext())
    }


    private fun updateActionBarTitle() {
        // Barra superior de la activity que aparece encima del fragment
        val actionBar: ActionBar? = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar?.title =  "Mis publicaciones"
    }


    override fun onStart() {
        super.onStart()
        getPostsFromFirebase()
    }


    //Devuelve todos los post en firebase y los agrega a la lista que despues se muestra
    private fun  getPostsFromFirebase() {

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val query = FirebaseFirestore
                            .getInstance()
                            .collection("Posts")
                            .whereEqualTo("idUsuario",userId)
                            .whereEqualTo("activo",true)

        registrationListener = query.addSnapshotListener { snapshot, error ->

            if (error != null) {
                Log.d(TAG, "Listen failed.", error)
                return@addSnapshotListener
            }
            posts.clear()
            if (snapshot != null ) {

                for (post in snapshot) {
                    posts.add(post.toObject())

                }
                // Se tiene que llamar despues de que la lista post obtenga todos los post de firebase, sino rompe
                updateRecyclerView()

            } else {
                Log.d(TAG, "Current data: null")
            }

        }
    }


    // encargada de actulizar la lista del recyclerView una vez obtenidos de Firebase
    private fun updateRecyclerView() {
        myPostsRecyclerView.adapter = MiPostsAdapter(posts,requireContext())
    }


    // se llama cuando se toca un post
    fun onItemClick ( position : Int ) {
        Snackbar.make(fragmentView, position.toString(), Snackbar.LENGTH_SHORT).show()
        // todo: mostrar comentarios del post cuando se toque uno
    }


    override fun onStop() {
        super.onStop()
        registrationListener.remove()
    }


}