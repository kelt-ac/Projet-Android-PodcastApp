package com.podcastapp.android.data.remote

import com.podcastapp.android.data.remote.model.PodcastIndexEpisodesResponse
import com.podcastapp.android.data.remote.model.PodcastIndexSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PodcastIndexApiService {

    @GET("search/byterm")
    suspend fun searchPodcasts(
        @Query("q")     query:  String,
        @Query("max")   max:    Int = 20,
        @Query("clean") clean:  Boolean = true
    ): PodcastIndexSearchResponse

    @GET("podcasts/trending")
    suspend fun getTrendingPodcasts(
        @Query("max")  max:  Int = 20,
        @Query("lang") lang: String = "fr,en"
    ): PodcastIndexSearchResponse

    @GET("episodes/byfeedid")
    suspend fun getEpisodesByFeedId(
        @Query("id")  feedId: Long,
        @Query("max") max:    Int = 20
    ): PodcastIndexEpisodesResponse
}