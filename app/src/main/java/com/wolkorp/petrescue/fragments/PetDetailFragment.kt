package com.wolkorp.petrescue.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.wolkorp.petrescue.R
import kotlinx.android.synthetic.main.fragment_pet_detail.*


class PetDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pet_detail, container, false)
    }


    override fun onStart() {
        super.onStart()



        val pet  = PetDetailFragmentArgs.fromBundle(requireArguments()).selectedPet

        pet_detail_textView.text = pet.descripcion



        Glide
            .with(requireContext())
            .load(pet.imageURL)
            .into(pet_detail_image)



    }

}