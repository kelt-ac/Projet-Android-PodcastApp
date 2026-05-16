package com.podcastapp.android.navigation

object Routes {
    // Auth
    const val LOGIN    = "login"
    const val REGISTER = "register"

    // Main
    const val HOME     = "home"
    const val SEARCH   = "search"
    const val PLAYER   = "player"

    // Subscriptions & Downloads
    const val SUBSCRIPTIONS = "subscriptions"
    const val DOWNLOADS     = "downloads"

    // Profile
    const val PROFILE = "profile"

    // Podcast detail
    const val PODCAST_DETAIL = "podcast_detail/{podcastId}"
    fun podcastDetail(id: String) = "podcast_detail/$id"
}