package com.wolkorp.petrescue

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //Muestra imagen al momento en que se abre la aplicacion
        //Apenas carga la aplicacion va directo a AuthActivity
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}