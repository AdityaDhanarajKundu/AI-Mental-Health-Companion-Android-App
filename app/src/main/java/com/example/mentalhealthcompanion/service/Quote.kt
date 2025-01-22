package com.example.mentalhealthcompanion.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Quote(val q: String, val a: String) {
    interface QuoteApiService {
        @GET("random")
        suspend fun getRandomQuote(): List<Quote>
    }

    object RetrofitInstance{
        val api: QuoteApiService by lazy {
            Retrofit.Builder()
                .baseUrl("https://zenquotes.io/api/today/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QuoteApiService::class.java)
        }
    }
}
