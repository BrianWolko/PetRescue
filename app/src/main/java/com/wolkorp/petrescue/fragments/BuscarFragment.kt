package com.wolkorp.petrescue.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

    private lateinit var locationTextSwitcher: TextSwitcher
    private lateinit var petDescriptionTextSwitcher: TextSwitcher
    private lateinit var mapa: GoogleMap


    // Atributos posibles para un fragment separado que se encargue de mostrar o controlar  BottomSheet
    private var selectedPhotoUri: Uri? = null
    private val PICK_IMAGE_CODE = 1000
    private val PLACE_PICKER_CODE = 1
    private lateinit var petDescriptionEditText: EditText
    private  var year = 0
    private  var month = 0
    private  var day = 0
    private  var hour = 0
    private  var minute = 0


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
        createAndAddPets()
        setUpReciclerView()
        setUpTextSwitcher()
    }


    /*
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
    }*/

    // todo: este despues tengo que borrarlo por ahora es para probar si el nuevo modelo de pet causa problemas con firebase
    // todo: tiene el miso nombre que la funcion commentada de arriba
    private fun createAndAddPets() {
        val query =  FirebaseFirestore
            .getInstance()
            .collection("Pets")
            .orderBy("fecha", Query.Direction.DESCENDING)

        query.addSnapshotListener { snapshot, error  ->
            if (error != null) {
                //todo handle error
                Toast.makeText(context, "Error cargando mascotas", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            petsList.clear()
            for (pet in snapshot!!) {
                petsList.add(pet.toObject())
            }

            // todo: aca es donde actualizo el adapter se repite linea de codifo que en funcion setUpREcyclerView()
            reciclerView.adapter = PetsAdapter(petsList, requireContext()) { selectedPet -> onPetClick(selectedPet) }

        }
    }


    private fun setUpReciclerView() {
        reciclerView.adapter = PetsAdapter(petsList, requireContext()) { selectedPet -> onPetClick(
            selectedPet
        ) }
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

        if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Get the Uri of data
            selectedPhotoUri = data.data
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
        /*
        val gmmIntentUri = Uri.parse("geo:37.7749,-122.4194")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)

         */
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
        // todo: por ahora inicializo la lat y long en 0.0
        val latitud = 0.0
        val longitud = 0.0
        val fecha =  Timestamp(getDateFromSelectedTime())


        val pet = Pet(descripcion, latitud, longitud, fecha, imageUrl)
        FirebaseFirestore
            .getInstance()
            .collection("Pets")
            .add(pet)
            .addOnSuccessListener {
                 Toast.makeText(context, "Se subio la mascota con exito!", Toast.LENGTH_LONG).show()
            }


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


}