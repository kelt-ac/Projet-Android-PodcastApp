package com.podcastapp.android.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.podcastapp.android.core.PrimaryDark
import com.podcastapp.android.core.PrimaryLight
import com.podcastapp.android.core.PrimaryMedium
import com.podcastapp.android.core.TextSecondary
import com.podcastapp.android.domain.model.Podcast
import com.podcastapp.android.viewmodel.SubscriptionIntent
import com.podcastapp.android.viewmodel.SubscriptionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastDetailScreen(
    podcast: Podcast,
    onBack: () -> Unit = {},
    onSubscribe: (Podcast) -> Unit = {},
    onPlayEpisode: () -> Unit = {}
) {
    val subscriptionViewModel: SubscriptionViewModel = hiltViewModel()
    val isSubscribed by subscriptionViewModel.isSubscribed.collectAsState()

    LaunchedEffect(podcast.id) {
        subscriptionViewModel.handleIntent(
            SubscriptionIntent.CheckSubscription(podcast.id)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = podcast.title,
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = PrimaryDark,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF0EEF8)),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ── Cover + infos principales ──────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model              = podcast.artworkUrl,
                        contentDescription = podcast.title,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text       = podcast.title,
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = PrimaryDark,
                        textAlign  = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text      = podcast.author,
                        fontSize  = 14.sp,
                        color     = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text     = podcast.genre,
                        fontSize = 12.sp,
                        color    = Color.White,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(PrimaryMedium)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Bouton S'abonner ───────────────
                    Button(
                        onClick = {
                            if (isSubscribed) {
                                subscriptionViewModel.handleIntent(
                                    SubscriptionIntent.Unsubscribe(podcast)
                                )
                            } else {
                                subscriptionViewModel.handleIntent(
                                    SubscriptionIntent.Subscribe(podcast)
                                )
                            }
                            onSubscribe(podcast)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape  = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSubscribed) PrimaryLight else PrimaryDark
                        )
                    ) {
                        Text(
                            text       = if (isSubscribed) "✓ Abonné" else "S'abonner",
                            color      = if (isSubscribed) PrimaryDark else Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ── Statistiques ───────────────────────────
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(label = "Épisodes", value = "${podcast.episodeCount}")
                    Divider(
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp),
                        color = Color(0xFFE0E0F0)
                    )
                    StatItem(label = "Genre", value = podcast.genre)
                    Divider(
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp),
                        color = Color(0xFFE0E0F0)
                    )
                    StatItem(label = "Auteur", value = podcast.author.take(15))
                }
            }

            // ── Description ────────────────────────────
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text       = "À propos",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = PrimaryDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Découvrez ${podcast.title} par ${podcast.author}. " +
                                "Ce podcast propose ${podcast.episodeCount} épisodes " +
                                "dans la catégorie ${podcast.genre}.",
                        fontSize   = 14.sp,
                        color      = TextSecondary,
                        lineHeight = 22.sp
                    )
                }
            }

            // ── Épisodes ───────────────────────────────
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text       = "🎙️ Épisodes",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = PrimaryDark
                    )
                }
            }

            // ── Liste des épisodes ─────────────────────
            items(10) { index ->
                EpisodeItem(
                    number   = index + 1,
                    title    = "Épisode ${index + 1} — ${podcast.title}",
                    duration = "${20 + index * 3} min",
                    onPlay   = onPlayEpisode
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text       = value,
            fontSize   = 14.sp,
            fontWeight = FontWeight.Bold,
            color      = PrimaryDark,
            maxLines   = 1,
            overflow   = TextOverflow.Ellipsis
        )
        Text(
            text     = label,
            fontSize = 12.sp,
            color    = TextSecondary
        )
    }
}

@Composable
fun EpisodeItem(
    number: Int,
    title: String,
    duration: String,
    onPlay: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text       = "$number",
            fontSize   = 14.sp,
            fontWeight = FontWeight.Bold,
            color      = PrimaryMedium,
            modifier   = Modifier.width(28.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = title,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = PrimaryDark,
                maxLines   = 2,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                text     = duration,
                fontSize = 12.sp,
                color    = TextSecondary
            )
        }
        IconButton(onClick = onPlay) {
            Text("▶", fontSize = 18.sp, color = PrimaryDark)
        }
        HorizontalDivider(color = Color(0xFFE0E0F0))
    }
}