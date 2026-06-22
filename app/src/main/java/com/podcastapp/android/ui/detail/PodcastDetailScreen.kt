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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.podcastapp.android.core.PrimaryDark
import com.podcastapp.android.core.PrimaryMedium
import com.podcastapp.android.core.TextSecondary
import com.podcastapp.android.domain.model.Podcast
import com.podcastapp.android.viewmodel.SubscriptionIntent
import com.podcastapp.android.viewmodel.SubscriptionViewModel
import com.podcastapp.android.viewmodel.DownloadIntent
import com.podcastapp.android.viewmodel.DownloadViewModel
import com.podcastapp.android.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastDetailScreen(
    podcast: Podcast,
    onBack: () -> Unit = {},
    onSubscribe: (Podcast) -> Unit = {},
    onPlayEpisode: (String) -> Unit = {}
) {
    val subscriptionViewModel: SubscriptionViewModel = hiltViewModel()
    val isSubscribed by subscriptionViewModel.isSubscribed.collectAsState()
    val downloadViewModel: DownloadViewModel = hiltViewModel()
    val detailViewModel: DetailViewModel = hiltViewModel()
    val detailState by detailViewModel.state.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current


    LaunchedEffect(podcast.id) {
        subscriptionViewModel.handleIntent(
            SubscriptionIntent.CheckSubscription(podcast.id)
        )
        detailViewModel.loadEpisodes(podcast.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = podcast.title,
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = MaterialTheme.colorScheme.onSurface,
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
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ── Cover + infos principales ──────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
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
                        color      = MaterialTheme.colorScheme.onSurface,
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
                            containerColor = if (isSubscribed) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text       = if (isSubscribed) "✓ Abonné" else "S'abonner",
                            color      = if (isSubscribed) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onPrimary,
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
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(label = "Épisodes", value = "${detailState.episodes.size}")
                    Divider(
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp),
                        color = MaterialTheme.colorScheme.outline
                    )
                    StatItem(label = "Genre", value = podcast.genre)
                    Divider(
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp),
                        color = MaterialTheme.colorScheme.outline
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
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    Text(
                        text       = "À propos",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Découvrez ${podcast.title} par ${podcast.author}. " +
                                "Ce podcast propose ${detailState.episodes.size} épisode" +
                                (if (detailState.episodes.size > 1) "s" else "") +
                                " dans la catégorie ${podcast.genre}.",
                        fontSize   = 14.sp,
                        color      = TextSecondary,
                        lineHeight = 22.sp
                    )
                }
            }

            // ── Liste des épisodes ─────────────────────
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    Text(
                        text       = "🎙️ Épisodes",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (detailState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else if (detailState.episodes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text     = "Aucun épisode disponible",
                            color    = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                itemsIndexed(detailState.episodes) { index, episode ->
                    val safeTitle = episode.title?.takeIf { it.isNotBlank() }
                        ?: "Épisode sans titre"
                    val safeDuration = (episode.duration ?: 0).let { seconds ->
                        if (seconds > 0) "${seconds / 60} min" else "Durée inconnue"
                    }
                    val safeAudioUrl = episode.audioUrl ?: ""

                    EpisodeItem(
                        number     = index + 1,
                        title      = safeTitle,
                        duration   = safeDuration,
                        onPlay     = { onPlayEpisode(safeAudioUrl) },
                        onDownload = {
                            detailViewModel.downloadEpisode(context, podcast, episode)
                        }
                    )
                }
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
            color      = MaterialTheme.colorScheme.onSurface,
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
    onPlay: () -> Unit = {},
    onDownload: () -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
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
                    color      = MaterialTheme.colorScheme.onSurface,
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
                Text("▶", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            IconButton(onClick = onDownload) {
                Text("📥", fontSize = 16.sp)
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    }
}