package com.wolkorp.petrescue.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewSwitcher
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import com.google.firebase.firestore.ktx.toObject
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.adapters.PetsAdapter
import com.wolkorp.petrescue.models.Pet
import kotlinx.android.synthetic.main.fragment_buscar.*


class BuscarFragment : Fragment(), OnMapReadyCallback {

    //LOS ARIBUTOS DEL FRAGEMNT

    //Listener que escucha cambio en la base de datos
    private lateinit var registrationListener: ListenerRegistration

    private lateinit var recyclerView: RecyclerView
    private var petsList: ArrayList<Pet> = ArrayList()

    private lateinit var locationTextSwitcher: TextSwitcher
    private lateinit var petDescriptionTextSwitcher: TextSwitcher
    private lateinit var mapa: GoogleMap


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Inicializa el mapa
        map_view.onCreate(savedInstanceState)
        map_view.onResume()
        map_view.getMapAsync(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buscar, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Inicializo los atributos del Fragment
        recyclerView = view.findViewById(R.id.pets_list)
        locationTextSwitcher = view.findViewById(R.id.location_text_switcher)
        petDescriptionTextSwitcher = view.findViewById(R.id.description_text_switcher)

        configureRecyclerView()
        getPetsFromFirebase()
        setUpTextSwitcher()
    }


    private fun configureRecyclerView() {
        recyclerView.adapter = PetsAdapter(petsList, requireContext())
        recyclerView.layoutManager = CardSliderLayoutManager(requireContext())
        CardSnapHelper().attachToRecyclerView(recyclerView);

        //Se activa cuando se desliza  el RecyclerView
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //El RecyclerView paro de moverse
                    reciclerViewStoppedScrolling()
                }
            }
        })
    }


    //Esta es la funcion que se encarga de actulizar todas las cosas del fragment cuando
    //el reciclerView deja de moverse
    private fun reciclerViewStoppedScrolling() {

        val recivlerViewManager = recyclerView.layoutManager as CardSliderLayoutManager
        //Me da la posicion del principal item que se muestra en el recicler view
        val position = recivlerViewManager.getActiveCardPosition()

        //val petDescription = petsList.get(position).descripcion
        val latitude = petsList.get(position).latitud
        val longitude = petsList.get(position).longitud
        val fecha = petsList.get(position).fecha
        val hora = petsList.get(position).hora

        moveMapTo(latitude, longitude)
        updateText(fecha, hora)
    }


    private fun moveMapTo(latitude: Double, longitude: Double) {
        val localizacion = LatLng(latitude, longitude)

        mapa.addMarker(MarkerOptions().position(localizacion).title("$localizacion"))
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(localizacion, 14f))
    }


    private fun updateText(fecha: String, hora: String) {
        //todo: falta agregar casos de excepciones donde alguno de los datos no este disponible
        val locationMessage = "$fecha -  $hora"
        val descriptionMessage = "Aca deberia cambiar la descripcion de la mascota"

        locationTextSwitcher.setText(locationMessage)
        petDescriptionTextSwitcher.setText(descriptionMessage)
    }


    //Devuelve todos los post en firebase y los agrega a la lista que despues se muestra
    private fun getPetsFromFirebase() {
        val query =  FirebaseFirestore.getInstance().collection("Pets").orderBy("descripcion", Query.Direction.ASCENDING)

        registrationListener = query.addSnapshotListener { snapshot, error  ->
            if (error != null) {
                //todo handle error
                Toast.makeText(context, "Error cargando mascotas", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            petsList.clear()
            for (pet in snapshot!!) {
                petsList.add(pet.toObject())
            }

            //Es importante que este metodo se llame despues de haber llenado la lista
            //con los posts, sino no se muestra nada en el recyclerView
            updatePetsList()
        }
    }


    private fun updatePetsList() {
        recyclerView.adapter = PetsAdapter(petsList, requireContext())
    }


    private fun setUpTextSwitcher() {
        locationTextSwitcher.setFactory(object: ViewSwitcher.ViewFactory {
            override fun makeView() : View {
                val locationTextView = TextView(requireContext())
                return locationTextView
            }
        })

        petDescriptionTextSwitcher.setFactory(object: ViewSwitcher.ViewFactory {
            override fun makeView() : View {
                val descriptionTextView = TextView(requireContext())
                return descriptionTextView
            }
        })

        locationTextSwitcher.setText("texto de prueba")
        petDescriptionTextSwitcher.setText("Aca va la descripcion")
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
           //todo que pasa si google map es null
            mapa = googleMap
            // Agrega marcador en centro de Buenos Aires y mueve la camara
            val buenosAires = LatLng(-34.6099, -58.4290)
            mapa.addMarker(MarkerOptions().position(buenosAires).title("Ort Almagro"))
            mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(buenosAires, 11f))

    }


    override fun onStop() {
        super.onStop()
        registrationListener.remove()
    }


}