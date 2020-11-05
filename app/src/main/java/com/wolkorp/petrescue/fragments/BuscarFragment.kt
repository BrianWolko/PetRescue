package com.wolkorp.petrescue.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.Constants.GOOGLE_API_KEY
import com.sucho.placepicker.MapType
import com.sucho.placepicker.PlacePicker
import com.google.firebase.firestore.ListenerRegistration
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.adapters.PetsAdapter
import com.wolkorp.petrescue.models.Pet
import kotlinx.android.synthetic.main.fragment_buscar.*
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList


class BuscarFragment : Fragment(), OnMapReadyCallback, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    //LOS ARIBUTOS DEL FRAGEMNT
    private lateinit var fragmentView: View
    private var petsList: ArrayList<Pet> = ArrayList()
    private lateinit var reciclerView: RecyclerView
  
    //Listener que escucha cambio en la base de datos
    private lateinit var registrationListener: ListenerRegistration

    private lateinit var locationTextSwitcher: TextSwitcher
    private lateinit var petDescriptionTextSwitcher: TextSwitcher
    private lateinit var mapa: GoogleMap


    // Atributos que son posibles atributos para un fragment separado que se encargue de mostrar y controlar  BottomSheet
    private var selectedPhotoUri: Uri? = null
    private val PICK_IMAGE_CODE = 1000
    private val PLACE_PICKER_CODE = 1
    private lateinit var petDescriptionEditText: EditText
    private  var year = 0
    private  var month = 0
    private  var day = 0
    private  var hour = 0
    private  var minute = 0
    private var selectedLatitude = 0.0
    private var selectedLongitude = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


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
        reciclerView = fragmentView.findViewById(R.id.pets_list)
        locationTextSwitcher = fragmentView.findViewById(R.id.location_text_switcher)
        petDescriptionTextSwitcher = fragmentView.findViewById(R.id.description_text_switcher)

        return fragmentView
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.buscar_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpTextSwitcher()
        setUpReciclerView()
    }


    private fun setUpTextSwitcher() {
        locationTextSwitcher.setFactory(object : ViewSwitcher.ViewFactory {
            override fun makeView(): View {
                val locationTextView = TextView(requireContext())
                return locationTextView
            }
        })

        petDescriptionTextSwitcher.setFactory(object : ViewSwitcher.ViewFactory {
            override fun makeView(): View {
                val descriptionTextView = TextView(requireContext())
                return descriptionTextView
            }
        })

    }


    private fun setUpReciclerView() {
        reciclerView.adapter = PetsAdapter(petsList, requireContext()) { selectedPet -> onPetClick(selectedPet) }
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


    // Se encarga de actulizar todas las views del fragment cuando el reciclerView deja de moverse
    private fun reciclerViewStoppedScrolling() {

        val recivlerViewManager = reciclerView.layoutManager as CardSliderLayoutManager
        //Me da la posicion del principal item que se muestra en el recicler view
        val activeCardPosition = recivlerViewManager.getActiveCardPosition()
        updateViewsToPosition(activeCardPosition)
    }


    private fun updateViewsToPosition(position: Int) {
        // Si la posicion no existe salgo de la funcion
        if (position == -1) return

        val petDescription = petsList.get(position).descripcion
        val latitude = petsList.get(position).latitud
        val longitude = petsList.get(position).longitud
        val fecha = petsList.get(position).fecha

        moveMapTo(latitude, longitude)
        updateText(petDescription, fecha)
    }


    private fun moveMapTo(latitude: Double, longitude: Double) {
        val localizacion = LatLng(latitude, longitude)
        mapa.addMarker(MarkerOptions().position(localizacion).title(localizacion.toString()))
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(localizacion, 14f))
    }


    private fun updateText(petDescription: String, petTimestamp: Timestamp) {
        //todo: falta agregar casos de excepciones donde alguno de los datos no este disponible

        //Modifica el Timestamp para mostrarlo formateado
        val formattedDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(petTimestamp.toDate())
        val formattedHour = DateFormat.getTimeInstance(DateFormat.SHORT).format(petTimestamp.toDate())

        val locationMessage = "$formattedDate   $formattedHour"
        val descriptionMessage = "$petDescription"

        locationTextSwitcher.setText(locationMessage)
        petDescriptionTextSwitcher.setText(descriptionMessage)
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


    override fun onStart() {
        super.onStart()
        getPetsFromFirebase()
    }


    //Devuelve todos las mascota en el mapa desde firebase y los agrega a la lista que despues se muestra  el recylcerView
    private fun getPetsFromFirebase() {
        val query =  FirebaseFirestore
                             .getInstance()
                             .collection("Pets")
                             .orderBy("fecha", Query.Direction.DESCENDING)

        registrationListener = query.addSnapshotListener { snapshot, error  ->
            if (error != null) {
                //todo handle error
                Toast.makeText(context, "Error cargando mascotas", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            petsList.clear()
            for (pet in snapshot!!) {
                petsList.add(pet.toObject())
                Log.d("BuscarFragment", "SE AGREGO UN OBJETO")
            }
            Log.d("BuscarFragment", "SE TERMINARION DE AGREGAR OBJETOS \n \n")

            // Es importante que esta linea se llame despues de haber llenado la lista en el for looop
            reciclerView.adapter = PetsAdapter(petsList, requireContext()) { selectedPet -> onPetClick(selectedPet) }

            //todo: cambiar el nombre de este metodo
            reciclerViewStoppedScrolling()

        }
    }





    // Funcion que se llama cuando se toca el botton de la ActionBar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Boton para abrir el popup
            R.id.menu_add_pet_location -> {
                // todo:  mostrar el popup view
                showBottomSheetView()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    // Muestra un Bottom Sheet que contiene los campos necesarios para subir una nueva mascota
    private fun showBottomSheetView() {

        // Cargar y mostrar el BottomSheet
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
        val addPetBottomSheet = LayoutInflater
            .from(requireContext().applicationContext)
            .inflate(
                R.layout.bottom_sheet_add_pet_location,
                fragmentView.findViewById(R.id.bottomSheetContainer)
            )

        // Permite que se muestre completo el BottomsSheetDialog sino no se muestra por completo
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        // Edit text donde se escribe la descripcion de la mascota
        petDescriptionEditText = addPetBottomSheet.findViewById(R.id.texto_descripcion_mascota)

        // Botton para subir imagen
        val addPhotoButton: Button = addPetBottomSheet.findViewById(R.id.button_add_photo)
        addPhotoButton.setOnClickListener {
            selectImageFromGallery()
        }


        // Botton para seleccionar fecha y hora
        val addDateAndHourButton: Button = addPetBottomSheet.findViewById(R.id.button_add_date)
        addDateAndHourButton.setOnClickListener{
            pickDateAndHour()
        }


        // Botton para seleccionar localizacion
        val addLocation: Button = addPetBottomSheet.findViewById(R.id.button_add_location)
        addLocation.setOnClickListener{
            selectLocation()
        }


        // Boton para enviar la mascota a Firebase
        val uploadPetButton: Button = addPetBottomSheet.findViewById(R.id.buttonUploadPet)
        uploadPetButton.setOnClickListener {

            uploadToFirebase()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(addPetBottomSheet)
        bottomSheetDialog.show()
    }


    // Para seleccionar una imagen de las fotos del telefono
    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Please select..."), PICK_IMAGE_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Resultado de seleccionar una foto del telefono
        if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Get the Uri of data
            selectedPhotoUri = data.data
        }


        // resultado de seleccionar una localizacion
        if (requestCode == Constants.PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)

            if ( addressData?.latitude != null && addressData?.longitude != null) {
                this.selectedLatitude = addressData!!.latitude
                this.selectedLongitude = addressData!!.longitude
            } else {
                Toast.makeText(context, "Error obteniendo localizacion\n Latitud o Longitud es null", Toast.LENGTH_LONG).show()
            }

        }

    }


    private fun pickDateAndHour() {
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)

        // Muestra el DatePicker
        DatePickerDialog(requireContext(), this, year, month, day).show()
    }


    // Funcion requerida de lai nterfaz DatePickerDialog.OnDateSetListener que implementa este Fragment
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        this.year = year
        this.month = month
        this.day = dayOfMonth

        val calendar = Calendar.getInstance()
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)

        // Muestra el TimePicker
        TimePickerDialog(requireContext(), this, hour, minute, true).show()
    }


    // Funcion requerida de la interfaz TimePickerDialog.OnTimeSetListener que implementa este Fragment
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        this.hour = hourOfDay
        this.minute = minute
    }



    private fun selectLocation() {
        val intent = PlacePicker.IntentBuilder()
            .setLatLong(-34.6099, -58.4290)  // Initial Latitude and Longitude the Map will load into
            .showLatLong(true)  // Show Coordinates in the Activity
            .setMapZoom(11.0f)  // Map Zoom Level. Default: 14.0
            .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
            .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
            //.setMarkerDrawable(R.drawable.marker) // Change the default Marker Image
            .setMarkerImageImageColor(R.color.colorPrimary)
           // .setFabColor(R.color.fabColor)
            //.setPrimaryTextColor(R.color.primaryTextColor) // Change text color of Shortened Address
            //.setSecondaryTextColor(R.color.secondaryTextColor) // Change text color of full Address
            //.setBottomViewColor(R.color.bottomViewColor) // Change Address View Background Color (Default: White)
            //.setMapRawResourceStyle(R.raw.map_style)  //Set Map Style (https://mapstyle.withgoogle.com/)
            .setMapType(MapType.NORMAL)
            //.setPlaceSearchBar(true, "AIzaSyBSirIeZ2xmrNez6vuOvo2yO3aZlPnv8gA") //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
            .onlyCoordinates(true)  //Get only Coordinates from Place Picker
            .hideLocationButton(true)   //Hide Location Button (Default: false)
            .disableMarkerAnimation(true)   //Disable Marker Animation (Default: false)
            .build(requireActivity())
        startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
    }


    private fun uploadToFirebase() {
        val fileName = UUID.randomUUID().toString()
        val refStorage = FirebaseStorage
                                            .getInstance()
                                            .reference
                                            .child("imagenesMasctotasBuscarFragment/$fileName")

        if(selectedPhotoUri == null) {
            Toast.makeText(context, "No se subio la mascota.\nTiene que seleccionar una foto", Toast.LENGTH_LONG).show()
            return
        }
            // Esta linea sube solo la imagen a Firebase Storage
            refStorage.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    // Link con la localizacion de la foto en Firebase Storage
                    refStorage.downloadUrl.addOnSuccessListener { firestoreUrl ->
                        // Solo una vez subida la imagen con exito aStorage se sube la mascota  a Firebase Firestore
                        uploadPetToFirebase(firestoreUrl.toString())
                    }
                }
    }


    private fun uploadPetToFirebase(imageUrl: String) {
        // todo: Averiguar como obtener fecha, hora, latitud, longitud, de una foto

        val descripcion = petDescriptionEditText.text.toString()
        val latitud = this.selectedLatitude
        val longitud = this.selectedLongitude
        val fecha =  Timestamp(getDateFromSelectedTime())


        val pet = Pet(descripcion, latitud, longitud, fecha, imageUrl)
        FirebaseFirestore
            .getInstance()
            .collection("Pets")
            .add(pet)
            .addOnSuccessListener {
                 Toast.makeText(context, "Se subio la mascota con exito!", Toast.LENGTH_LONG).show()
            }

        selectedPhotoUri = null
    }


    // Formatea los datos que se obtuvieron de DatePicker y TimePicker y los devuelve como un objeto Date
    private fun getDateFromSelectedTime(): Date {
        val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = this.year
        cal[Calendar.MONTH] = this.month
        cal[Calendar.DAY_OF_MONTH] = this.day
        cal[Calendar.HOUR] = this.hour
        cal[Calendar.MINUTE] = this.minute
        return  cal.time
    }


    //Funcion que se llama cuando el usuario toca una Mascota en el RecyclerView
    private fun onPetClick(selectedPet: Pet) {
        //Guarda al Pet que se tocó  en las clases autogeneradas del navgraph para pasar a otro fragment
        val action = BuscarFragmentDirections.actionBuscarFragmentToPetDetailFragment(selectedPet)
        fragmentView.findNavController().navigate(action)
    }
  
  
    override fun onStop() {
        super.onStop()
        registrationListener.remove()
    }

    
}