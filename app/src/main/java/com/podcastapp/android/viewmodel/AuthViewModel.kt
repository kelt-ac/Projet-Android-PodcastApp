package com.podcastapp.android.viewmodel

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.podcastapp.android.ui.auth.AuthIntent
import com.podcastapp.android.ui.auth.AuthViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val webClientId = "VOTRE_WEB_CLIENT_ID_ICI"

    private val _state = MutableStateFlow(AuthViewState())
    val state: StateFlow<AuthViewState> = _state

    init {
        if (auth.currentUser != null) {
            _state.value = _state.value.copy(isLoggedIn = true)
        }
    }

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
            is AuthIntent.Login          -> login()
            is AuthIntent.Register       -> register()
            is AuthIntent.LoginWithGoogle   -> { }

            // ── Mot de passe oublié ──────────────────────────
            is AuthIntent.OpenForgotPasswordDialog ->
                _state.value = _state.value.copy(
                    showForgotPasswordDialog = true,
                    resetEmail = _state.value.email,
                    resetMessage = null,
                    resetSuccess = false
                )
            is AuthIntent.DismissForgotPasswordDialog ->
                _state.value = _state.value.copy(
                    showForgotPasswordDialog = false,
                    resetMessage = null,
                    resetSuccess = false
                )
            is AuthIntent.ResetEmailChanged ->
                _state.value = _state.value.copy(resetEmail = intent.email)
            is AuthIntent.SendPasswordReset -> sendPasswordResetEmail()
        }
    }

    fun loginWithGoogle(context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                val result     = credentialManager.getCredential(context, request)
                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider
                    .getCredential(googleIdTokenCredential.idToken, null)
                auth.signInWithCredential(firebaseCredential).await()
                _state.value = _state.value.copy(
                    isLoading  = false,
                    isLoggedIn = true,
                    errorMessage = null
                )
            } catch (e: GetCredentialException) {
                _state.value = _state.value.copy(
                    isLoading    = false,
                    errorMessage = "Connexion Google annulée"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading    = false,
                    errorMessage = "Erreur Google : ${e.message}"
                )
            }
        }
    }

    private fun login() {
        val email    = _state.value.email.trim()
        val password = _state.value.password
        if (email.isEmpty() || password.isEmpty()) {
            _state.value = _state.value.copy(errorMessage = "Veuillez remplir tous les champs")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _state.value = _state.value.copy(
                    isLoading  = false,
                    isLoggedIn = true,
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
            _state.value = _state.value.copy(errorMessage = "Veuillez remplir tous les champs")
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
                    isLoading  = false,
                    isLoggedIn = true,
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

    // ── Mot de passe oublié ───────────────────────────────────
    private fun sendPasswordResetEmail() {
        val email = _state.value.resetEmail.trim()
        if (email.isEmpty()) {
            _state.value = _state.value.copy(resetMessage = "Veuillez saisir votre email")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isResetLoading = true, resetMessage = null)
            try {
                auth.sendPasswordResetEmail(email).await()
                _state.value = _state.value.copy(
                    isResetLoading = false,
                    resetSuccess = true,
                    resetMessage = "Email de réinitialisation envoyé ! Vérifiez votre boîte de réception."
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isResetLoading = false,
                    resetSuccess = false,
                    resetMessage = "Erreur : impossible d'envoyer l'email. Vérifiez l'adresse saisie."
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