package com.podcastapp.android.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podcastapp.android.domain.model.Podcast
import com.podcastapp.android.service.AudioPlayerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerViewState(
    val podcast: Podcast? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val playbackSpeed: Float = 1.0f
)

sealed class PlayerIntent {
    data class LoadPodcast(val podcast: Podcast, val context: Context) : PlayerIntent()
    object PlayPause                         : PlayerIntent()
    object SeekForward                       : PlayerIntent()
    object SeekBackward                      : PlayerIntent()
    data class SeekTo(val position: Long)    : PlayerIntent()
    data class SetSpeed(val speed: Float)    : PlayerIntent()
    object Stop                              : PlayerIntent()
}

@HiltViewModel
class PlayerViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(PlayerViewState())
    val state: StateFlow<PlayerViewState> = _state

    fun handleIntent(intent: PlayerIntent) {
        when (intent) {
            is PlayerIntent.LoadPodcast  -> loadPodcast(intent.podcast, intent.context)
            is PlayerIntent.PlayPause    -> togglePlayPause()
            is PlayerIntent.SeekForward  -> seekForward()
            is PlayerIntent.SeekBackward -> seekBackward()
            is PlayerIntent.SeekTo       -> seekTo(intent.position)
            is PlayerIntent.SetSpeed     -> setSpeed(intent.speed)
            is PlayerIntent.Stop         -> stop()
        }
    }

    private fun loadPodcast(podcast: Podcast, context: Context) {
        _state.value = _state.value.copy(
            podcast         = podcast,
            isLoading       = true,
            isPlaying       = false,
            currentPosition = 0L,
            duration        = 180_000L
        )

        val serviceIntent = Intent(context, AudioPlayerService::class.java).apply {
            putExtra("AUDIO_URL", podcast.feedUrl)
        }
        context.startService(serviceIntent)

        viewModelScope.launch {
            delay(1000)
            _state.value = _state.value.copy(
                isLoading = false,
                isPlaying = true
            )
            startProgressUpdate()
        }
    }

    private fun togglePlayPause() {
        _state.value = _state.value.copy(
            isPlaying = !_state.value.isPlaying
        )
        if (_state.value.isPlaying) startProgressUpdate()
    }

    private fun seekForward() {
        val newPosition = (_state.value.currentPosition + 30_000L)
            .coerceAtMost(_state.value.duration)
        _state.value = _state.value.copy(currentPosition = newPosition)
    }

    private fun seekBackward() {
        val newPosition = (_state.value.currentPosition - 15_000L)
            .coerceAtLeast(0L)
        _state.value = _state.value.copy(currentPosition = newPosition)
    }

    private fun seekTo(position: Long) {
        _state.value = _state.value.copy(currentPosition = position)
    }

    private fun setSpeed(speed: Float) {
        _state.value = _state.value.copy(playbackSpeed = speed)
    }

    private fun stop() {
        _state.value = _state.value.copy(
            isPlaying       = false,
            currentPosition = 0L
        )
    }

    private fun startProgressUpdate() {
        viewModelScope.launch {
            while (_state.value.isPlaying) {
                delay(1000L)
                val newPosition = _state.value.currentPosition + 1000L
                if (newPosition >= _state.value.duration) {
                    _state.value = _state.value.copy(
                        isPlaying       = false,
                        currentPosition = 0L
                    )
                    break
                }
                _state.value = _state.value.copy(currentPosition = newPosition)
            }
        }
    }
}