package com.sherlock.flo

import android.os.Bundle
import android.view.View
import android.widget.Toast
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

        // 반복 버튼 클릭시 변화 (반복꺼짐(2) - 전체반복(0) - 1개반복(1) - (다시)반복꺼짐)
        binding.songRepeatOffIv.setOnClickListener {
            setRepeatStatus(0)
        }
        binding.songRepeatOnAllIv.setOnClickListener {
            setRepeatStatus(1)
        }
        binding.songRepeatOnOneIv.setOnClickListener {
            setRepeatStatus(2)
        }

        binding.songRandomOffIv.setOnClickListener {
            setRandomPlayStatus(true)
        }
        binding.songRandomOnIv.setOnClickListener {
            setRandomPlayStatus(false)
        }


        // 제목, 가수 이름 받아와서 바꾸기
        if (intent.hasExtra("title") && intent.hasExtra("singer")){
            binding.songMusicTitleTv.text = intent.getStringExtra("title")
            binding.songSingerNameTv.text = intent.getStringExtra("singer")
        }

    }

    private fun setPlayerStatus(isPlaying: Boolean){
        if (isPlaying){
            binding.songPlayBtnIv.visibility = View.VISIBLE
            binding.songPauseBtnIv.visibility = View.GONE
        }
        else {
            binding.songPauseBtnIv.visibility = View.VISIBLE
            binding.songPlayBtnIv.visibility = View.GONE
        }
    }

    private fun setRepeatStatus(isRepeating: Int){
        // isRepeating 0:전체반복 1:한곡반복 2:반복안함
        when (isRepeating) {
            0 -> {
                binding.songRepeatOffIv.visibility = View.GONE
                binding.songRepeatOnAllIv.visibility = View.VISIBLE
                Toast.makeText(this, "전체 음악을 반복합니다.",Toast.LENGTH_SHORT).show()
            }
            1 -> {
                binding.songRepeatOnAllIv.visibility = View.GONE
                binding.songRepeatOnOneIv.visibility = View.VISIBLE
                Toast.makeText(this, "현재 음악을 반복합니다.",Toast.LENGTH_SHORT).show()
            }
            2 -> {
                binding.songRepeatOnOneIv.visibility = View.GONE
                binding.songRepeatOffIv.visibility = View.VISIBLE
                Toast.makeText(this, "반복을 사용하지 않습니다.",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setRandomPlayStatus(isRandom: Boolean){
        if (isRandom){
            binding.songRandomOnIv.visibility = View.VISIBLE
            binding.songRandomOffIv.visibility = View.GONE
            Toast.makeText(this, "재생목록을 랜덤으로 재생합니다.",Toast.LENGTH_SHORT).show()
        }
        else {
            binding.songRandomOffIv.visibility = View.VISIBLE
            binding.songRandomOnIv.visibility = View.GONE
            Toast.makeText(this, "재생목록을 순서대로 재생합니다.",Toast.LENGTH_SHORT).show()
        }
    }

}