package com.wolkorp.petrescue

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

//Tipo de login BW 18/8/2020
enum class ProviderType{
    BASIC,
    GOOGLE

}

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.mainFragment)


        val appBarConfiguration = AppBarConfiguration(setOf(R.id.buscarFragment, R.id.historiasFragment, R.id.perfilFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)

        bottomNavigationView.setupWithNavController(navController)

        /* ESTA FUNCION LA PASE A PerfilFragment
        //setup
        val bundle: Bundle?= intent.extras
        val email: String? = bundle?.getString("email")
        val provider: String? = bundle?.getString("provider")
        setup(email ?:"", provider ?:"")

        //Guardado de datos BW 18/8/2020
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider",provider)
        prefs.apply()

         */
    }



   /*  ESTA FUNCION TAMBIEN LA PASE A PerfilFragment
     private fun setup(email: String, provider: String) {

        title = "Inicio"
        emailTextView.text= email
        providerTextView.text = provider

        logOutButton.setOnClickListener{
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()


            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }*/
}