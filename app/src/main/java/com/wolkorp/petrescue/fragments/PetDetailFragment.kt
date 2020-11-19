package com.wolkorp.petrescue.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.wolkorp.petrescue.R
import kotlinx.android.synthetic.main.fragment_pet_detail.*
import java.text.DateFormat


class PetDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pet_detail, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar? = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar?.title =  "Detalle Mascota"
    }


    override fun onStart() {
        super.onStart()


        val pet  = PetDetailFragmentArgs.fromBundle(requireArguments()).selectedPet

        Glide
            .with(requireContext())
            .load(pet.imageURL)
            .into(pet_detail_image)


        pet_detail_description.text = pet.descripcion

        val formattedTimeStamp = DateFormat.getDateInstance(DateFormat.MEDIUM).format(pet.fecha.toDate())
        pet_detail_date.text = formattedTimeStamp


    }

}