package com.podcastapp.android.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.podcastapp.android.core.PrimaryDark
import com.podcastapp.android.core.PrimaryMedium
import com.podcastapp.android.core.TextSecondary
import com.podcastapp.android.domain.model.Podcast
import com.podcastapp.android.viewmodel.PlayerIntent
import com.podcastapp.android.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    podcast: Podcast,
    onBack: () -> Unit = {}
) {
    val viewModel: PlayerViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(podcast) {
        viewModel.handleIntent(PlayerIntent.LoadPodcast(podcast, context))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "Lecture en cours",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = PrimaryDark
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Retour", color = PrimaryMedium, fontSize = 13.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF0EEF8))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Cover ──────────────────────────────────────
            AsyncImage(
                model              = podcast.artworkUrl,
                contentDescription = podcast.title,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Titre + Auteur ─────────────────────────────
            Text(
                text       = podcast.title,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                color      = PrimaryDark,
                textAlign  = TextAlign.Center,
                maxLines   = 2,
                overflow   = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text     = podcast.author,
                fontSize = 14.sp,
                color    = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Barre de progression ───────────────────────
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value         = if (state.duration > 0)
                        state.currentPosition.toFloat() / state.duration.toFloat()
                    else 0f,
                    onValueChange = { progress ->
                        val newPosition = (progress * state.duration).toLong()
                        viewModel.handleIntent(PlayerIntent.SeekTo(newPosition))
                    },
                    colors = SliderDefaults.colors(
                        thumbColor       = PrimaryDark,
                        activeTrackColor = PrimaryDark,
                        inactiveTrackColor = Color(0xFFE0E0F0)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text     = formatTime(state.currentPosition),
                        fontSize = 12.sp,
                        color    = TextSecondary
                    )
                    Text(
                        text     = formatTime(state.duration),
                        fontSize = 12.sp,
                        color    = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Contrôles ──────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Reculer 15s
                IconButton(
                    onClick  = { viewModel.handleIntent(PlayerIntent.SeekBackward) },
                    modifier = Modifier.size(56.dp)
                ) {
                    Text("⏮ 15s", fontSize = 12.sp, color = PrimaryDark, textAlign = TextAlign.Center)
                }

                // Play/Pause
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(PrimaryDark),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { viewModel.handleIntent(PlayerIntent.PlayPause) }
                    ) {
                        Text(
                            text     = if (state.isPlaying) "⏸" else "▶",
                            fontSize = 28.sp,
                            color    = Color.White
                        )
                    }
                }

                // Avancer 30s
                IconButton(
                    onClick  = { viewModel.handleIntent(PlayerIntent.SeekForward) },
                    modifier = Modifier.size(56.dp)
                ) {
                    Text("30s ⏭", fontSize = 12.sp, color = PrimaryDark, textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Sélecteur de vitesse ───────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(0.5f, 1.0f, 1.5f, 2.0f).forEach { speed ->
                    val isSelected = state.playbackSpeed == speed
                    FilterChip(
                        selected = isSelected,
                        onClick  = { viewModel.handleIntent(PlayerIntent.SetSpeed(speed)) },
                        label    = { Text("${speed}×", fontSize = 12.sp) },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryDark,
                            selectedLabelColor     = Color.White,
                            containerColor         = Color.White,
                            labelColor             = PrimaryDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Indicateur de chargement ───────────────────
            if (state.isLoading) {
                CircularProgressIndicator(color = PrimaryDark)
            }
        }
    }
}

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}