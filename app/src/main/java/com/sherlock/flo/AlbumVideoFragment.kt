package com.sherlock.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sherlock.flo.databinding.FragmentAlbumVideoBinding

class AlbumVideoFragment: Fragment() {

    lateinit var binding : FragmentAlbumVideoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumVideoBinding.inflate(inflater, container, false)

        return binding.root
    }
}