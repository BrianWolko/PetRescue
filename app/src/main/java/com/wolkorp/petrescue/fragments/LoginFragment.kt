package com.wolkorp.petrescue.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.models.User
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var loginView: View
    private val GOOGLE_SIGN_IN = 1


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
        loginView = inflater.inflate(R.layout.fragment_login, container, false)
        return loginView
    }


    override fun onStart(){
        super.onStart()
        setUpLoginButton()
        setUpLoginWithGoogle()
    }


    //Funcion Login, se encarga de obtener el mail y la contraseña y enviarlos a Firebase
    private fun setUpLoginButton() {
        logInButton.setOnClickListener {

            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            //Si alguno de los campos esta vacio, sale de la funcion
            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Los campos no pueden estar vacios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Loggeo exitoso, navega hacia MainActivity
                    saveMail(email)
                    it.findNavController().navigate(R.id.action_loginFragment_to_homeActivity)

                } else {
                    // Error en el logeo, se muestra mensaje
                    // podrian hacerse mas cosas aca ademas de mostrar un mensaje
                    Toast.makeText(
                        context,
                        "No se pudo ingresar a tu cuenta. \nIntenta de nuevo",
                        Toast.LENGTH_SHORT
                    ).show()
                    print(task.result)
                }
            }

                .addOnFailureListener {
                    print("Fallo el login y aca esta el error:")
                    print(it.localizedMessage)
                }
        }
    }


    private fun saveMail(email: String) {
        val prefs = requireContext().getSharedPreferences(
            getString(R.string.prefs_file),
            Context.MODE_PRIVATE
        ).edit()
        prefs.putString("email", email)
        prefs.apply()
    }


    private fun setUpLoginWithGoogle() {

        google_sign_in.setOnClickListener {

            // Configuracion
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(requireContext(), googleConf)

            // todo: Averiguar si esta linea es necesaria
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }


   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            //Handle Sign In result
            try {

                Toast.makeText(context, "Verificando Cuenta", Toast.LENGTH_LONG).show()
                //Esta linea esta fallando, o es algo de la consola en firebase
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                val credential: AuthCredential = GoogleAuthProvider.getCredential(
                    account?.idToken,
                    null
                )


                auth.signInWithCredential(credential).addOnCompleteListener{
                    if(it.isSuccessful && it.isComplete) {

                        saveUser()
                    }
                }

            } catch (error: ApiException) {
                Toast.makeText(
                    context,
                    "Error ingresando con Google. \nIntenta de nuevo",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("LoginFragment", "$error")
            }
        }
   }


    private fun saveUser() {


        val acct = GoogleSignIn.getLastSignedInAccount(activity)
       // todo: asegurarse que acct no es null
        val personName = acct?.givenName!!
        val personFamilyName = acct?.familyName!!
        val personEmail = acct?.email!!


        saveUserLocally(personEmail, personName, personFamilyName)

        //Ocupa el mismo uid de FirebaseAuth como id del documento de firestore
        val uid = FirebaseAuth.getInstance().uid ?: "No id"
        //Al momento de registrarse el numero de telefono y el url de la imagen van vacios
        val user = User(uid, personName, personFamilyName, personEmail, "", "", "")

        FirebaseFirestore
            .getInstance()
            .collection("Users")
            .document(uid)
            .set(user)
            .addOnSuccessListener {

                Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show()
                loginView.findNavController().navigate(R.id.action_loginFragment_to_homeActivity)
            }
            .addOnFailureListener { error ->

                //todo podria mejorarse el manejo de errores
                Toast.makeText(
                    context,
                    "No se guardar tu cuenta. \nIntenta de nuevo",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("LoginFragment", "Error creando usuario $error")
            }
    }


    private fun saveUserLocally(email: String, userName: String, userLastName: String) {
        val prefs = requireContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("userName", userName)
        prefs.putString("userLastName", userLastName)
        prefs.apply()
    }


}