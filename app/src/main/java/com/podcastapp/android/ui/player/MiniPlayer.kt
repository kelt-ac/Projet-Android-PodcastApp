package com.podcastapp.android.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.podcastapp.android.core.PrimaryDark
import com.podcastapp.android.core.PrimaryMedium
import com.podcastapp.android.core.TextSecondary
import com.podcastapp.android.domain.model.Podcast
import com.podcastapp.android.viewmodel.PlayerIntent
import com.podcastapp.android.viewmodel.PlayerViewState

@Composable
fun MiniPlayer(
    state: PlayerViewState,
    onExpand: () -> Unit = {},
    onPlayPause: () -> Unit = {}
) {
    if (state.podcast == null) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onExpand() },
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = PrimaryDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            // ── Barre de progression ───────────────────
            val progress = if (state.duration > 0)
                state.currentPosition.toFloat() / state.duration.toFloat()
            else 0f

            LinearProgressIndicator(
                progress    = { progress },
                modifier    = Modifier.fillMaxWidth(),
                color       = PrimaryMedium,
                trackColor  = Color.White.copy(alpha = 0.3f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ── Cover ──────────────────────────────
                AsyncImage(
                    model              = state.podcast.artworkUrl,
                    contentDescription = state.podcast.title,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // ── Titre + Auteur ─────────────────────
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = state.podcast.title,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color.White,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Text(
                        text     = state.podcast.author,
                        fontSize = 11.sp,
                        color    = Color.White.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // ── Timer ──────────────────────────────
                Text(
                    text     = formatTime(state.currentPosition),
                    fontSize = 11.sp,
                    color    = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // ── Bouton Play/Pause ──────────────────
                IconButton(onClick = onPlayPause) {
                    Text(
                        text     = if (state.isPlaying) "⏸" else "▶",
                        fontSize = 20.sp,
                        color    = Color.White
                    )
                }
            }
        }
    }
}