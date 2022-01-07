package com.sherlock.flo

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.sherlock.flo.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {

    lateinit var binding : ActivitySongBinding
    private val song : Song = Song() // 현재 재생중인 노래
    private val gson: Gson = Gson()
    private lateinit var timer: Timer // 음악 재생시 second,seekbar 관리 스레드
    private var mediaPlayer: MediaPlayer? = null // 음악 재생시 음악 나오게 하는것

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initSong()

        timer = Timer(song.playTime, song.isPlaying, 0)
        timer.start()

        // HomeFragment 로 되돌아가기
        binding.songDownIb.setOnClickListener {
            finish()
        }

        // 재생 일시정지 버튼 클릭시 토글
        binding.songPlayBtnIv.setOnClickListener {
            timer.isPlaying = true
            setPlayerStatus(true)
            song.isPlaying = true
            mediaPlayer?.start()
        }
        binding.songPauseBtnIv.setOnClickListener {
            timer.isPlaying = false
            setPlayerStatus(false)
            song.isPlaying = false
            mediaPlayer?.pause()
        }

        // 반복 버튼 클릭시 변화 (반복꺼짐(0) - 전체반복(2) - 1개반복(1) - (다시)반복꺼짐)
        binding.songRepeatOffIv.setOnClickListener {
            setRepeatStatus(2)
            timer.repeatStatus = 2
        }
        binding.songRepeatOnAllIv.setOnClickListener {
            setRepeatStatus(1)
            timer.repeatStatus = 1
        }
        binding.songRepeatOnOneIv.setOnClickListener {
            setRepeatStatus(0)
            timer.repeatStatus = 0
        }

        // 랜덤재생 버튼 클릭시 토클
        binding.songRandomOffIv.setOnClickListener {
            setRandomPlayStatus(true)
        }
        binding.songRandomOnIv.setOnClickListener {
            setRandomPlayStatus(false)
        }

    }

    private fun initSong() {
        // 제목, 가수 이름, 음악 시간, 재생상태 받아와서 바꾸기
        if (intent.hasExtra("title") && intent.hasExtra("singer") && intent.hasExtra("second") && intent.hasExtra("playTime") && intent.hasExtra("isPlaying") && intent.hasExtra("music")){
            song.title = intent.getStringExtra("title")!!
            song.singer = intent.getStringExtra("singer")!!
            song.second = intent.getIntExtra("second", 0)
            song.playTime = intent.getIntExtra("playTime", 0)
            song.isPlaying = intent.getBooleanExtra("isPlaying", false)
            song.music = intent.getStringExtra("music")!!
            val music = resources.getIdentifier(song.music, "raw", this.packageName)

            binding.songMusicTitleTv.text = song.title
            binding.songSingerNameTv.text = song.singer
            binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime/60, song.playTime%60)
            setPlayerStatus(song.isPlaying)
            mediaPlayer = MediaPlayer.create(this, music) // 음악 파일을 mediaPlayer와 연동
        }
    }

    private fun setPlayerStatus(isPlaying: Boolean){
        if (isPlaying){ //플레잉 하게 해라
            binding.songPlayBtnIv.visibility = View.GONE
            binding.songPauseBtnIv.visibility = View.VISIBLE
        }
        else {
            binding.songPauseBtnIv.visibility = View.GONE
            binding.songPlayBtnIv.visibility = View.VISIBLE
        }
    }

    private fun setRepeatStatus(isRepeating: Int){
        // isRepeating 0:전체반복 1:한곡반복 2:반복안함
        when (isRepeating) {
            0 -> {
                binding.songRepeatOnOneIv.visibility = View.GONE
                binding.songRepeatOffIv.visibility = View.VISIBLE
                Toast.makeText(this, "반복을 사용하지 않습니다.",Toast.LENGTH_SHORT).show()
            }
            1 -> {
                binding.songRepeatOnAllIv.visibility = View.GONE
                binding.songRepeatOnOneIv.visibility = View.VISIBLE
                Toast.makeText(this, "현재 음악을 반복합니다.",Toast.LENGTH_SHORT).show()
            }
            2 -> {
                binding.songRepeatOffIv.visibility = View.GONE
                binding.songRepeatOnAllIv.visibility = View.VISIBLE
                Toast.makeText(this, "전체 음악을 반복합니다.",Toast.LENGTH_SHORT).show()
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

    // 재생시 seekbar, second 변화하는 timer
    inner class Timer(private val playTime: Int, var isPlaying: Boolean, var repeatStatus: Int) : Thread() {
        private var second = 0

        override fun run() {
            runOnUiThread {
                binding.songMusicplayerProgressSb.progress = 0
                binding.songStartTimeTv.text = "00:00"
            }
            try {
                while(true) {
                    if (second >= playTime) {
                        when (repeatStatus) {
                            2 -> {
                                second = 0
                                // 다음 곡으로 가기 (나중에 추가해야함)
                            }
                            1 -> {
                                second = 0
                            }
                            else -> { // 반복 안하기이면 종료
                                runOnUiThread {
                                    binding.songPauseBtnIv.visibility = View.GONE
                                    binding.songPlayBtnIv.visibility = View.VISIBLE
                                    isPlaying = false
                                }
                                break
                            }
                        }
                    }
                    if (isPlaying){
                        sleep(1000)
                        second++
                        runOnUiThread {
                            binding.songMusicplayerProgressSb.progress = second*1000/playTime
                            binding.songStartTimeTv.text = String.format("%02d:%02d", second/60, second%60)
                        }
                    }
                }
            }catch (e: InterruptedException){
                Log.d("YEJOON_INTERRUPT", "플레이어 쓰레드 정상 종료")
            }

        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
        timer.isPlaying = false
        song.isPlaying = false
        song.second = binding.songMusicplayerProgressSb.progress * song.playTime / 1000
        setPlayerStatus(false)

        // sharedPreferences 에 현재까지의 내용 저장
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() // sp 조작시 사용
        // Gson 을 Json 으로 변환해서 editor를 통해 sp에 저장한다.
        val json = gson.toJson(song)
        editor.putString("song", json)

        editor.apply()
    }


    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt() // 타이머 종료
        mediaPlayer?.release() // 미디어 플레이어 해제
        mediaPlayer = null
    }
}