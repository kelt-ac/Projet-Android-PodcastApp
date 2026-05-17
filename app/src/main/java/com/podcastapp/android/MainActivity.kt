package com.podcastapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.podcastapp.android.core.PodcastAppTheme
import com.podcastapp.android.domain.model.Podcast
import com.podcastapp.android.ui.auth.AuthIntent
import com.podcastapp.android.ui.auth.LoginScreen
import com.podcastapp.android.ui.detail.PodcastDetailScreen
import com.podcastapp.android.ui.home.HomeScreen
import com.podcastapp.android.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PodcastAppTheme {
                val viewModel: AuthViewModel = viewModel()
                val state by viewModel.state.collectAsState()
                val context = this

                // Navigation state
                var selectedPodcast by remember { mutableStateOf<Podcast?>(null) }

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

                    selectedPodcast != null -> PodcastDetailScreen(
                        podcast   = selectedPodcast!!,
                        onBack    = { selectedPodcast = null },
                        onSubscribe = { }
                    )

                    else -> HomeScreen(
                        onLogout       = { viewModel.logout() },
                        onPodcastClick = { podcast ->
                            selectedPodcast = podcast
                        }
                    )
                }
            }
        }
    }
}