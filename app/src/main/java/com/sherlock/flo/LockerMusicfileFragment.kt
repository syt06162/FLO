package com.sherlock.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sherlock.flo.databinding.FragmentLockerMusicfileBinding
import com.sherlock.flo.databinding.FragmentLockerSavedsongBinding

class LockerMusicfileFragment : Fragment() {
    lateinit var binding : FragmentLockerMusicfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerMusicfileBinding.inflate(layoutInflater, container, false)

        return binding.root
    }
}