package com.wolkorp.petrescue.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wolkorp.petrescue.R
import kotlinx.android.synthetic.main.fragment_login.*

import kotlinx.android.synthetic.main.fragment_register.*


class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        /*
        //Esto era lo que habia hecho Brian, falta integrarlo
        //Analitics event
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
        val bundle = Bundle();
        bundle.putString("message", "Integracion de Firebase completa")
        analytics.logEvent("InitSesion", bundle)

        */
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    override fun onStart(){
        super.onStart()
        setUpLoginButton()
    }


    //Funcion Login, se encarga de obtener el mail y la contraseña y enviarlos a Firebase
    private fun setUpLoginButton() {
        logInButton.setOnClickListener {

            //Falta por hacer asegurarse de que el mail y la contraseña  no son nil
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Loggeo exitoso, navega hacia MainActivity
                    it.findNavController().navigate(R.id.action_loginFragment_to_homeActivity)

                } else {
                    // Error en el logeo, se muestra mensaje
                    // podrian hacerse mas cosas aca ademas de mostrar un mensaje
                    Toast.makeText(context, "No se pudo ingresar a tu cuenta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



   /*
   private fun session(){
        val prefs : SharedPreferences = getSharedPreferences(getString(R.string.prefs_file),
            Context.MODE_PRIVATE)
        val email = prefs.getString("email",null)
        val provider = prefs.getString("provider",null)

        if( email != null && provider != null){
            authLayout.visibility = View.INVISIBLE
            showHome(email, ProviderType.valueOf(provider))
        }
    }

    private fun setup(){



        googleButton.setOnClickListener{

            // Configuracion
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            //val googleClient = GoogleSignIn.getClient(this,googleConf)
            //googleClient.signOut()

          //  startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN )
        }
    }


    private fun showAlert(){
        //Cambie esta linea : this por contex!!
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN) {

            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {

                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)

                if (account != null) {

                    val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{

                        if(it.isSuccessful){
                            showHome(account.email ?: "" , ProviderType.GOOGLE )
                        } else {
                            showAlert()
                        }

                    }
                }
            } catch (e: ApiException){

                showAlert()

            }
        }


    }

    */



}