package com.Sherlock.FLO

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.Sherlock.FLO.databinding.FragmentLockerBinding


class LockerFragment : Fragment() {

    lateinit var binding: FragmentLockerBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLockerBinding.inflate(inflater, container, false)
        return binding.root
    }


}