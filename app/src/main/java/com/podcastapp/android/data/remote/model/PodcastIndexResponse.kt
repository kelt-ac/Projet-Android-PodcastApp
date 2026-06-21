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
    @SerializedName("id")            val id:            Long? = null,
    @SerializedName("title")         val title:         String? = null,
    @SerializedName("description")   val description:   String? = null,
    @SerializedName("enclosureUrl")  val audioUrl:       String? = null,
    @SerializedName("duration")      val duration:       Int? = null,
    @SerializedName("image")         val image:          String? = null,
    @SerializedName("datePublished") val datePublished:  Long? = null,
    @SerializedName("feedId")        val feedId:         Long? = null
)