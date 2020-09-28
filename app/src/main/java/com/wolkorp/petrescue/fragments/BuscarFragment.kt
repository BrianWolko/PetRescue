package com.wolkorp.petrescue.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.adapters.PetsAdapter
import com.wolkorp.petrescue.models.Pet


class BuscarFragment : Fragment() {

    //LOS ARIBUTOS DEL FRAGEMNT

    private lateinit var reciclerView: RecyclerView
    private lateinit var petsList: ArrayList<Pet>
    private lateinit var fragmentView: View


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
        reciclerView = view.findViewById(R.id.pets_list)
        petsList = ArrayList()


        createAndAddPets()
        setUpReciclerView()
    }


    private fun createAndAddPets() {

        val pet1 = Pet("Mascota 1", "localizacion", "https://scx1.b-cdn.net/csz/news/800/2018/2-dog.jpg")
        val pet2 = Pet("Mascota 2", "localizacion", "https://image.cnbcfm.com/api/v1/image/105992231-1561667465295gettyimages-521697453.jpeg?v=1561667497&w=630&h=354")
        val pet3 = Pet("Mascota 3", "localizacion", "https://www.zooplus.ie/magazine/wp-content/uploads/2020/01/Female-Dogs-in-Heat-IE-768x512.jpeg")
        val pet4 = Pet("Mascota 4", "localizacion", "https://scx1.b-cdn.net/csz/news/800/2018/2-dog.jpg")
        val pet5 = Pet("Mascota 5", "localizacion", "https://image.cnbcfm.com/api/v1/image/105992231-1561667465295gettyimages-521697453.jpeg?v=1561667497&w=630&h=354")
        val pet6 = Pet("Mascota 6", "localizacion", "https://www.zooplus.ie/magazine/wp-content/uploads/2020/01/Female-Dogs-in-Heat-IE-768x512.jpeg")
        val pet7 = Pet("Mascota 7", "localizacion", "https://scx1.b-cdn.net/csz/news/800/2018/2-dog.jpg")
        val pet8 = Pet("Mascota 8", "localizacion", "https://image.cnbcfm.com/api/v1/image/105992231-1561667465295gettyimages-521697453.jpeg?v=1561667497&w=630&h=354")
        val pet9 = Pet("Mascota 9", "localizacion", "https://www.zooplus.ie/magazine/wp-content/uploads/2020/01/Female-Dogs-in-Heat-IE-768x512.jpeg")
        val pet10 = Pet("Mascota 10", "localizacion", "https://scx1.b-cdn.net/csz/news/800/2018/2-dog.jpg")
        val pet11 = Pet("Mascota 11", "localizacion", "https://image.cnbcfm.com/api/v1/image/105992231-1561667465295gettyimages-521697453.jpeg?v=1561667497&w=630&h=354")
        val pet12 = Pet("Mascota 12", "localizacion", "https://www.zooplus.ie/magazine/wp-content/uploads/2020/01/Female-Dogs-in-Heat-IE-768x512.jpeg")

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


        reciclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    petsDidScroll()
                }
            }
        })
    }


    private fun petsDidScroll() {
        val recivlerViewManager = reciclerView.layoutManager as CardSliderLayoutManager
        //Me da la posicion del principal item que se muestra en el recicler view
        val position = recivlerViewManager.getActiveCardPosition()
        val petName = petsList.get(position).name

        Snackbar.make(fragmentView, "El nombre es: $petName", Snackbar.LENGTH_LONG).show()
    }


}