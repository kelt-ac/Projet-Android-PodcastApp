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
            duration        = 0L
        )

        val serviceIntent = Intent(context, AudioPlayerService::class.java).apply {
            putExtra("AUDIO_URL", podcast.feedUrl)
        }
        context.startService(serviceIntent)

        viewModelScope.launch {
            delay(2000)
            // Récupérer la durée depuis le service
            val duration = AudioPlayerService.getPlayer()?.duration ?: 0L
            _state.value = _state.value.copy(
                isLoading = false,
                isPlaying = true,
                duration  = if (duration > 0) duration else 3600_000L
            )
            startProgressUpdate()
        }
    }

    private fun togglePlayPause() {
        val player = AudioPlayerService.getPlayer()
        if (player != null) {
            if (_state.value.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
        _state.value = _state.value.copy(
            isPlaying = !_state.value.isPlaying
        )
        if (_state.value.isPlaying) startProgressUpdate()
    }

    private fun seekForward() {
        val player = AudioPlayerService.getPlayer()
        val newPosition = (_state.value.currentPosition + 30_000L)
            .coerceAtMost(_state.value.duration)
        player?.seekTo(newPosition)
        _state.value = _state.value.copy(currentPosition = newPosition)
    }

    private fun seekBackward() {
        val player = AudioPlayerService.getPlayer()
        val newPosition = (_state.value.currentPosition - 15_000L)
            .coerceAtLeast(0L)
        player?.seekTo(newPosition)
        _state.value = _state.value.copy(currentPosition = newPosition)
    }

    private fun seekTo(position: Long) {
        val player = AudioPlayerService.getPlayer()
        player?.seekTo(position)
        _state.value = _state.value.copy(currentPosition = position)
    }

    private fun setSpeed(speed: Float) {
        val player = AudioPlayerService.getPlayer()
        player?.setPlaybackSpeed(speed)
        _state.value = _state.value.copy(playbackSpeed = speed)
    }

    private fun stop() {
        val player = AudioPlayerService.getPlayer()
        player?.stop()
        _state.value = _state.value.copy(
            isPlaying       = false,
            currentPosition = 0L
        )
    }

    private fun startProgressUpdate() {
        viewModelScope.launch {
            while (_state.value.isPlaying) {
                delay(1000L)
                val player = AudioPlayerService.getPlayer()
                if (player != null) {
                    val position = player.currentPosition
                    val duration = player.duration

                    if (duration > 0) {
                        _state.value = _state.value.copy(
                            currentPosition = position,
                            duration        = duration
                        )
                        // Vérifier si l'épisode est terminé
                        if (position >= duration) {
                            _state.value = _state.value.copy(
                                isPlaying       = false,
                                currentPosition = 0L
                            )
                            break
                        }
                    }
                } else {
                    break
                }
            }
        }
    }
}