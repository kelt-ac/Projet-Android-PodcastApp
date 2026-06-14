package com.podcastapp.android.data.remote.model

import com.google.gson.annotations.SerializedName

// ── Réponse recherche podcasts ─────────────────────────────
data class PodcastIndexSearchResponse(
    @SerializedName("feeds")  val feeds:  List<PodcastIndexFeed>,
    @SerializedName("count")  val count:  Int,
    @SerializedName("status") val status: String
)

data class PodcastIndexFeed(
    @SerializedName("id")           val id:           Long    = 0L,
    @SerializedName("title")        val title:        String? = null,
    @SerializedName("author")       val author:       String? = null,
    @SerializedName("image")        val image:        String? = null,
    @SerializedName("description")  val description:  String? = null,
    @SerializedName("categories")   val categories:   Map<String, String>? = null,
    @SerializedName("url")          val feedUrl:      String? = null,
    @SerializedName("link")         val podcastUrl:   String? = null,
    @SerializedName("episodeCount") val episodeCount: Int     = 0
)

// ── Réponse épisodes ───────────────────────────────────────
data class PodcastIndexEpisodesResponse(
    @SerializedName("items")  val items:  List<PodcastIndexEpisode>,
    @SerializedName("count")  val count:  Int,
    @SerializedName("status") val status: String
)

data class PodcastIndexEpisode(
    @SerializedName("id")           val id:           Long,
    @SerializedName("title")        val title:        String,
    @SerializedName("description")  val description:  String,
    @SerializedName("enclosureUrl") val audioUrl:     String,
    @SerializedName("duration")     val duration:     Int,
    @SerializedName("image")        val image:        String?,
    @SerializedName("datePublished") val datePublished: Long,
    @SerializedName("feedId")       val feedId:       Long
)