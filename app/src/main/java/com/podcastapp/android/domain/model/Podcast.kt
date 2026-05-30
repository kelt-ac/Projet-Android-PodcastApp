package com.podcastapp.android.domain.model

data class Podcast(
    val id: Long,
    val title: String,
    val author: String,
    val artworkUrl: String,
    val genre: String,
    val episodeCount: Int,
    val feedUrl: String,
    val podcastUrl: String
)