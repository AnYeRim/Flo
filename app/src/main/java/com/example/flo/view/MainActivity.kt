package com.example.flo.view

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.bumptech.glide.Glide
import com.example.flo.R
import com.example.flo.model.APIClient
import com.example.flo.model.APIInterface

import com.example.flo.model.Song
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.model.Lyrics
import com.example.flo.view.adapter.AdapterLyrics
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    val tag = "MainActivity"

    private var mediaPlayer: MediaPlayer? = MediaPlayer()

    private var song: Song? = null
    private var lyricsList: ArrayList<Lyrics>? = null

    private var adapterLyrics : AdapterLyrics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doGetSong()

        class RealTimeSeekBar : Thread() {
            override fun run() {
                super.run()
                while (mediaPlayer!!.isPlaying) {
                    try {
                        sleep(1000)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    seekBar.progress = mediaPlayer!!.currentPosition
                    // TODO 현재 재생되는 부분의 가사로 recyclerView의 포지션 맞추기
                    for (i in 0 until lyricsList!!.size) {
                        if (lyricsList!![i].milliseconds > seekBar.progress) {
                            handler.post {
                                recyclerLyrics.smoothScrollToPosition(i)
                                adapterLyrics!!.boldText(i)
                            }
                            break
                        }
                    }
                }
            }
        }

        btnPlay.setOnClickListener {
            btnPlay.visibility = View.GONE
            btnPause.visibility = View.VISIBLE

            mediaPlayer!!.start()
            RealTimeSeekBar().start()
        }

        btnPause.setOnClickListener {
            btnPlay.visibility = View.VISIBLE
            btnPause.visibility = View.GONE

            mediaPlayer!!.pause()
            RealTimeSeekBar().interrupt()
        }

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    movePlayPosition(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

    }

    val handler: Handler = object : Handler() {
    }

    fun movePlayPosition(position: Int) {
        mediaPlayer!!.seekTo(position)
    }

    private fun doGetSong() {
        val call: Call<Song> = getApiInterface().doGetSong()
        call.enqueue(object : Callback<Song> {
            override fun onResponse(call: Call<Song>, response: Response<Song>) {
                if (response.isSuccessful && response != null) {
                    song = response.body()!!
                    lyricsList = toLyrics(song!!.lyrics)
                    setSongData()
                }
            }

            override fun onFailure(call: Call<Song>, t: Throwable) {
                Log.e(tag, "연결 실패")
                Log.e(tag, t.message.toString())
            }
        })
    }

    private fun setSongData() {
        txtAlbum.text = song!!.album
        txtTitle.text = song!!.title
        txtSinger.text = song!!.singer
        Glide.with(applicationContext).load(song!!.image).into(imgAlbumCover)
        setRecyclerMessage(lyricsList!!)
        seekBar.max = song!!.duration * 1000
        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(song!!.file)
            prepare()
        }
    }

    private fun setRecyclerMessage(arrayLyrics: ArrayList<Lyrics>) {
        recyclerLyrics.layoutManager = LinearLayoutManager(this)
        adapterLyrics = AdapterLyrics(this, arrayLyrics)
        recyclerLyrics.adapter = adapterLyrics
    }

    private fun toLyrics(data: String): ArrayList<Lyrics> {
        var arrayLyrics: ArrayList<Lyrics> = ArrayList()
        for (i in data.split("\n")) {
            var lyrics = Lyrics()
            var time = i.split("[")[1].split("]")[0].split(":")
            lyrics.milliseconds =
                time[0].toInt() * 60 * 1000 + time[1].toInt() * 1000 + time[2].toInt()
            lyrics.context = i.split("]")[1]
            arrayLyrics.add(lyrics)
        }
        return arrayLyrics
    }

    private fun getApiInterface(): APIInterface {
        return APIClient().createAPI().create(APIInterface::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer?.release()
        mediaPlayer = null
    }
}