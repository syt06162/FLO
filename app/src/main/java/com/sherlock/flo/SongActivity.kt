package com.sherlock.flo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sherlock.flo.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {

    lateinit var binding : ActivitySongBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // HomeFragment 로 되돌아가기
        binding.songDownIb.setOnClickListener {
            finish()
        }

        // 재생 일시정지 버튼 클릭시 토글
        binding.songPlayBtnIv.setOnClickListener {
            setPlayerStatus(false)
        }
        binding.songPauseBtnIv.setOnClickListener {
            setPlayerStatus(true)
        }

        // 제목, 가수 이름 받아와서 바꾸기
        if (intent.hasExtra("title") && intent.hasExtra("singer")){
            binding.songMusicTitleTv.text = intent.getStringExtra("title")
            binding.songSingerNameTv.text = intent.getStringExtra("singer")
        }

    }

    fun setPlayerStatus(isPlaying: Boolean){
        if (isPlaying){
            binding.songPlayBtnIv.visibility = View.VISIBLE
            binding.songPauseBtnIv.visibility = View.GONE
        }
        else {
            binding.songPlayBtnIv.visibility = View.GONE
            binding.songPauseBtnIv.visibility = View.VISIBLE
        }
    }

}