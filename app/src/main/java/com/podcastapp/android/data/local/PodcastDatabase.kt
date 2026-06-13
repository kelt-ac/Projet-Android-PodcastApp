package com.podcastapp.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.podcastapp.android.data.local.dao.PodcastDao
import com.podcastapp.android.data.local.entity.PodcastEntity

@Database(
    entities = [PodcastEntity::class],
    version  = 1,
    exportSchema = false
)
abstract class PodcastDatabase : RoomDatabase() {
    abstract fun podcastDao(): PodcastDao
}