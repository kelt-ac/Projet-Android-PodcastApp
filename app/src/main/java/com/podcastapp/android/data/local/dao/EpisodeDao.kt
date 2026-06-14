package com.podcastapp.android.data.local.dao

import androidx.room.*
import com.podcastapp.android.data.local.entity.EpisodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {

    @Query("SELECT * FROM downloads ORDER BY downloadedAt DESC")
    fun getAllDownloads(): Flow<List<EpisodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(episode: EpisodeEntity)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteDownload(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM downloads WHERE id = :id)")
    suspend fun isDownloaded(id: String): Boolean
}