package com.example.flo.model

import retrofit2.Call
import retrofit2.http.GET

interface APIInterface {
    @GET("song.json")
    fun doGetSong(): Call<Song>
}