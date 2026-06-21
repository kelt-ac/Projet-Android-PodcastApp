package com.podcastapp.android.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.podcastapp.android.core.BackgroundLight
import com.podcastapp.android.core.FacebookBlue
import com.podcastapp.android.core.PodcastAppTheme
import com.podcastapp.android.core.PrimaryDark
import com.podcastapp.android.core.PrimaryLight
import com.podcastapp.android.core.PrimaryMedium
import com.podcastapp.android.core.TextSecondary

// ── État MVI ───────────────────────────────────────────────────
data class AuthViewState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginTab: Boolean = true,
    val passwordVisible: Boolean = false,
    val isLoggedIn: Boolean = false,
    val showForgotPasswordDialog: Boolean = false,
    val resetEmail: String = "",
    val isResetLoading: Boolean = false,
    val resetMessage: String? = null,
    val resetSuccess: Boolean = false
)

sealed class AuthIntent {
    data class EmailChanged(val email: String)    : AuthIntent()
    data class PasswordChanged(val pwd: String)   : AuthIntent()
    object ToggleTab                               : AuthIntent()
    object TogglePasswordVisibility                : AuthIntent()
    object Login                                   : AuthIntent()
    object Register                                : AuthIntent()
    object LoginWithGoogle                         : AuthIntent()
    object LoginWithFacebook                       : AuthIntent()
    object OpenForgotPasswordDialog                : AuthIntent()
    object DismissForgotPasswordDialog             : AuthIntent()
    data class ResetEmailChanged(val email: String): AuthIntent()
    object SendPasswordReset                       : AuthIntent()
}

// ── Écran principal ────────────────────────────────────────────
@Composable
fun LoginScreen(
    state: AuthViewState = AuthViewState(),
    onIntent: (AuthIntent) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Logo ───────────────────────────────────────────
            LogoSection()

            Spacer(modifier = Modifier.height(32.dp))

            // ── Carte principale ───────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // Onglets Login / Inscription
                    TabRow(
                        isLoginTab = state.isLoginTab,
                        onToggle = { onIntent(AuthIntent.ToggleTab) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Champ Email
                    AuthTextField(
                        label    = "Email",
                        value    = state.email,
                        hint     = "nom@exemple.com",
                        leadingIcon = Icons.EmailIcon,
                        onValueChange = { onIntent(AuthIntent.EmailChanged(it)) },
                        keyboardType  = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Champ Mot de passe
                    PasswordTextField(
                        value    = state.password,
                        visible  = state.passwordVisible,
                        onValueChange   = { onIntent(AuthIntent.PasswordChanged(it)) },
                        onToggleVisible = { onIntent(AuthIntent.TogglePasswordVisibility) }
                    )

                    // Mot de passe oublié
                    if (state.isLoginTab) {
                        Text(
                            text  = "Mot de passe oublié ?",
                            color = PrimaryMedium,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 6.dp)
                                .clickable { onIntent(AuthIntent.OpenForgotPasswordDialog) }
                        )
                    }

                    // Message d'erreur
                    state.errorMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text  = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bouton principal
                    Button(
                        onClick  = {
                            if (state.isLoginTab) onIntent(AuthIntent.Login)
                            else onIntent(AuthIntent.Register)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape  = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                color  = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text  = if (state.isLoginTab) "Se connecter" else "S'inscrire",
                                color = Color.White,
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Séparateur OU
                    OrDivider()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bouton Google
                    OutlinedButton(
                        onClick  = { onIntent(AuthIntent.LoginWithGoogle) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape  = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, Color(0xFFE0E0F0)
                        )
                    ) {
                        Text(text = "G  ", color = Color(0xFFEA4335), fontWeight = FontWeight.Bold)
                        Text(
                            text  = "Continuer avec Google",
                            color = Color(0xFF333333),
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bouton Facebook
                    Button(
                        onClick  = { onIntent(AuthIntent.LoginWithFacebook) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape  = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FacebookBlue)
                    ) {
                        Text(text = "f  ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            text  = "Continuer avec Facebook",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Fonctionnalités en bas
            FeaturesRow()

            Spacer(modifier = Modifier.height(16.dp))

            // Lien inscription/connexion
            Row {
                Text(
                    text  = if (state.isLoginTab) "Pas de compte ? " else "Déjà un compte ? ",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Text(
                    text  = if (state.isLoginTab) "Rejoindre PodcastApp" else "Se connecter",
                    color = PrimaryDark,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.clickable { onIntent(AuthIntent.ToggleTab) }
                )
            }
        }

        // ── Dialogue mot de passe oublié ────────────────────────
        // Placé au niveau racine du Box (indépendant du flux du Column
        // scrollable) car AlertDialog s'affiche dans sa propre fenêtre.
        if (state.showForgotPasswordDialog) {
            ForgotPasswordDialog(
                email = state.resetEmail,
                isLoading = state.isResetLoading,
                message = state.resetMessage,
                success = state.resetSuccess,
                onEmailChange = { onIntent(AuthIntent.ResetEmailChanged(it)) },
                onSend = { onIntent(AuthIntent.SendPasswordReset) },
                onDismiss = { onIntent(AuthIntent.DismissForgotPasswordDialog) }
            )
        }
    }
}

// ── Composables internes ───────────────────────────────────────

@Composable
private fun LogoSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.padding(end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .width(if (i == 1) 16.dp else if (i == 2) 20.dp else 22.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(PrimaryDark)
                    )
                }
            }
            Text(
                text       = "PodcastApp",
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = PrimaryDark
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text     = "Bienvenue !",
            fontSize = 14.sp,
            color    = TextSecondary
        )
    }
}

@Composable
private fun TabRow(isLoginTab: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF5F5FA))
            .padding(3.dp)
    ) {
        listOf("Connexion" to true, "Inscription" to false).forEach { (label, isLogin) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isLoginTab == isLogin) Color.White
                        else Color.Transparent
                    )
                    .border(
                        width = if (isLoginTab == isLogin) 0.5.dp else 0.dp,
                        color = if (isLoginTab == isLogin) Color(0xFFE0E0F0) else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { if (isLoginTab != isLogin) onToggle() }
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = label,
                    fontSize   = 13.sp,
                    fontWeight = if (isLoginTab == isLogin) FontWeight.SemiBold else FontWeight.Normal,
                    color      = if (isLoginTab == isLogin) PrimaryDark else TextSecondary,
                    textAlign  = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AuthTextField(
    label: String,
    value: String,
    hint: String,
    leadingIcon: @Composable () -> Unit,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            placeholder   = { Text(hint, color = Color(0xFFBBBBBB), fontSize = 13.sp) },
            leadingIcon   = leadingIcon,
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(12.dp),
            singleLine    = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = PrimaryMedium,
                unfocusedBorderColor = Color(0xFFE0E0F0),
                focusedContainerColor   = Color(0xFFFAFAFA),
                unfocusedContainerColor = Color(0xFFFAFAFA),
            )
        )
    }
}

