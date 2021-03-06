package com.wolkorp.petrescue.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.adapters.MisMascotasAdapter
import com.wolkorp.petrescue.models.Pet


class MisMascotasFragment : Fragment() {
    private lateinit var fragmentView: View
    private lateinit var myPetsRecyclerView: RecyclerView
    var pets = ArrayList<Pet>()
    private lateinit var btnConfirmar : Button
    private lateinit var btnCancelar : Button
    private lateinit var registrationListener: ListenerRegistration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateActionBarTitle()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_mis_mascotas, container, false)
        myPetsRecyclerView = fragmentView.findViewById(R.id.recyclerView_mis_mascotas)

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
    }

    private fun configureRecyclerView(){
        myPetsRecyclerView.setHasFixedSize(true)
        myPetsRecyclerView.layoutManager = LinearLayoutManager(context)
        myPetsRecyclerView.adapter = MisMascotasAdapter(pets,requireContext()){ idMascota -> onItemDelete(idMascota) }
    }

    private fun updateActionBarTitle() {
        // Barra superior de la activity que aparece encima del fragment
        val actionBar: ActionBar? = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar?.title =  "Mis mascotas"
    }

    override fun onStart() {
        super.onStart()
        getPetsFromFirebase()
    }

    private fun  getPetsFromFirebase() {

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val query = FirebaseFirestore
            .getInstance()
            .collection("Pets")
        // todo: modificar el modelo Pet para que tenga userid y petid
         .whereEqualTo("idUsuario",userId)
         .whereEqualTo("activo",true)


        registrationListener = query.addSnapshotListener { snapshot, error ->

            if (error != null) {
                Log.d(ContentValues.TAG, "Listen failed.", error)
                return@addSnapshotListener
            }
            pets.clear()
            if (snapshot != null ) {

                for (post in snapshot) {
                    pets.add(post.toObject())

                }
                // Se tiene que llamar despues de que la lista post obtenga todos los post de firebase, sino rompe
                updateRecyclerView()

            } else {
                Log.d(ContentValues.TAG, "Current data: null")
            }

        }
    }

    private fun updateRecyclerView() {
        myPetsRecyclerView.adapter = MisMascotasAdapter(pets, requireContext()) { petId -> onItemDelete(petId) }
    }


    // se llama cuando se toca una publicacion de mascota
    fun onItemDelete (petId : String ) {
        var addPetBottomSheet = LayoutInflater
            .from(requireContext().applicationContext)
            .inflate(R.layout.bottom_sheet_delete_post, fragmentView.findViewById(R.id.bottomSheetContainer))

        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
        // Permite que se muestre completo el BottomsSheetDialog sino no se muestra por completo
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        btnConfirmar = addPetBottomSheet.findViewById(R.id.btn_confirmar)
        btnCancelar = addPetBottomSheet.findViewById(R.id.btn_cancelar)

        btnConfirmar.setOnClickListener{
            changePetPublicationActiveState(petId)
            bottomSheetDialog.dismiss()
        }

        btnCancelar.setOnClickListener{
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(addPetBottomSheet)
        bottomSheetDialog.show()

    }

    private fun changePetPublicationActiveState(id: String) {
        // todo: no esta funcionando porque el id que se pasa es el del usuario, no el id del post. Agregar al modelo post un campo idPost?
        Log.d("PetsListAdapter", "id = "+ id)
        val ref = FirebaseFirestore
            .getInstance()
            .collection("Pets")
            .document(id)
        ref.update("activo",false)
        Toast.makeText(context, "Se borró la mascota del mapa", Toast.LENGTH_LONG).show()

    }


    override fun onStop() {
        super.onStop()
        registrationListener.remove()
    }

}