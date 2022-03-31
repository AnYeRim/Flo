package com.example.flo.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIClient {
     fun createAPI(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/")
            .build();
    }
}