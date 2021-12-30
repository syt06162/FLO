package com.sherlock.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sherlock.flo.databinding.FragmentAlbumSongBinding

class AlbumSongFragment: Fragment() {

    lateinit var binding : FragmentAlbumSongBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumSongBinding.inflate(inflater, container, false)

        return binding.root
    }

    // 크기 다시 조절해주기
    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

}