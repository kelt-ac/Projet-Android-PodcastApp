package com.podcastapp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podcastapp.android.data.repository.SubscriptionRepository
import com.podcastapp.android.domain.model.Podcast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubscriptionViewState(
    val subscriptions: List<Podcast> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class SubscriptionIntent {
    data class Subscribe(val podcast: Podcast)   : SubscriptionIntent()
    data class Unsubscribe(val podcast: Podcast) : SubscriptionIntent()
    data class CheckSubscription(val id: Long)   : SubscriptionIntent()
}

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SubscriptionViewState())
    val state: StateFlow<SubscriptionViewState> = _state

    private val _isSubscribed = MutableStateFlow(false)
    val isSubscribed: StateFlow<Boolean> = _isSubscribed

    init {
        loadSubscriptions()
    }

    private fun loadSubscriptions() {
        viewModelScope.launch {
            repository.getAllSubscriptions().collect { podcasts ->
                _state.value = _state.value.copy(subscriptions = podcasts)
            }
        }
    }

    fun handleIntent(intent: SubscriptionIntent) {
        when (intent) {
            is SubscriptionIntent.Subscribe      -> subscribe(intent.podcast)
            is SubscriptionIntent.Unsubscribe    -> unsubscribe(intent.podcast)
            is SubscriptionIntent.CheckSubscription -> checkSubscription(intent.id)
        }
    }

    private fun subscribe(podcast: Podcast) {
        viewModelScope.launch {
            repository.subscribe(podcast)
            _isSubscribed.value = true
        }
    }

    private fun unsubscribe(podcast: Podcast) {
        viewModelScope.launch {
            repository.unsubscribe(podcast)
            _isSubscribed.value = false
        }
    }

    private fun checkSubscription(id: Long) {
        viewModelScope.launch {
            _isSubscribed.value = repository.isSubscribed(id)
        }
    }
}