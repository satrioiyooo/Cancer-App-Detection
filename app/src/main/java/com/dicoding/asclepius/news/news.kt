package com.dicoding.asclepius.news

import com.google.gson.annotations.SerializedName

data class news(
    @SerializedName("articles")
    val articles: List<Article>,
    @SerializedName("status")
    val status: String,
    @SerializedName("totalResults")
    val totalResults: Int
)
