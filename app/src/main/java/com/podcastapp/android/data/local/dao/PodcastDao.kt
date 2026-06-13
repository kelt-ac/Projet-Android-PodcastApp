package com.podcastapp.android.data.local.dao

import androidx.room.*
import com.podcastapp.android.data.local.entity.PodcastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PodcastDao {

    @Query("SELECT * FROM subscriptions")
    fun getAllSubscriptions(): Flow<List<PodcastEntity>>

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getSubscriptionById(id: Long): PodcastEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(podcast: PodcastEntity)

    @Delete
    suspend fun deleteSubscription(podcast: PodcastEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM subscriptions WHERE id = :id)")
    suspend fun isSubscribed(id: Long): Boolean
}