@Composable
private fun PasswordTextField(
    value: String,
    visible: Boolean,
    onValueChange: (String) -> Unit,
    onToggleVisible: () -> Unit
) {
    Column {
        Text(text = "Mot de passe", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            placeholder   = { Text("••••••••", color = Color(0xFFBBBBBB), fontSize = 13.sp) },
            leadingIcon   = {
                Icon(
                    painter = painterResource(android.R.drawable.ic_lock_idle_lock),
                    contentDescription = null,
                    tint   = Color(0xFFBBBBBB),
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon  = {
                IconButton(onClick = onToggleVisible) {
                    Icon(
                        painter = painterResource(
                            if (visible) android.R.drawable.ic_menu_view
                            else android.R.drawable.ic_secure
                        ),
                        contentDescription = if (visible) "Masquer" else "Afficher",
                        tint = Color(0xFFBBBBBB),
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            visualTransformation = if (visible) VisualTransformation.None
            else PasswordVisualTransformation(),
            modifier   = Modifier.fillMaxWidth(),
            shape      = RoundedCornerShape(12.dp),
            singleLine = true,
            colors     = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = PrimaryMedium,
                unfocusedBorderColor    = Color(0xFFE0E0F0),
                focusedContainerColor   = Color(0xFFFAFAFA),
                unfocusedContainerColor = Color(0xFFFAFAFA),
            )
        )
    }
}

@Composable
private fun OrDivider() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0F0))
        Text(
            text     = "  OU  ",
            fontSize = 12.sp,
            color    = TextSecondary
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0F0))
    }
}

@Composable
private fun FeaturesRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf(
            "🎧" to "Hors-ligne" to "Tout sauvegarder",
            "📻" to "Podcasts"   to "Millions d'épisodes"
        ).forEach { (iconLabel, sub) ->
            val (icon, label) = iconLabel
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryLight)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = icon, fontSize = 20.sp)
                Column {
                    Text(
                        text       = label,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = PrimaryDark
                    )
                    Text(text = sub, fontSize = 10.sp, color = PrimaryMedium)
                }
            }
        }
    }
}

// Icônes inline simples
private object Icons {
    val EmailIcon: @Composable () -> Unit = {
        Icon(
            painter = painterResource(android.R.drawable.ic_dialog_email),
            contentDescription = null,
            tint     = Color(0xFFBBBBBB),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun ForgotPasswordDialog(
    email: String,
    isLoading: Boolean,
    message: String?,
    success: Boolean,
    onEmailChange: (String) -> Unit,
    onSend: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Mot de passe oublié",
                fontWeight = FontWeight.Bold,
                color = PrimaryDark,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Saisissez votre email, nous vous enverrons un lien de réinitialisation.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    placeholder = { Text("nom@exemple.com", color = Color(0xFFBBBBBB), fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !success,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryMedium,
                        unfocusedBorderColor = Color(0xFFE0E0F0),
                        focusedContainerColor = Color(0xFFFAFAFA),
                        unfocusedContainerColor = Color(0xFFFAFAFA),
                    )
                )
                message?.let {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = if (success) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            if (!success) {
                Button(
                    onClick = onSend,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Envoyer", color = Color.White)
                    }
                }
            } else {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Fermer", color = Color.White)
                }
            }
        },
        dismissButton = {
            if (!success) {
                TextButton(onClick = onDismiss) {
                    Text("Annuler", color = TextSecondary)
                }
            }
        }
    )
}

// ── Preview ────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PodcastAppTheme {
        LoginScreen()
    }
}