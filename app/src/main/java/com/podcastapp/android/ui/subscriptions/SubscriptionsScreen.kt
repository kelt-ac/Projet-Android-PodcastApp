package com.podcastapp.android.ui.subscriptions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.podcastapp.android.domain.model.Podcast
import com.podcastapp.android.viewmodel.SubscriptionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    onBack: () -> Unit = {},
    onPodcastClick: (Podcast) -> Unit = {}
) {
    val viewModel: SubscriptionViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "⭐ Mes Abonnements",
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
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryDark)
                }
            }

            state.subscriptions.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎙️", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text       = "Aucun abonnement",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = PrimaryDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text     = "Abonnez-vous à des podcasts\npour les retrouver ici",
                            fontSize = 14.sp,
                            color    = TextSecondary,
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color(0xFFF0EEF8)),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement   = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.subscriptions) { podcast ->
                        SubscriptionCard(
                            podcast = podcast,
                            onClick = { onPodcastClick(podcast) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubscriptionCard(
    podcast: Podcast,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model              = podcast.artworkUrl,
                contentDescription = podcast.title,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text       = podcast.title,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = PrimaryDark,
                maxLines   = 2,
                overflow   = TextOverflow.Ellipsis,
                modifier   = Modifier.fillMaxWidth()
            )
            Text(
                text     = podcast.author,
                fontSize = 11.sp,
                color    = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}