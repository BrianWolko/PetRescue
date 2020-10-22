package com.wolkorp.petrescue.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.wolkorp.petrescue.R
import kotlinx.android.synthetic.main.fragment_post_detail.*


class PostDetailFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_detail, container, false)
    }


    override fun onStart() {
        super.onStart()
        val post  = PostDetailFragmentArgs.fromBundle(requireArguments()).selectedPost

        post_detail_textview.text = post.texto
        
        Glide
            .with(requireContext())
            .load(post.urlImg)
            .into(post_detail_image)

    }
}