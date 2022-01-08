package com.sherlock.flo

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.sherlock.flo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var gson: Gson = Gson()
    private var song = Song()
    private lateinit var timer: Timer
    private var mediaPlayer: MediaPlayer? = null // 음악 재생시 음악 나오게 하는것

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigation()

        // 미니플레이어 클릭했을 때 Activity 전환
        binding.mainPlayerLayout.setOnClickListener {
            val intent = Intent(this, SongActivity::class.java)
            val song = Song(binding.mainMiniplayerTitleTv.text.toString(), binding.mainMiniplayerSingerTv.text.toString(), timer.second, 6, false, "music_lilac")

            intent.putExtra("title", song.title)
            intent.putExtra("singer", song.singer)
            intent.putExtra("second", song.second)
            intent.putExtra("playTime", song.playTime)
            intent.putExtra("isPlaying", song.isPlaying)
            intent.putExtra("music", song.music)

            startActivity(intent)
        }

        // 미니플레이어 속 play/pause 버튼 누르면 토글 & mediaPlayer 실행
        binding.mainMiniplayerPlayBtn.setOnClickListener {
            song.isPlaying = true
            setPlayerStatus(true)
            if (timer.second == song.playTime) {
                song.second = 0
                binding.mainSeekbarSb.progress = 0
                timer = Timer(song.playTime, song.isPlaying)
                timer.start()
                mediaPlayer?.seekTo(0)
            }
            timer.isPlaying = true
            mediaPlayer?.start()
        }
        binding.mainMiniplayerPauseBtn.setOnClickListener {
            song.isPlaying = false
            setPlayerStatus(false)
            timer.isPlaying = false
            mediaPlayer?.pause()
        }


        // 바텀 네비게이션 (템플릿에서 기본 제공 코드)
        binding.mainBnv.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

            }
            false
        }

    }

    private fun setPlayerStatus(isPlaying: Boolean){
        if (isPlaying){ //플레잉 하게 해라
            binding.mainMiniplayerPlayBtn.visibility = View.GONE
            binding.mainMiniplayerPauseBtn.visibility = View.VISIBLE
        }
        else {
            binding.mainMiniplayerPauseBtn.visibility = View.GONE
            binding.mainMiniplayerPlayBtn.visibility = View.VISIBLE
        }
    }

    private fun setMiniPlayer(song : Song){
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainSeekbarSb.progress = song.second*1000/song.playTime
        setPlayerStatus(song.isPlaying)

        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        mediaPlayer?.seekTo(song.second*1000)

        timer = Timer(song.playTime, song.isPlaying)
        timer.start()
    }

    override fun onStart() {
        super.onStart()

        // sharedPreferences 에 저장된 값 가져오기
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val jsonSong = sharedPreferences.getString("song", null)
        song = if (jsonSong==null) {
            Song("라일락 눌", "아이유 눌", 0, 195, false, "music_lilac")
        } else {
            gson.fromJson(jsonSong, Song::class.java)
        }

        setMiniPlayer(song)
    }

    // 재생시 seekbar, second 변화하는 timer
    inner class Timer(private val playTime: Int, var isPlaying: Boolean) : Thread() {
        internal var second :Int = song.second
        private var div10second: Float = 0f

        override fun run() {
            try {
                while(true) {
                    if (second >= playTime) {
                        // mediaPlayer와 Timer 사이의 차이를 해결하기 위함
                        sleep(1000)
                        if (!isPlaying)
                            continue
                        // mediaPlayer와 Timer 사이의 차이를 해결하기 위함

                        // 일단 종료하는걸로 만들고, 반복 등을 사용하고 싶으면 sp 받아와서 repeatStatus 와 randomPlay 적용.
                        // 반복 코드는 SongActivity에 있음
                        runOnUiThread {
                            setPlayerStatus(false)
                            isPlaying = false
                        }
                        timer.interrupt()
                    }

                    if (isPlaying){
                        sleep(100)
                        div10second += 0.1f
                        if (div10second >= 1f) {
                            div10second -= 1f
                            second += 1
                            runOnUiThread {
                                binding.mainSeekbarSb.progress = second*1000/playTime
                            }
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
        song.second = timer.second
        setPlayerStatus(false)

        // sharedPreferences 에 현재까지의 내용 저장
        var sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        var editor = sharedPreferences.edit() // sp 조작시 사용
        // Gson 을 Json 으로 변환해서 editor를 통해 sp - "song" 에 저장한다.
        var json = gson.toJson(song)
        editor.putString("song", json)
        editor.apply()

        Log.d("YYYMAIN", song.second.toString())

        // 반복상태, 랜덤재생 상태도 저장 "song_activity_status" 에
//        sharedPreferences = getSharedPreferences("song_activity_status", MODE_PRIVATE)
//        editor = sharedPreferences.edit() // sp 조작시 사용
//        editor.putInt("repeatStatus", repeatStatus)
//        editor.putBoolean("randomPlayStatus", randomPlayStatus)
//        editor.apply()
        // main에서는 이것을 변경 못함
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt() // 타이머 종료
        mediaPlayer?.release() // 미디어 플레이어 해제
        mediaPlayer = null
    }

    private fun initNavigation() {
        supportFragmentManager.beginTransaction().replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()
    }

}

