package com.podcastapp.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class EpisodeEntity(
    @PrimaryKey
    val id: String,
    val podcastId: Long,
    val podcastTitle: String,
    val episodeTitle: String,
    val artworkUrl: String,
    val audioUrl: String,
    val duration: String,
    val downloadedAt: Long = System.currentTimeMillis()
)