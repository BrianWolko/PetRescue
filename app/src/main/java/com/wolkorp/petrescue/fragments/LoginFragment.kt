package com.wolkorp.petrescue.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation

import com.wolkorp.petrescue.R
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {


    private val GOOGLE_SIGN_IN = 100

    /*
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)


        //Analitics event
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
        val bundle = Bundle();
        bundle.putString("message", "Integracion de Firebase completa")
        analytics.logEvent("InitSesion", bundle)

        //Setup

        //estas dos fnciones comente
        //setup()
       // session()
    }
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        //authLayout.visibility = View.VISIBLE


        //Navega hacia MainActivity falta agregarle mas
        logInButton.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_homeActivity)
        )
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
        //Cambies esta linea : this por contex!!
        val builder = AlertDialog.Builder(context!!)
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