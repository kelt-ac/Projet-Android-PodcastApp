package com.podcastapp.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.podcastapp.android.data.local.dao.EpisodeDao
import com.podcastapp.android.data.local.dao.PodcastDao
import com.podcastapp.android.data.local.entity.EpisodeEntity
import com.podcastapp.android.data.local.entity.PodcastEntity

@Database(
    entities = [PodcastEntity::class, EpisodeEntity::class],
    version  = 2,
    exportSchema = false
)
abstract class PodcastDatabase : RoomDatabase() {
    abstract fun podcastDao(): PodcastDao
    abstract fun episodeDao(): EpisodeDao
}