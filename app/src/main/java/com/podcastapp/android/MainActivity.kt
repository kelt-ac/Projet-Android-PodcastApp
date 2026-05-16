package com.podcastapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.podcastapp.android.core.PodcastAppTheme
import com.podcastapp.android.ui.auth.AuthIntent
import com.podcastapp.android.ui.auth.AuthViewState
import com.podcastapp.android.ui.auth.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PodcastAppTheme {
                var state by remember { mutableStateOf(AuthViewState()) }

                LoginScreen(
                    state = state,
                    onIntent = { intent ->
                        when (intent) {
                            is AuthIntent.EmailChanged ->
                                state = state.copy(email = intent.email)
                            is AuthIntent.PasswordChanged ->
                                state = state.copy(password = intent.pwd)
                            is AuthIntent.ToggleTab ->
                                state = state.copy(isLoginTab = !state.isLoginTab)
                            is AuthIntent.TogglePasswordVisibility ->
                                state = state.copy(passwordVisible = !state.passwordVisible)
                            else -> Unit
                        }
                    }
                )
            }
        }
    }
}