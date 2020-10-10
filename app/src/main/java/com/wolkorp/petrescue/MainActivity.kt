package com.wolkorp.petrescue

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

//Tipo de login BW 18/8/2020
enum class ProviderType{
    BASIC,
    GOOGLE

}

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //Asegurarse que esta linea no rompa nada
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //La barra de navegcion inferior
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        //El fragmento principal que es el punto de entrada a los demas fragmentos
        val navController = findNavController(R.id.mainFragment)

        //Permite que la barra superior cambie su titulo a el del fragmento que este activo
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.buscarFragment, R.id.categoriasFragment, R.id.perfilFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)

        //Hace funcionar la navegacion de la barra inferior (BottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

    }




}