package com.wolkorp.petrescue.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.adapters.PetsAdapter
import com.wolkorp.petrescue.models.Pet


class BuscarFragment : Fragment() {

    //LOS ARIBUTOS DEL FRAGEMNT

    private lateinit var reciclerView: RecyclerView
    private lateinit var petsList: ArrayList<Pet>


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
        reciclerView = view.findViewById(R.id.pets_list)
        petsList = ArrayList()


        createAndAddPets()
        setUpReciclerView()
    }


    private fun createAndAddPets() {

     val pet1 = Pet("Mascota 1", "localizacion", "https://scx1.b-cdn.net/csz/news/800/2018/2-dog.jpg")
     val pet2 = Pet("Mascota 2", "localizacion", "https://image.cnbcfm.com/api/v1/image/105992231-1561667465295gettyimages-521697453.jpeg?v=1561667497&w=630&h=354")
     val pet3 = Pet("Mascota 3", "localizacion", "https://www.zooplus.ie/magazine/wp-content/uploads/2020/01/Female-Dogs-in-Heat-IE-768x512.jpeg")

     petsList.add(pet1)
     petsList.add(pet2)
     petsList.add(pet3)
    }


    private fun setUpReciclerView() {

        //Esta es la linea de codigo que une el adapter con el pager y permite que funcinonen juntos
        reciclerView.adapter = PetsAdapter(petsList, requireContext())



        reciclerView.layoutManager = CardSliderLayoutManager(requireContext())
        CardSnapHelper().attachToRecyclerView(reciclerView);
    }


}