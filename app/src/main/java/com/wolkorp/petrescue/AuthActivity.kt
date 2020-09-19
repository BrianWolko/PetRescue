package com.wolkorp.petrescue


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {

        //Le quita la barra superior a toda esta activity
        setTheme(R.style.Theme_Design_NoActionBar)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        //Esto ver si lo muevo a la primera pantalla que apareceria al lanzar la app
        //auth = Firebase.auth

    }



    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
       // val currentUser = auth.currentUser

        //Cambiar esta funcion por una que navegue irectamnete a MainActivity si ya esta regustrado
        //updateUI(currentUser)
    }
}





