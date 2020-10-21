package com.wolkorp.petrescue.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.adapters.MiPostsAdapter
import com.wolkorp.petrescue.adapters.PostListAdapter
import com.wolkorp.petrescue.models.Post
import kotlinx.android.synthetic.main.fragment_mis_posts.*


class MisPostsFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var miPostAdapter: MiPostsAdapter
    private lateinit var fragmentView: View
    private lateinit var rec_mis_posts: RecyclerView
    var posts = ArrayList<Post>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_mis_posts, container, false)
        rec_mis_posts = fragmentView.findViewById(R.id.rec_mis_posts)



        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        getPostsFromFirebase()


    }


    override fun onStart() {
        super.onStart()
        //posts.add(Post("Tomas Brito","15-10-2020 00:27:44","texto test texto test","2","https://firebasestorage.googleapis.com/v0/b/pet-rescue-4f2a1.appspot.com/o/ImgsPost%2F042b3a0b-151e-442d-9de0-40960bf60f58.jpg?alt=media&token=61d1b7b3-6e88-42ce-82e5-245c48eb9d4d","a",true))





        fun onItemClick ( position : Int ) : Boolean {
            Snackbar.make(fragmentView,position.toString(),Snackbar.LENGTH_SHORT).show()
            return true
        }


    }
    //Devuelve todos los post en firebase y los agrega a la lista que despues se muestra
    private fun  getPostsFromFirebase(){
        val query = db
            .collection("Post").whereEqualTo("nombre",FirebaseAuth.getInstance().currentUser?.email).whereEqualTo("activo",true)
            query.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                posts.clear()
                if (snapshot != null ) {
                    for (post in snapshot) {
                        posts.add(post.toObject())
                    }
                } else {
                    Log.d(TAG, "Current data: null")
                }
            configureRecyclerView()
            }
    }

    private fun configureRecyclerView(){
        rec_mis_posts.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        rec_mis_posts.layoutManager = linearLayoutManager

        miPostAdapter = MiPostsAdapter(posts,requireContext())
        rec_mis_posts.adapter = miPostAdapter
    }

}