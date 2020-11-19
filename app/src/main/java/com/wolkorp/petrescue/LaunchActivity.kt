package com.wolkorp.petrescue

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


//Esta clase sirve para mostrar la imagen /drawable/ic_launch_scren.xml  al momento que el usuario
//abre la app mientras carga el resto del codigo
class LaunchActivity : AppCompatActivity() {

    //Se utiliza en esta activity para ver si el susario ya esta loggeado o no
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Firebase.auth.signOut()

        //Para nevagar entre activities
        val intent: Intent

        //Obtengo el usuario actual
        auth = Firebase.auth
        val currentUser = auth.currentUser

        if (currentUser == null) {
            //El usuario NO esta loggeado asi que va a AuthActivity
             intent = Intent(this, AuthActivity::class.java)

        } else {
            //El usuario esta logeado asi que va directo a MainActivity
             intent = Intent(this, HomeActivity::class.java)
        }

        startActivity(intent)

       //Esta funcion es para que solo muestre la imagen al abrir la app
       //evitando que al presionar el boton hacia atras se vuelva a mostrar la imagen
        finish()
    }
}