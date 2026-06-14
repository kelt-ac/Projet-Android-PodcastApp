package com.podcastapp.android.data.repository

import com.podcastapp.android.data.remote.PodcastIndexApiService
import com.podcastapp.android.data.remote.model.PodcastIndexEpisode
import com.podcastapp.android.domain.model.Podcast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PodcastIndexRepository @Inject constructor(
    private val api: PodcastIndexApiService
) {
    suspend fun searchPodcasts(query: String): List<Podcast> {
        return api.searchPodcasts(query).feeds
            .filter { it.id != 0L && !it.title.isNullOrEmpty() }
            .map { feed ->
                Podcast(
                    id           = feed.id,
                    title        = feed.title        ?: "Sans titre",
                    author       = feed.author       ?: "Auteur inconnu",
                    artworkUrl   = feed.image        ?: "",
                    genre        = feed.categories?.values?.firstOrNull() ?: "Général",
                    episodeCount = feed.episodeCount,
                    feedUrl      = feed.feedUrl      ?: "",
                    podcastUrl   = feed.podcastUrl   ?: ""
                )
            }
    }

    suspend fun getTrendingPodcasts(): List<Podcast> {
        return api.getTrendingPodcasts().feeds
            .filter { it.id != 0L && !it.title.isNullOrEmpty() }
            .map { feed ->
                Podcast(
                    id           = feed.id,
                    title        = feed.title        ?: "Sans titre",
                    author       = feed.author       ?: "Auteur inconnu",
                    artworkUrl   = feed.image        ?: "",
                    genre        = feed.categories?.values?.firstOrNull() ?: "Général",
                    episodeCount = feed.episodeCount,
                    feedUrl      = feed.feedUrl      ?: "",
                    podcastUrl   = feed.podcastUrl   ?: ""
                )
            }
    }

    suspend fun getEpisodes(feedId: Long): List<PodcastIndexEpisode> {
        return api.getEpisodesByFeedId(feedId).items
    }
}