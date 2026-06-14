package com.podcastapp.android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.podcastapp.android.core.PodcastAppTheme
import com.podcastapp.android.domain.model.Podcast
import com.podcastapp.android.ui.auth.AuthIntent
import com.podcastapp.android.ui.auth.LoginScreen
import com.podcastapp.android.ui.detail.PodcastDetailScreen
import com.podcastapp.android.ui.home.AdaptiveHomeScreen
import com.podcastapp.android.ui.player.MiniPlayer
import com.podcastapp.android.ui.player.PlayerScreen
import com.podcastapp.android.ui.subscriptions.SubscriptionsScreen
import com.podcastapp.android.viewmodel.AuthViewModel
import com.podcastapp.android.viewmodel.PlayerIntent
import com.podcastapp.android.viewmodel.PlayerViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Demander permission notifications (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        setContent {
            PodcastAppTheme {
                val authViewModel: AuthViewModel     = hiltViewModel()
                val playerViewModel: PlayerViewModel = hiltViewModel()
                val authState   by authViewModel.state.collectAsState()
                val playerState by playerViewModel.state.collectAsState()
                val context     = this
                val windowSizeClass = calculateWindowSizeClass(this)

                var selectedPodcast      by remember { mutableStateOf<Podcast?>(null) }
                var selectedEpisode      by remember { mutableStateOf<Podcast?>(null) }
                var showSubscriptions    by remember { mutableStateOf(false) }
                var showPlayerFullScreen by remember { mutableStateOf(false) }

                val showMiniPlayer = playerState.podcast != null &&
                        !showPlayerFullScreen &&
                        authState.isLoggedIn

                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        when {
                            !authState.isLoggedIn -> LoginScreen(
                                state    = authState,
                                onIntent = { intent ->
                                    when (intent) {
                                        is AuthIntent.LoginWithGoogle ->
                                            authViewModel.loginWithGoogle(context)
                                        else -> authViewModel.handleIntent(intent)
                                    }
                                }
                            )

                            showPlayerFullScreen -> PlayerScreen(
                                podcast = playerState.podcast!!,
                                onBack  = { showPlayerFullScreen = false }
                            )

                            selectedEpisode != null -> {
                                val episode = selectedEpisode!!
                                LaunchedEffect(episode) {
                                    playerViewModel.handleIntent(
                                        PlayerIntent.LoadPodcast(episode, context)
                                    )
                                    showPlayerFullScreen = true
                                    selectedEpisode = null
                                }
                            }

                            selectedPodcast != null -> PodcastDetailScreen(
                                podcast       = selectedPodcast!!,
                                onBack        = { selectedPodcast = null },
                                onSubscribe   = { },
                                onPlayEpisode = { selectedEpisode = selectedPodcast }
                            )

                            showSubscriptions -> SubscriptionsScreen(
                                onBack         = { showSubscriptions = false },
                                onPodcastClick = { podcast -> selectedPodcast = podcast }
                            )

                            else -> AdaptiveHomeScreen(
                                windowSizeClass      = windowSizeClass,
                                onLogout             = { authViewModel.logout() },
                                onPodcastClick       = { podcast -> selectedPodcast = podcast },
                                onSubscriptionsClick = { showSubscriptions = true }
                            )
                        }
                    }

                    // ── Mini Player ────────────────────────────
                    if (showMiniPlayer) {
                        MiniPlayer(
                            state       = playerState,
                            onExpand    = { showPlayerFullScreen = true },
                            onPlayPause = {
                                playerViewModel.handleIntent(PlayerIntent.PlayPause)
                            }
                        )
                    }
                }
            }
        }
    }
}