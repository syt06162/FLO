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
    private var createFlag : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation() // 바텀 네비게이션 (템플릿에서 기본 제공 코드)
        initClickListener() // 각종 클릭 리스너들
    }

    override fun onResume() {
        super.onResume()

        loadSongFromSharedPreferences() // sp에서 song 정보 받아오기

        if (createFlag){ // create 즉 앱 처음 실행일때는 sp에 isplaying이 true 값이어도 무조건 isplying을 false로.
            song.isPlaying = false
            createFlag = false
        }

        setMiniPlayer(song)
        Log.d("YYYcount", Thread.activeCount().toString())
    }

    override fun onPause() {
        super.onPause()

        // 시간 정보 song에 저장
        song.second = timer.second

        // 음악 종료
        timer.interrupt() // 타이머 종료
        mediaPlayer?.release() // 미디어 플레이어 해제
        mediaPlayer = null
        Log.d("YYYplaying", "main pause"+song.isPlaying.toString())
    }


    override fun onStop() {
        super.onStop()

        // mediaPlayer, timer 일시정지(pause)
        mediaPlayer?.pause()
        timer.isPlaying = false
        song.isPlaying = false
        setPlayerStatus(false)

        saveSongInSharedPreferences() // 완전 종료시 or Home버튼 누를시 sp에 데이터 저장

        Log.d("YYYplaying", "main stop"+song.isPlaying.toString())
    }


    private fun initClickListener() {
        // 미니플레이어 클릭했을 때 Activity 전환
        binding.mainPlayerLayout.setOnClickListener {
            val intent = Intent(this, SongActivity::class.java)
            val intentSong = Song(
                binding.mainMiniplayerTitleTv.text.toString(),
                binding.mainMiniplayerSingerTv.text.toString(),
                timer.second,
                6,
                song.isPlaying,
                "music_lilac"
            )
            intent.putExtra("title", intentSong.title)
            intent.putExtra("singer", intentSong.singer)
            intent.putExtra("second", intentSong.second)
            intent.putExtra("playTime", intentSong.playTime)
            intent.putExtra("isPlaying", intentSong.isPlaying)
            intent.putExtra("music", intentSong.music)

            Log.d("YYYplaying", "main clicklistne"+intentSong.isPlaying.toString())

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

        // mediaPlayer, timer 생성, 그리고 음악 재생(isPlaying에 따라)
        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        mediaPlayer?.seekTo(song.second*1000)
        if (song.isPlaying)
            mediaPlayer?.start()
        timer = Timer(song.playTime, song.isPlaying)
        timer.start()
    }

    private fun saveSongInSharedPreferences() {
        // sharedPreferences 에 현재까지의 내용 저장
        var sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        var editor = sharedPreferences.edit() // sp 조작시 사용
        // Gson 을 Json 으로 변환해서 editor를 통해 sp - "song" 에 저장한다.
        var json = gson.toJson(song)
        editor.putString("song", json)
        editor.apply()
    }

    private fun loadSongFromSharedPreferences() {
        // sharedPreferences 에 저장된 값 가져오기
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val jsonSong = sharedPreferences.getString("song", null)
        song = if (jsonSong == null) {
            Song("라일락 눌", "아이유 눌", 0, 195, false, "music_lilac")
        } else {
            gson.fromJson(jsonSong, Song::class.java)
        }
    }

    private fun initNavigation() {
        supportFragmentManager.beginTransaction().replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

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
                            song.isPlaying = false
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

}

