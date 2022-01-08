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
    internal var repeatStatus : Int = 0
    private var randomPlayStatus : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSong()

        // 반복재생, 랜덤재생 상태 불러오기 (sharedPreferences - "song_activity_status"
        val sharedPreferences = getSharedPreferences("song_activity_status", MODE_PRIVATE)
        repeatStatus = sharedPreferences.getInt("repeatStatus", 0)
        randomPlayStatus = sharedPreferences.getBoolean("randomPlayStatus", false)
        setRepeatStatus(repeatStatus, false)
        setRandomPlayStatus(randomPlayStatus, false)

        // HomeFragment 로 되돌아가기
        binding.songDownIb.setOnClickListener {
            finish()
        }

        // 재생 일시정지 버튼 클릭시 토글
        binding.songPlayBtnIv.setOnClickListener {
            setPlayerStatus(true)
            if (timer.second == song.playTime) {
                song.second = 0
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

        // seekbar 중간 지점 눌렀을 때 해당 지점으로 점프
//        binding.songMusicplayerProgressSb.setOnSeekBarChangeListener{
//            onProgress
//        }

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

            Log.d("YYYplaying", "song"+song.isPlaying.toString())
            Log.d("YYY", "song의 수신" + song.second.toString())
            binding.songMusicTitleTv.text = song.title
            binding.songSingerNameTv.text = song.singer
            binding.songMusicplayerProgressSb.progress = song.second*1000/song.playTime
            binding.songStartTimeTv.text = String.format("%02d:%02d", song.second/60, song.second%60)
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

                        when (repeatStatus) {
                            2 -> {
                                second = 0
                                mediaPlayer?.seekTo(0)
                                mediaPlayer?.start()
                                // 다음 곡으로 가기 (나중에 추가해야함)
                            }
                            1 -> {
                                second = 0
                                mediaPlayer?.seekTo(0)
                                mediaPlayer?.start()
                            }
                            else -> { // 반복 안하기이면 종료
                                runOnUiThread {
                                    setPlayerStatus(false)
                                    isPlaying = false
                                }
                                timer.interrupt()
                            }
                        }
                    }
                    if (isPlaying){
                        sleep(100)
                        div10second += 0.1f
                        if (div10second >= 1f) {
                            div10second -= 1f
                            second += 1
                            runOnUiThread {
                                binding.songMusicplayerProgressSb.progress = second*1000/playTime
                                binding.songStartTimeTv.text = String.format("%02d:%02d", second/60, second%60)
                            }
                        }
                    }
                }
            }catch (e: InterruptedException){
                Log.d("YEJOON_INTERRUPT", "플레이어 쓰레드 정상 종료")
            }

        }
    }

    override fun onResume() {
        super.onResume()

        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music) // 음악 파일을 mediaPlayer와 연동
        mediaPlayer?.seekTo(song.second*1000)
        if (song.isPlaying)
            mediaPlayer?.start()
        timer = Timer(song.playTime, song.isPlaying)
        timer.start()

        Log.d("YYYcount", Thread.activeCount().toString())
    }

    override fun onPause() {
        super.onPause()
        song.second = timer.second

        timer.interrupt() // 타이머 종료
        mediaPlayer?.release() // 미디어 플레이어 해제
        mediaPlayer = null

        Log.d("YYYSONG", song.second.toString())
    }

    override fun onStop() {
        super.onStop()

        mediaPlayer?.pause()
        timer.isPlaying = false
        song.isPlaying = false
        setPlayerStatus(false)

        // sharedPreferences 에 현재까지의 내용 저장
        var sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        var editor = sharedPreferences.edit() // sp 조작시 사용
        // Gson 을 Json 으로 변환해서 editor를 통해 sp - "song" 에 저장한다.
        var json = gson.toJson(song)
        editor.putString("song", json)
        editor.apply()

        // 반복상태, 랜덤재생 상태도 저장 "song_activity_status" 에
        sharedPreferences = getSharedPreferences("song_activity_status", MODE_PRIVATE)
        editor = sharedPreferences.edit() // sp 조작시 사용
        editor.putInt("repeatStatus", repeatStatus)
        editor.putBoolean("randomPlayStatus", randomPlayStatus)
        editor.apply()
    }


    override fun onDestroy() {
        super.onDestroy()

    }
}