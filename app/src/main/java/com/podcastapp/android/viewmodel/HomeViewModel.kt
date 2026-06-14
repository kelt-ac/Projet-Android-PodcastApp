package com.podcastapp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podcastapp.android.data.repository.PodcastIndexRepository
import com.podcastapp.android.domain.model.Podcast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeViewState(
    val isLoading: Boolean = false,
    val podcasts: List<Podcast> = emptyList(),
    val searchResults: List<Podcast> = emptyList(),
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String = ""
)

sealed class HomeIntent {
    object LoadPodcasts                        : HomeIntent()
    data class Search(val query: String)       : HomeIntent()
    object ClearSearch                         : HomeIntent()
    data class SelectCategory(val cat: String) : HomeIntent()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PodcastIndexRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeViewState())
    val state: StateFlow<HomeViewState> = _state

    init {
        handleIntent(HomeIntent.LoadPodcasts)
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadPodcasts   -> loadPodcasts()
            is HomeIntent.Search         -> search(intent.query)
            is HomeIntent.ClearSearch    -> clearSearch()
            is HomeIntent.SelectCategory -> selectCategory(intent.cat)
        }
    }

    private fun loadPodcasts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val podcasts = repository.getTrendingPodcasts()
                _state.value = _state.value.copy(
                    isLoading = false,
                    podcasts  = podcasts
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading    = false,
                    errorMessage = "Erreur de chargement : ${e.message}"
                )
            }
        }
    }

    private fun search(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        if (query.isBlank()) { clearSearch(); return }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val results = repository.searchPodcasts(query)
                _state.value = _state.value.copy(
                    isLoading     = false,
                    searchResults = results
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading    = false,
                    errorMessage = "Erreur de recherche : ${e.message}"
                )
            }
        }
    }

    private fun clearSearch() {
        _state.value = _state.value.copy(
            searchQuery   = "",
            searchResults = emptyList()
        )
    }

    private fun selectCategory(category: String) {
        val newCategory = if (_state.value.selectedCategory == category) "" else category
        _state.value = _state.value.copy(selectedCategory = newCategory)
        if (newCategory.isBlank()) { loadPodcasts(); return }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val results = repository.searchPodcasts(newCategory)
                _state.value = _state.value.copy(
                    isLoading = false,
                    podcasts  = results
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading    = false,
                    errorMessage = "Erreur : ${e.message}"
                )
            }
        }
    }
}