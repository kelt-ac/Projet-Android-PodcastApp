package com.podcastapp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podcastapp.android.data.local.entity.EpisodeEntity
import com.podcastapp.android.data.repository.DownloadRepository
import com.podcastapp.android.domain.model.Podcast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DownloadViewState(
    val downloads: List<EpisodeEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class DownloadIntent {
    data class Download(val podcast: Podcast, val episodeNumber: Int) : DownloadIntent()
    data class Delete(val id: String)                                  : DownloadIntent()
    data class CheckDownload(val id: String)                           : DownloadIntent()
}

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val repository: DownloadRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DownloadViewState())
    val state: StateFlow<DownloadViewState> = _state

    private val _isDownloaded = MutableStateFlow(false)
    val isDownloaded: StateFlow<Boolean> = _isDownloaded

    init {
        loadDownloads()
    }

    private fun loadDownloads() {
        viewModelScope.launch {
            repository.getAllDownloads().collect { downloads ->
                _state.value = _state.value.copy(downloads = downloads)
            }
        }
    }

    fun handleIntent(intent: DownloadIntent) {
        when (intent) {
            is DownloadIntent.Download      -> download(intent.podcast, intent.episodeNumber)
            is DownloadIntent.Delete        -> delete(intent.id)
            is DownloadIntent.CheckDownload -> checkDownload(intent.id)
        }
    }

    private fun download(podcast: Podcast, episodeNumber: Int) {
        viewModelScope.launch {
            val episode = EpisodeEntity(
                id           = "${podcast.id}_$episodeNumber",
                podcastId    = podcast.id,
                podcastTitle = podcast.title,
                episodeTitle = "Épisode $episodeNumber — ${podcast.title}",
                artworkUrl   = podcast.artworkUrl,
                audioUrl     = podcast.feedUrl,
                duration     = "${20 + episodeNumber * 3} min"
            )
            repository.download(episode)
            _isDownloaded.value = true
        }
    }

    private fun delete(id: String) {
        viewModelScope.launch {
            repository.deleteDownload(id)
        }
    }

    private fun checkDownload(id: String) {
        viewModelScope.launch {
            _isDownloaded.value = repository.isDownloaded(id)
        }
    }
}