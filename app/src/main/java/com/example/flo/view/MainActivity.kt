package com.example.flo.view

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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


class MainActivity : AppCompatActivity() {
    val tag = "MainActivity"
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doGetSong()

        btnPlay.setOnClickListener {
            btnPlay.visibility = View.GONE
            btnPause.visibility = View.VISIBLE

            mediaPlayer?.start()
        }

        btnPause.setOnClickListener {
            btnPlay.visibility = View.VISIBLE
            btnPause.visibility = View.GONE

            mediaPlayer?.pause()
        }
    }

    private fun doGetSong() {
        val call: Call<Song> = getApiInterface().doGetSong()
        call.enqueue(object : Callback<Song> {
            override fun onResponse(call: Call<Song>, response: Response<Song>) {
                if (response.isSuccessful && response != null) {
                    setSongData(response.body()!!)
                }
            }

            override fun onFailure(call: Call<Song>, t: Throwable) {
                Log.e(tag, "연결 실패")
                Log.e(tag, t.message.toString())
            }
        })
    }

    private fun setSongData(song : Song){
        txtAlbum.text = song.album
        txtTitle.text = song.title
        txtSinger.text = song.singer
        Glide.with(applicationContext).load(song.image).into(imgAlbumCover)
        setRecyclerMessage(toLyrics(song.lyrics))
        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(song.file)
            prepare()
        }
    }

    private fun setRecyclerMessage(arrayLyrics: ArrayList<Lyrics>) {
        recyclerLyrics.layoutManager = LinearLayoutManager(this)
        recyclerLyrics.adapter = AdapterLyrics(this, arrayLyrics)
    }

    private fun toLyrics(data: String): ArrayList<Lyrics> {
        var arrayLyrics: ArrayList<Lyrics> = ArrayList()
        for (i in data.split("\n")) {
            var lyrics = Lyrics()
            lyrics.time = i.split("]")[0].substring(1)
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