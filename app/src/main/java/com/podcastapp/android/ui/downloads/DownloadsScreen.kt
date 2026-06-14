package com.podcastapp.android.ui.downloads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.podcastapp.android.core.PrimaryMedium
import com.podcastapp.android.core.TextSecondary
import com.podcastapp.android.data.local.entity.EpisodeEntity
import com.podcastapp.android.viewmodel.DownloadIntent
import com.podcastapp.android.viewmodel.DownloadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    onBack: () -> Unit = {}
) {
    val viewModel: DownloadViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "📥 Téléchargements",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
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
        when {
            state.downloads.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📥", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text       = "Aucun téléchargement",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = PrimaryDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text      = "Téléchargez des épisodes\npour les écouter hors ligne",
                            fontSize  = 14.sp,
                            color     = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onBack,
                            colors  = ButtonDefaults.buttonColors(
                                containerColor = PrimaryDark
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Découvrir des podcasts", color = Color.White)
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color(0xFFF0EEF8)),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    items(
                        items = state.downloads,
                        key   = { it.id }
                    ) { episode ->
                        DownloadItem(
                            episode  = episode,
                            onDelete = {
                                viewModel.handleIntent(DownloadIntent.Delete(episode.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadItem(
    episode: EpisodeEntity,
    onDelete: () -> Unit = {}
) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model              = episode.artworkUrl,
                contentDescription = episode.episodeTitle,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = episode.episodeTitle,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = PrimaryDark,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis
                )
                Text(
                    text     = episode.podcastTitle,
                    fontSize = 11.sp,
                    color    = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text     = "⏱ ${episode.duration}",
                    fontSize = 11.sp,
                    color    = TextSecondary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onDelete) {
                Text("🗑️", fontSize = 18.sp)
            }
        }
    }
}