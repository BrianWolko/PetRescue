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
        val pet1 = Pet("Mascota 1", -34.6129, -58.4329, "Mar 28", "8:00", "https://thegoldenscope.files.wordpress.com/2014/05/cani-randagi-1-spiegel-de.jpg")
        val pet2 = Pet("Mascota 2", -34.5948, -58.4354, "Mar 30", "18:00","https://i.redd.it/c8z2xyougzj31.jpg")
        val pet3 = Pet("Mascota 3", -34.5924, -58.4650, "Abr 2", "16:00","https://images.newindianexpress.com/uploads/user/imagelibrary/2020/4/6/w1200X800/doggo.JPG")
        val pet4 = Pet("Mascota 4", -34.5628, -58.4984, "May 1", "7:00","https://cdnuploads.aa.com.tr/uploads/Contents/2020/04/05/thumbs_b_c_af7544b5879e3faa0eb3ebcaa6a44f20.jpg?v=21143")
        val pet5 = Pet("Mascota 5", -34.5715, -58.4205, "May 22", "12:00","https://upload.wikimedia.org/wikipedia/commons/thumb/4/4d/Stray_dogs_crosswalk.jpg/1024px-Stray_dogs_crosswalk.jpg")
        val pet6 = Pet("Mascota 6", -34.6251, -58.3973, "Jun 4", "8:00","https://yabangee.com/wp-content/uploads/Cat5.jpg")
        val pet7 = Pet("Mascota 7", -34.6092, -58.3891, "Jun 18", "8:40","https://steemitimages.com/DQmdRLBWGw6iVt5MEkNjeLwHHbVFnShNXXmYbShcyryXyt4/DSC_01612.jpg")
        val pet8 = Pet("Mascota 8", -34.5952, -58.3800, "Jul 25", "13:00","https://i.insider.com/5cd2f20c93a15226895f5ef2?width=1100&format=jpeg&auto=webp")
        val pet9 = Pet("Mascota 9", -34.6297, -58.3706, "Aug 7", "20:00","https://i.redd.it/rrcw06uuijh21.jpg")
        val pet10 = Pet("Mascota 10", -34.6595, -58.4896, "Aug 9", "16:00","https://img-aws.ehowcdn.com/750x428p/s3.amazonaws.com/cuteness_data/s3fs-public/diy_blog/Facts-About-Street-Dogs-in-Mexico.jpg")
        val pet11 = Pet("Mascota 11", -34.5200, -58.4815, "Sep 1", "22:30","https://aristotleguide.files.wordpress.com/2015/09/man-petting-strays-syntagma-athens.jpg")
        val pet12 = Pet("Mascota 12", -34.6157, -58.4178, "Sep 5", "7:30","https://myanimals.com/wp-content/uploads/2018/03/dog-in-street-461x306.jpg")


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

        //val petDescription = petsList.get(position).descripcion
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
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(localizacion, 14f))
    }


    private fun updateText(fecha: String, hora: String) {
        //todo: falta agregar casos de excepciones donde alguno de los datos no este disponible

        val locationMessage = "$fecha -  $hora"
        val descriptionMessage = "Aca deberia cambiar la descripcion de la mascota"

        locationTextSwitcher.setText(locationMessage)
        petDescriptionTextSwitcher.setText(descriptionMessage)
    }


}