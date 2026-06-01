package com.podcastapp.android.data.remote

import com.podcastapp.android.data.remote.model.ItunesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PodcastApiService {

    @GET("search")
    suspend fun searchPodcasts(
        @Query("term")   term: String,
        @Query("media")  media: String = "podcast",
        @Query("limit")  limit: Int = 20,
        @Query("entity") entity: String = "podcast"
    ): ItunesResponse

    @GET("search")
    suspend fun getTopPodcasts(
        @Query("term")   term: String = "top podcasts",
        @Query("media")  media: String = "podcast",
        @Query("limit")  limit: Int = 20,
        @Query("entity") entity: String = "podcast"
    ): ItunesResponse
}