package com.podcastapp.android.data.repository

import com.podcastapp.android.data.local.dao.PodcastDao
import com.podcastapp.android.data.local.entity.PodcastEntity
import com.podcastapp.android.domain.model.Podcast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepository @Inject constructor(
    private val dao: PodcastDao
) {
    fun getAllSubscriptions(): Flow<List<Podcast>> =
        dao.getAllSubscriptions().map { entities ->
            entities.map { it.toPodcast() }
        }

    suspend fun subscribe(podcast: Podcast) {
        dao.insertSubscription(podcast.toEntity())
    }

    suspend fun unsubscribe(podcast: Podcast) {
        dao.deleteSubscription(podcast.toEntity())
    }

    suspend fun isSubscribed(id: Long): Boolean =
        dao.isSubscribed(id)

    private fun PodcastEntity.toPodcast() = Podcast(
        id           = id,
        title        = title,
        author       = author,
        artworkUrl   = artworkUrl,
        genre        = genre,
        episodeCount = episodeCount,
        feedUrl      = feedUrl,
        podcastUrl   = podcastUrl
    )

    private fun Podcast.toEntity() = PodcastEntity(
        id           = id,
        title        = title,
        author       = author,
        artworkUrl   = artworkUrl,
        genre        = genre,
        episodeCount = episodeCount,
        feedUrl      = feedUrl,
        podcastUrl   = podcastUrl
    )
}