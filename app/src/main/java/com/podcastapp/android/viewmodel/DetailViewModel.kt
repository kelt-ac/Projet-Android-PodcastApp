package com.podcastapp.android.viewmodel

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podcastapp.android.data.local.entity.EpisodeEntity
import com.podcastapp.android.data.remote.model.PodcastIndexEpisode
import com.podcastapp.android.data.repository.DownloadRepository
import com.podcastapp.android.data.repository.PodcastIndexRepository
import com.podcastapp.android.domain.model.Podcast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailViewState(
    val episodes: List<PodcastIndexEpisode> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: PodcastIndexRepository,
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DetailViewState())
    val state: StateFlow<DetailViewState> = _state

    fun loadEpisodes(feedId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val episodes = repository.getEpisodes(feedId)
                _state.value = _state.value.copy(
                    isLoading = false,
                    episodes  = episodes
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading    = false,
                    errorMessage = "Erreur : ${e.message}"
                )
            }
        }
    }

    fun downloadEpisode(
        context: Context,
        podcast: Podcast,
        episode: PodcastIndexEpisode
    ) {
        val audioUrl = episode.audioUrl
        if (audioUrl.isNullOrBlank() || episode.id == null) {
            _state.value = _state.value.copy(
                errorMessage = "Épisode invalide, téléchargement impossible"
            )
            return
        }
        val safeTitle = episode.title?.takeIf { it.isNotBlank() } ?: "Épisode sans titre"

        viewModelScope.launch {
            try {
                val request = DownloadManager.Request(Uri.parse(audioUrl))
                    .setTitle(safeTitle)
                    .setDescription("Téléchargement PodcastApp")
                    .setNotificationVisibility(
                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                    )
                    .setDestinationInExternalFilesDir(
                        context,
                        Environment.DIRECTORY_PODCASTS,
                        "${episode.id}.mp3"
                    )
                    .setAllowedOverMetered(true)

                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)

                downloadRepository.download(
                    EpisodeEntity(
                        id           = "${episode.id}",
                        podcastId    = podcast.id,
                        podcastTitle = podcast.title,
                        episodeTitle = safeTitle,
                        artworkUrl   = episode.image ?: podcast.artworkUrl,
                        audioUrl     = audioUrl,
                        duration     = if ((episode.duration ?: 0) > 0) "${episode.duration!! / 60} min" else "Durée inconnue"
                    )
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Erreur téléchargement : ${e.message}"
                )
            }
        }
    }
}
