package com.podcastapp.android.data.repository

import com.podcastapp.android.data.remote.RetrofitClient
import com.podcastapp.android.domain.model.Podcast

class PodcastRepository {

    private val api = RetrofitClient.instance

    suspend fun getTopPodcasts(): List<Podcast> {
        return api.getTopPodcasts().results.map { dto ->
            Podcast(
                id           = dto.id,
                title        = dto.title,
                author       = dto.author,
                artworkUrl   = dto.artworkUrl ?: "",
                genre        = dto.genre ?: "Général",
                episodeCount = dto.episodeCount ?: 0,
                feedUrl      = dto.feedUrl ?: "",
                podcastUrl   = dto.podcastUrl ?: ""
            )
        }
    }

    suspend fun searchPodcasts(query: String): List<Podcast> {
        return api.searchPodcasts(term = query).results.map { dto ->
            Podcast(
                id           = dto.id,
                title        = dto.title,
                author       = dto.author,
                artworkUrl   = dto.artworkUrl ?: "",
                genre        = dto.genre ?: "Général",
                episodeCount = dto.episodeCount ?: 0,
                feedUrl      = dto.feedUrl ?: "",
                podcastUrl   = dto.podcastUrl ?: ""
            )
        }
    }
}