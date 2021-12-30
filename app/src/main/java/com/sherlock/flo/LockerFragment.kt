package com.sherlock.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.sherlock.flo.databinding.FragmentLockerBinding


class LockerFragment : Fragment() {

    lateinit var binding: FragmentLockerBinding
    val information = arrayListOf("저장한곡", "음악파일")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        // 탭레이아웃, 뷰페이저 띄우기
        val adapter = LockerViewpagerAdapter(this)
        binding.lockerContentVp.adapter = adapter
        TabLayoutMediator(binding.lockerContentTb, binding.lockerContentVp){
            tab, position -> tab.text = information[position]
        }.attach()


        return binding.root
    }


}