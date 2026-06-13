package com.podcastapp.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class PodcastEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val author: String,
    val artworkUrl: String,
    val genre: String,
    val episodeCount: Int,
    val feedUrl: String,
    val podcastUrl: String
)