package com.wolkorp.petrescue.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.models.User
import kotlinx.android.synthetic.main.fragment_perfil.*


class PerfilFragment : Fragment() {

    private lateinit var user: User
    private lateinit var fragmentView : View
    private lateinit var nombre : TextView
    private lateinit var pais : TextView
    private lateinit var email : TextView
    private lateinit var numero: TextView
    private lateinit var profileImage : ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_perfil, container, false)
        nombre = fragmentView.findViewById(R.id.nombre)
        pais = fragmentView.findViewById(R.id.pais)
        email = fragmentView.findViewById(R.id.email)
        numero = fragmentView.findViewById(R.id.numero)
        profileImage = fragmentView.findViewById(R.id.profile_img)

        return fragmentView
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.perfil_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onStart() {
        super.onStart()
        loadUserData()

        btn_ver_posts_activos.setOnClickListener {
            it.findNavController().navigate(R.id.action_perfilFragment_to_misPostsFragment)
        }

        btn_ver_mascotas.setOnClickListener {
            it.findNavController().navigate(R.id.action_perfilFragment_to_misMascotasFragment)
        }

        logOutButton.setOnClickListener {
            logOut()
        }
    }

    private fun loadUserData() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if(currentUserId != null) {
            val query =  FirebaseFirestore
                                            .getInstance()
                                            .collection("Users")
                                            .document(currentUserId)


                query.addSnapshotListener{ document, e ->
                if (document != null) {

                    user = document.toObject()!!
                    nombre.text ="${user.userName} ${user.userLastName}"
                    pais.text = user.pais
                    email.text = user.email
                    numero.text = user.phoneNumber
                    updateImage(user.profileImageUrl)
                }
            }


        }

    }


    private fun updateImage(link : String){
        // Solo cargar imagen con glide si existe url de imagen del usuario
        if(user.profileImageUrl.isNotEmpty()) {
            Glide
                .with(fragmentView)
                .load(link)
                .into(profileImage)
        }
    }



    // Se llama cuando se toca el botton de la barra superior
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.menu_edit_perfil -> {
                //Guarda el usuario en las clases autogeneradas del navgraph para pasarlo a otro fragment
                val action = PerfilFragmentDirections.actionPerfilFragmentToEditarPerfilFragment(this.user)
                fragmentView.findNavController().navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Limpia las shared preferences, sale de la cuenta
    private fun logOut() {
        val prefs = requireContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()

        Firebase.auth.signOut()
        fragmentView.findNavController().navigate(R.id.action_perfilFragment_to_authActivity)
    }

}