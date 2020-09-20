package com.wolkorp.petrescue


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AuthActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        //Le quita la barra superior a toda esta activity
        setTheme(R.style.Theme_Design_NoActionBar)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }

}





