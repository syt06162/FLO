package com.sherlock.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sherlock.flo.databinding.FragmentHomePanelBinding

class HomePanelFragment(
    private val main_img: Int, private val main_title: String, private val total_songs: Int, private val date: String,
    private val album_1_img: Int, private val album_1_title: String, private val album_1_singer: String,
    private val album_2_img: Int, private val album_2_title: String, private val album_2_singer: String) : Fragment() {

    lateinit var binding: FragmentHomePanelBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomePanelBinding.inflate(layoutInflater, container, false)

        binding.homePanelBackgroundIv.setImageResource(main_img)
        binding.homePanelTitleTv.text = main_title
        binding.homePanelTotalSongsTv.text = "총 " + total_songs.toString() + "곡"
        binding.homePanelDateTv.text = date

        binding.homePanelAlbumExp1Iv.setImageResource(album_1_img)
        binding.homePanelAlbumExp1TitleTv.text = album_1_title
        binding.homePanelAlbumExp1SingerTv.text = album_1_singer

        binding.homePanelAlbumExp2Iv.setImageResource(album_2_img)
        binding.homePanelAlbumExp2TitleTv.text = album_2_title
        binding.homePanelAlbumExp2SingerTv.text = album_2_singer

        return binding.root
    }
}