package com.podcastapp.android.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.podcastapp.android.core.PrimaryDark
import com.podcastapp.android.core.PrimaryLight
import com.podcastapp.android.core.PrimaryMedium
import com.podcastapp.android.core.TextSecondary
import com.podcastapp.android.domain.model.Podcast
import com.podcastapp.android.viewmodel.HomeIntent
import com.podcastapp.android.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit = {},
    onPodcastClick: (Podcast) -> Unit = {},
    onSubscriptionsClick: () -> Unit = {},
    onDownloadsClick: () -> Unit = {}
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text       = "≡ ",
                            fontSize   = 20.sp,
                            color      = PrimaryDark,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text       = "PodcastApp",
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color      = PrimaryDark
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onSubscriptionsClick) {
                        Text("⭐", fontSize = 18.sp)
                    }
                    TextButton(onClick = onDownloadsClick) {
                        Text("📥", fontSize = 18.sp)
                    }
                    TextButton(onClick = onLogout) {
                        Text("Déconnexion", color = PrimaryMedium, fontSize = 12.sp)
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
        ) {
            // ── Barre de recherche ─────────────────────────
            SearchBar(
                query    = state.searchQuery,
                onSearch = { viewModel.handleIntent(HomeIntent.Search(it)) },
                onClear  = { viewModel.handleIntent(HomeIntent.ClearSearch) }
            )

            // ── Chips catégories ───────────────────────────────────
            CategoryChips(
                selectedCategory   = state.selectedCategory,
                onCategorySelected = {
                    viewModel.handleIntent(HomeIntent.SelectCategory(it))
                }
            )

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryDark)
                    }
                }

                state.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text  = "😕",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text  = state.errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.handleIntent(HomeIntent.LoadPodcasts) },
                                colors  = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
                            ) {
                                Text("Réessayer", color = Color.White)
                            }
                        }
                    }
                }

                state.searchQuery.isNotBlank() -> {
                    // ── Résultats de recherche ─────────────
                    SearchResults(
                        results        = state.searchResults,
                        onPodcastClick = onPodcastClick
                    )
                }

                else -> {
                    // ── Contenu principal ──────────────────
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        item {
                            SectionTitle("🔥 Tendances")
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.podcasts.take(10)) { podcast ->
                                    PodcastCardHorizontal(
                                        podcast  = podcast,
                                        onClick  = { onPodcastClick(podcast) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        item {
                            SectionTitle("🎙️ Tous les podcasts")
                        }

                        items(state.podcasts) { podcast ->
                            PodcastCardVertical(
                                podcast = podcast,
                                onClick = { onPodcastClick(podcast) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Composants ─────────────────────────────────────────────

@Composable
fun SearchBar(
    query: String,
    onSearch: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value         = query,
        onValueChange = onSearch,
        placeholder   = { Text("Rechercher un podcast...", fontSize = 13.sp) },
        modifier      = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape  = RoundedCornerShape(12.dp),
        singleLine = true,
        trailingIcon = {
            if (query.isNotBlank()) {
                TextButton(onClick = onClear) {
                    Text("✕", color = TextSecondary)
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = PrimaryMedium,
            unfocusedBorderColor = Color(0xFFE0E0F0),
            focusedContainerColor   = Color.White,
            unfocusedContainerColor = Color.White,
        )
    )
}



@Composable
fun SectionTitle(title: String) {
    Text(
        text       = title,
        fontSize   = 16.sp,
        fontWeight = FontWeight.Bold,
        color      = PrimaryDark,
        modifier   = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun PodcastCardHorizontal(
    podcast: Podcast,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model             = podcast.artworkUrl,
            contentDescription = podcast.title,
            contentScale      = ContentScale.Crop,
            modifier          = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text     = podcast.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color    = PrimaryDark,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
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

@Composable
fun PodcastCardVertical(
    podcast: Podcast,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model             = podcast.artworkUrl,
                contentDescription = podcast.title,
                contentScale      = ContentScale.Crop,
                modifier          = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = podcast.title,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = PrimaryDark,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis
                )
                Text(
                    text     = podcast.author,
                    fontSize = 12.sp,
                    color    = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text     = podcast.genre,
                    fontSize = 11.sp,
                    color    = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(PrimaryMedium)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun SearchResults(
    results: List<Podcast>,
    onPodcastClick: (Podcast) -> Unit
) {
    if (results.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text     = "Aucun résultat trouvé",
                color    = TextSecondary,
                fontSize = 14.sp
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(results) { podcast ->
                PodcastCardVertical(
                    podcast = podcast,
                    onClick = { onPodcastClick(podcast) }
                )
            }
        }
    }
}

@Composable
fun CategoryChips(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf(
        "🎵 Musique",
        "💻 Technologie",
        "🎭 Culture",
        "⚽ Sport",
        "📰 Actualités",
        "🎓 Éducation",
        "💼 Business",
        "🎮 Gaming",
        "🏥 Santé",
        "🎬 Cinéma"
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        items(categories) { category ->
            val isSelected = selectedCategory == category
            FilterChip(
                selected = isSelected,
                onClick  = { onCategorySelected(category) },
                label    = { Text(category, fontSize = 12.sp) },
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryDark,
                    selectedLabelColor     = Color.White,
                    containerColor         = Color.White,
                    labelColor             = PrimaryDark
                ),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}