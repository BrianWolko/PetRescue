package com.wolkorp.petrescue.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.adapters.PetsAdapter
import com.wolkorp.petrescue.models.Pet
import kotlinx.android.synthetic.main.fragment_buscar.*


class BuscarFragment : Fragment(), OnMapReadyCallback {

    //LOS ARIBUTOS DEL FRAGEMNT
    private lateinit var fragmentView: View
    private lateinit var petsList: ArrayList<Pet>
    private lateinit var reciclerView: RecyclerView

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
        fragmentView = inflater.inflate(R.layout.fragment_buscar, container, false)
        return fragmentView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Inicializo los atributos del Fragment
        petsList = ArrayList()
        reciclerView = view.findViewById(R.id.pets_list)
        locationTextSwitcher = view.findViewById(R.id.location_text_switcher)
        petDescriptionTextSwitcher = view.findViewById(R.id.description_text_switcher)

        createAndAddPets()
        setUpReciclerView()
        setUpTextSwitcher()
    }


    private fun createAndAddPets() {

        //Por ahora para probar que funciona dejo esta parte hardcodeada
        val pet1 = Pet("Mascota 1", -34.6129, -58.4329, "Mar 28", "8:00", "https://scx1.b-cdn.net/csz/news/800/2018/2-dog.jpg")
        val pet2 = Pet("Mascota 2", -34.5948, -58.4354, "Mar 30", "18:00","https://image.cnbcfm.com/api/v1/image/105992231-1561667465295gettyimages-521697453.jpeg?v=1561667497&w=630&h=354")
        val pet3 = Pet("Mascota 3", -34.5924, -58.4650, "Abr 2", "16:00","https://www.zooplus.ie/magazine/wp-content/uploads/2020/01/Female-Dogs-in-Heat-IE-768x512.jpeg")
        val pet4 = Pet("Mascota 4", -34.5628, -58.4984, "May 1", "7:00","https://scx1.b-cdn.net/csz/news/800/2018/2-dog.jpg")
        val pet5 = Pet("Mascota 5", -34.5715, -58.4205, "May 22", "12:00","https://image.cnbcfm.com/api/v1/image/105992231-1561667465295gettyimages-521697453.jpeg?v=1561667497&w=630&h=354")
        val pet6 = Pet("Mascota 6", -34.6251, -58.3973, "Jun 4", "8:00","https://www.zooplus.ie/magazine/wp-content/uploads/2020/01/Female-Dogs-in-Heat-IE-768x512.jpeg")
        val pet7 = Pet("Mascota 7", -34.6092, -58.3891, "Jun 18", "8:40","https://scx1.b-cdn.net/csz/news/800/2018/2-dog.jpg")
        val pet8 = Pet("Mascota 8", -34.5952, -58.3800, "Jul 25", "13:00","https://image.cnbcfm.com/api/v1/image/105992231-1561667465295gettyimages-521697453.jpeg?v=1561667497&w=630&h=354")
        val pet9 = Pet("Mascota 9", -34.6297, -58.3706, "Aug 7", "20:00","https://www.zooplus.ie/magazine/wp-content/uploads/2020/01/Female-Dogs-in-Heat-IE-768x512.jpeg")
        val pet10 = Pet("Mascota 10", -34.6595, -58.4896, "Aug 9", "16:00","https://scx1.b-cdn.net/csz/news/800/2018/2-dog.jpg")
        val pet11 = Pet("Mascota 11", -34.5200, -58.4815, "Sep 1", "22:30","https://image.cnbcfm.com/api/v1/image/105992231-1561667465295gettyimages-521697453.jpeg?v=1561667497&w=630&h=354")
        val pet12 = Pet("Mascota 12", -34.6157, -58.4178, "Sep 5", "7:30","https://www.zooplus.ie/magazine/wp-content/uploads/2020/01/Female-Dogs-in-Heat-IE-768x512.jpeg")

        petsList.add(pet1)
        petsList.add(pet2)
        petsList.add(pet3)
        petsList.add(pet4)
        petsList.add(pet5)
        petsList.add(pet6)
        petsList.add(pet7)
        petsList.add(pet8)
        petsList.add(pet9)
        petsList.add(pet10)
        petsList.add(pet11)
        petsList.add(pet12)
    }


    private fun setUpReciclerView() {
        reciclerView.adapter = PetsAdapter(petsList, requireContext())
        reciclerView.layoutManager = CardSliderLayoutManager(requireContext())
        CardSnapHelper().attachToRecyclerView(reciclerView);

        //Se activa cuando se desliza  el RecyclerView
        reciclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //El RecyclerView paro de moverse
                    reciclerViewStoppedScrolling()
                }
            }
        })
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
        if(googleMap != null) {
            mapa = googleMap
            // Agrega marcador en centro de Buenos Aires y mueve la camara
            val buenosAires = LatLng(-34.6099, -58.4290)
            mapa.addMarker(MarkerOptions().position(buenosAires).title("Ort Almagro"))
            mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(buenosAires, 11f))
        }
    }


    //Esta es la funcion que se encarga de actulizar todas las cosas del fragment cuando
    //el reciclerView deja de moverse
    private fun reciclerViewStoppedScrolling() {

        val recivlerViewManager = reciclerView.layoutManager as CardSliderLayoutManager
        //Me da la posicion del principal item que se muestra en el recicler view
        val position = recivlerViewManager.getActiveCardPosition()

        val petName = petsList.get(position).name
        val latitude = petsList.get(position).latidud
        val longitude = petsList.get(position).longitud
        val fecha = petsList.get(position).fecha
        val hora = petsList.get(position).hora

        moveMapTo(latitude, longitude)
        updateText(fecha, hora)
    }


    private fun moveMapTo(latitude: Double, longitude: Double) {
        val localizacion = LatLng(latitude, longitude)

        mapa.addMarker(MarkerOptions().position(localizacion).title("Perrito"))
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(localizacion, 15f))
    }


    private fun updateText(fecha: String, hora: String) {
        //todo: falta agregar casos de excepciones donde alguno de los datos no este disponible

        val locationMessage = "$fecha -  $hora"
        val descriptionMessage = "Aca deberia cambiar la descripcion de la mascota"

        locationTextSwitcher.setText(locationMessage)
        petDescriptionTextSwitcher.setText(descriptionMessage)
    }


}