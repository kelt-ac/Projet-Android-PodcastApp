package com.podcastapp.android.data.remote.model

import com.google.gson.annotations.SerializedName

data class ItunesResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results")     val results: List<PodcastDto>
)

data class PodcastDto(
    @SerializedName("collectionId")      val id: Long,
    @SerializedName("collectionName")    val title: String,
    @SerializedName("artistName")        val author: String,
    @SerializedName("artworkUrl600")     val artworkUrl: String?,
    @SerializedName("primaryGenreName")  val genre: String?,
    @SerializedName("trackCount")        val episodeCount: Int?,
    @SerializedName("feedUrl")           val feedUrl: String?,
    @SerializedName("collectionViewUrl") val podcastUrl: String?
)