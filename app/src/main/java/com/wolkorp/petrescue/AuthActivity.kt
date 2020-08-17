package com.wolkorp.petrescue

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.facebook.login.widget.LoginButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_auth.view.*

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        Thread.sleep(2000)
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        //Analitics event
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        val bundle = Bundle();
        bundle.putString("message", "Integracion de Firebase completa")
        analytics.logEvent("InitSesion", bundle)

        //Setup
        setup()
    }

    private fun setup(){
        title = "Autenticacion"

        //funcion de registro
        signUpButton.setOnClickListener{
            if( emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty() ){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailEditText.text.toString(),
                    passwordEditText.text.toString()).addOnCompleteListener{

                    if(it.isSuccessful){
                        showHome(it.result?.user?.email ?:"" , ProviderType.BASIC )
                    } else {
                        showAlert()
                    }
                }
            }
        }

        //funcion logIn
        logInButton.setOnClickListener{
            if( emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty() ){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(emailEditText.text.toString(),
                    passwordEditText.text.toString()).addOnCompleteListener{

                    if(it.isSuccessful){
                        showHome(it.result?.user?.email ?:"" , ProviderType.BASIC )
                    } else {
                        showAlert()
                    }
                }
            }
        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType){
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider",provider.name)
        }
        startActivity(homeIntent)
    }
}
