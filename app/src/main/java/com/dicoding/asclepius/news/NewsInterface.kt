package com.dicoding.asclepius.news

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val base_url = "https://newsapi.org/v2/"
const val api_key = "658be048502c4e5f9e8101e9477713bd"

interface NewsInterface {

    @GET("everything?apiKey=$api_key")
    fun geteverything(
        @Query("q") query : String,
        @Query("Page") page : Int
    )  : Call<news>
}

object NewsService{
    val newsInstance:NewsInterface
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        newsInstance = retrofit.create(NewsInterface::class.java)
    }
}