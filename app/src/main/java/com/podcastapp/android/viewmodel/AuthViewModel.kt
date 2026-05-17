package com.podcastapp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.podcastapp.android.ui.auth.AuthIntent
import com.podcastapp.android.ui.auth.AuthViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(AuthViewState())
    val state: StateFlow<AuthViewState> = _state

    fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.EmailChanged ->
                _state.value = _state.value.copy(email = intent.email)
            is AuthIntent.PasswordChanged ->
                _state.value = _state.value.copy(password = intent.pwd)
            is AuthIntent.ToggleTab ->
                _state.value = _state.value.copy(isLoginTab = !_state.value.isLoginTab)
            is AuthIntent.TogglePasswordVisibility ->
                _state.value = _state.value.copy(passwordVisible = !_state.value.passwordVisible)
            is AuthIntent.Login -> login()
            is AuthIntent.Register -> register()
            is AuthIntent.LoginWithGoogle -> { }
            is AuthIntent.LoginWithFacebook -> { }
        }
    }

    private fun login() {
        val email    = _state.value.email.trim()
        val password = _state.value.password

        if (email.isEmpty() || password.isEmpty()) {
            _state.value = _state.value.copy(
                isLoggedIn = true,
                errorMessage = "Veuillez remplir tous les champs"
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _state.value = _state.value.copy(
                    isLoading    = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading    = false,
                    errorMessage = "Email ou mot de passe incorrect"
                )
            }
        }
    }

    private fun register() {
        val email    = _state.value.email.trim()
        val password = _state.value.password

        if (email.isEmpty() || password.isEmpty()) {
            _state.value = _state.value.copy(
                isLoggedIn = true,
                errorMessage = "Veuillez remplir tous les champs"
            )
            return
        }

        if (password.length < 8) {
            _state.value = _state.value.copy(
                errorMessage = "Le mot de passe doit contenir au moins 8 caractères"
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _state.value = _state.value.copy(
                    isLoading    = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading    = false,
                    errorMessage = "Erreur : ${e.message}"
                )
            }
        }
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun logout() {
        auth.signOut()
        _state.value = AuthViewState()
    }
}