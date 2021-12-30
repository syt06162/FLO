package com.sherlock.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sherlock.flo.databinding.FragmentLockerSavedsongBinding

class LockerSavedsongFragment : Fragment() {
    lateinit var binding : FragmentLockerSavedsongBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerSavedsongBinding.inflate(layoutInflater, container, false)

        return binding.root
    }
}