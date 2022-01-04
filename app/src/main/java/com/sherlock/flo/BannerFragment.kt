package com.sherlock.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sherlock.flo.databinding.FragmentBannerBinding

class BannerFragment(private val imgRes : Int) : Fragment() {

    lateinit var binding : FragmentBannerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBannerBinding.inflate(inflater, container, false)
        
        // 배너 이미지 넣기
        binding.bannerImageIv.setImageResource(imgRes)

        return binding.root
    }

}