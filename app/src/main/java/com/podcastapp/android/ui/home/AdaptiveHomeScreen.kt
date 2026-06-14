package com.podcastapp.android.ui.home

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import com.podcastapp.android.domain.model.Podcast

@Composable
fun AdaptiveHomeScreen(
    windowSizeClass: WindowSizeClass,
    onLogout: () -> Unit = {},
    onPodcastClick: (Podcast) -> Unit = {},
    onSubscriptionsClick: () -> Unit = {}
) {
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            HomeScreen(
                onLogout       = onLogout,
                onPodcastClick = onPodcastClick,
                onSubscriptionsClick = onSubscriptionsClick
            )
        }
        WindowWidthSizeClass.Medium,
        WindowWidthSizeClass.Expanded -> {
            TabletHomeScreen(
                onLogout       = onLogout,
                onPodcastClick = onPodcastClick
            )
        }
    }
}