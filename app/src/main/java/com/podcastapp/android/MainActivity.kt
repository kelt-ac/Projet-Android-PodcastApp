package com.podcastapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.podcastapp.android.core.PodcastAppTheme
import com.podcastapp.android.ui.auth.AuthIntent
import com.podcastapp.android.ui.auth.LoginScreen
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

                when {
                    state.isLoggedIn -> HomeScreen(
                        onLogout = { viewModel.logout() }
                    )
                    else -> LoginScreen(
                        state    = state,
                        onIntent = { intent ->
                            when (intent) {
                                is AuthIntent.LoginWithGoogle ->
                                    viewModel.loginWithGoogle(context)
                                else -> viewModel.handleIntent(intent)
                            }
                        }
                    )
                }
            }
        }
    }
}