package com.sherlock.flo

import android.media.MediaPlayer
import android.os.Bundle
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
    internal var repeatStatus : Int = 0
    private var randomPlayStatus : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSong()
        initClickListener() // 각종 클릭 리스너

        loadRepeatRandomStatusFromSharedPreferences() // 반복재생, 랜덤재생 정보 불러오기

        // seekbar 중간 지점 눌렀을 때 해당 지점으로 점프
//        binding.songMusicplayerProgressSb.setOnSeekBarChangeListener{
//            onProgress
//        }
    }

    override fun onResume() {
        super.onResume()

        // mediaPlayer와 timer 생성, 그리고 음악 재생(isPlaying에 따라)
        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music) // 음악 파일을 mediaPlayer와 연동
        mediaPlayer?.seekTo(song.tenmillisecond*10)
        if (song.isPlaying)
            mediaPlayer?.start()
        timer = Timer(song.playTime, song.isPlaying)
        timer.start()
    }

    override fun onPause() {
        // 시간 정보 song에 저장
        song.tenmillisecond = timer.tenmillisecond

        // 음악 종료
        timer.isPlaying = false
        timer.interrupt() // 타이머 종료
        mediaPlayer?.release() // 미디어 플레이어 해제
        mediaPlayer = null

        saveSongInSharedPreferences() // 노래 정보 sp에 저장

        super.onPause()
    }

    override fun onStop() {
        // mediaPlayer, timer 일시정지(pause)
        song.isPlaying = false
        setPlayerStatus(false)

        saveRepeatRandomStatusInSharedPreferences() // 반복재생,랜덤재생 정보 sp에 저장

        super.onStop()
    }


    private fun initClickListener(){
        // HomeFragment 로 되돌아가기
        binding.songDownIb.setOnClickListener {
            finish()
        }

        // 재생 일시정지 버튼 클릭시 토글
        binding.songPlayBtnIv.setOnClickListener {
            setPlayerStatus(true)
            if (timer.tenmillisecond == song.playTime*100) {
                song.tenmillisecond = 0
                binding.songStartTimeTv.text = "00:00"
                binding.songMusicplayerProgressSb.progress = 0
                timer = Timer(song.playTime, song.isPlaying)
                timer.start()
                mediaPlayer?.seekTo(0)
            }
            timer.isPlaying = true
            song.isPlaying = true
            mediaPlayer?.start()
        }
        binding.songPauseBtnIv.setOnClickListener {
            setPlayerStatus(false)
            timer.isPlaying = false
            song.isPlaying = false
            mediaPlayer?.pause()
        }

        // 반복 버튼 클릭시 변화 (반복꺼짐(0) - 전체반복(2) - 1개반복(1) - (다시)반복꺼짐)
        binding.songRepeatOffIv.setOnClickListener {
            repeatStatus = 2
            setRepeatStatus(repeatStatus)
        }
        binding.songRepeatOnAllIv.setOnClickListener {
            repeatStatus = 1
            setRepeatStatus(repeatStatus)
        }
        binding.songRepeatOnOneIv.setOnClickListener {
            repeatStatus = 0
            setRepeatStatus(repeatStatus)
        }

        // 랜덤재생 버튼 클릭시 토클
        binding.songRandomOffIv.setOnClickListener {
            randomPlayStatus = true
            setRandomPlayStatus(randomPlayStatus)
        }
        binding.songRandomOnIv.setOnClickListener {
            randomPlayStatus = false
            setRandomPlayStatus(randomPlayStatus)
        }
    }

    private fun initSong() {
        // 제목, 가수 이름, 음악 시간, 재생상태 받아와서 바꾸기
        if (intent.hasExtra("title") && intent.hasExtra("singer") && intent.hasExtra("tenmillisecond") && intent.hasExtra("playTime") && intent.hasExtra("isPlaying") && intent.hasExtra("music")){
            song.title = intent.getStringExtra("title")!!
            song.singer = intent.getStringExtra("singer")!!
            song.tenmillisecond = intent.getIntExtra("tenmillisecond", 0)
            song.playTime = intent.getIntExtra("playTime", 0)
            song.isPlaying = intent.getBooleanExtra("isPlaying", false)
            song.music = intent.getStringExtra("music")!!

            binding.songMusicTitleTv.text = song.title
            binding.songSingerNameTv.text = song.singer
            binding.songMusicplayerProgressSb.progress = song.tenmillisecond*10/song.playTime
            binding.songStartTimeTv.text = String.format("%02d:%02d",  song.tenmillisecond/100/60, song.tenmillisecond/100%60)
            binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime/60, song.playTime%60)
            setPlayerStatus(song.isPlaying)
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

    private fun setRepeatStatus(isRepeating: Int, printingToast: Boolean = true){
        // isRepeating 0:전체반복 1:한곡반복 2:반복안함
        when (isRepeating) {
            0 -> {
                binding.songRepeatOffIv.visibility = View.VISIBLE
                binding.songRepeatOnOneIv.visibility = View.GONE
                binding.songRepeatOnAllIv.visibility = View.GONE
                if (printingToast)
                    Toast.makeText(this, "반복을 사용하지 않습니다.",Toast.LENGTH_SHORT).show()
            }
            1 -> {
                binding.songRepeatOffIv.visibility = View.GONE
                binding.songRepeatOnOneIv.visibility = View.VISIBLE
                binding.songRepeatOnAllIv.visibility = View.GONE
                if (printingToast)
                    Toast.makeText(this, "현재 음악을 반복합니다.",Toast.LENGTH_SHORT).show()
            }
            2 -> {
                binding.songRepeatOffIv.visibility = View.GONE
                binding.songRepeatOnOneIv.visibility = View.GONE
                binding.songRepeatOnAllIv.visibility = View.VISIBLE
                if (printingToast)
                    Toast.makeText(this, "전체 음악을 반복합니다.",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setRandomPlayStatus(isRandom: Boolean, printingToast: Boolean = true){
        if (isRandom){
            binding.songRandomOnIv.visibility = View.VISIBLE
            binding.songRandomOffIv.visibility = View.GONE
            if (printingToast)
                Toast.makeText(this, "재생목록을 랜덤으로 재생합니다.",Toast.LENGTH_SHORT).show()
        }
        else {
            binding.songRandomOffIv.visibility = View.VISIBLE
            binding.songRandomOnIv.visibility = View.GONE
            if (printingToast)
                Toast.makeText(this, "재생목록을 순서대로 재생합니다.",Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveSongInSharedPreferences() {
        // sharedPreferences 에 현재까지의 내용 저장
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() // sp 조작시 사용
        // Gson 을 Json 으로 변환해서 editor를 통해 sp - "song" 에 저장한다.
        val json = gson.toJson(song)
        editor.putString("song", json)
        editor.apply()
    }

    private fun saveRepeatRandomStatusInSharedPreferences() {
        // 반복상태, 랜덤재생 상태도 저장 "song_activity_status" 에
        val sharedPreferences = getSharedPreferences("song_activity_status", MODE_PRIVATE)
        val editor = sharedPreferences.edit() // sp 조작시 사용
        editor.putInt("repeatStatus", repeatStatus)
        editor.putBoolean("randomPlayStatus", randomPlayStatus)
        editor.apply()
    }

    private fun loadRepeatRandomStatusFromSharedPreferences() {
        // 반복재생, 랜덤재생 상태 불러오기 (sharedPreferences - "song_activity_status"
        val sharedPreferences = getSharedPreferences("song_activity_status", MODE_PRIVATE)
        repeatStatus = sharedPreferences.getInt("repeatStatus", 0)
        randomPlayStatus = sharedPreferences.getBoolean("randomPlayStatus", false)
        setRepeatStatus(repeatStatus, false)
        setRandomPlayStatus(randomPlayStatus, false)
    }


    // 재생시 seekbar, second 변화하는 timer
    inner class Timer(private val playTime: Int, var isPlaying: Boolean) : Thread() {
        internal var tenmillisecond :Int = song.tenmillisecond

        override fun run() {
            try {
                while(!currentThread().isInterrupted) {
                    if (tenmillisecond >= playTime*100) {
                        when (repeatStatus) {
                            2 -> {
                                tenmillisecond = 0
                                mediaPlayer?.seekTo(0)
                                mediaPlayer?.start()
                                // 다음 곡으로 가기 (나중에 추가해야함)
                            }
                            1 -> {
                                tenmillisecond = 0
                                mediaPlayer?.seekTo(0)
                                mediaPlayer?.start()
                            }
                            else -> { // 반복 안하기이면 종료
                                runOnUiThread {
                                    setPlayerStatus(false)
                                    song.isPlaying = false
                                }
                                timer.interrupt()
                            }
                        }
                    }
                    if (isPlaying){
                        sleep(10)
                        tenmillisecond++
                        runOnUiThread {
                            binding.songMusicplayerProgressSb.progress = tenmillisecond*10/playTime
                            binding.songStartTimeTv.text = String.format("%02d:%02d", tenmillisecond/100/60, tenmillisecond/100%60)
                        }

                    }
                }
            }catch (e: InterruptedException){
                Log.d("YEJOON_INTERRUPT", "플레이어 쓰레드 정상 종료")
            }

        }
    }
}