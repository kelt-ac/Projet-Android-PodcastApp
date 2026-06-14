package com.podcastapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.podcastapp.android.core.PodcastAppTheme
import com.podcastapp.android.domain.model.Podcast
import com.podcastapp.android.ui.auth.AuthIntent
import com.podcastapp.android.ui.auth.LoginScreen
import com.podcastapp.android.ui.detail.PodcastDetailScreen
import com.podcastapp.android.ui.home.AdaptiveHomeScreen
import com.podcastapp.android.ui.player.PlayerScreen
import com.podcastapp.android.viewmodel.AuthViewModel
import com.podcastapp.android.ui.subscriptions.SubscriptionsScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PodcastAppTheme {
                val viewModel: AuthViewModel = hiltViewModel()
                val state by viewModel.state.collectAsState()
                val context = this
                val windowSizeClass = calculateWindowSizeClass(this)

                var selectedPodcast by remember { mutableStateOf<Podcast?>(null) }
                var selectedEpisode by remember { mutableStateOf<Podcast?>(null) }
                var showSubscriptions by remember { mutableStateOf(false) }


                when {
                    !state.isLoggedIn -> LoginScreen(
                        state    = state,
                        onIntent = { intent ->
                            when (intent) {
                                is AuthIntent.LoginWithGoogle ->
                                    viewModel.loginWithGoogle(context)
                                else -> viewModel.handleIntent(intent)
                            }
                        }
                    )

                    selectedEpisode != null -> PlayerScreen(
                        podcast = selectedEpisode!!,
                        onBack  = { selectedEpisode = null }
                    )

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
                        windowSizeClass = windowSizeClass,
                        onLogout        = { viewModel.logout() },
                        onPodcastClick  = { podcast -> selectedPodcast = podcast },
                        onSubscriptionsClick = { showSubscriptions = true }
                    )
                }
            }
        }
    }
}