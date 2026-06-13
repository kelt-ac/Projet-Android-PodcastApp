package com.podcastapp.android.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastapp.android.core.PrimaryDark
import com.podcastapp.android.core.PrimaryMedium
import com.podcastapp.android.domain.model.Podcast
import com.podcastapp.android.viewmodel.HomeIntent
import com.podcastapp.android.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabletHomeScreen(
    onLogout: () -> Unit = {},
    onPodcastClick: (Podcast) -> Unit = {}
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    var selectedPodcast by remember { mutableStateOf<Podcast?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "🎙️ PodcastApp",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = PrimaryDark
                    )
                },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Déconnexion", color = PrimaryMedium)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF0EEF8))
        ) {
            // ── Colonne gauche ─────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                SearchBar(
                    query    = state.searchQuery,
                    onSearch = { viewModel.handleIntent(HomeIntent.Search(it)) },
                    onClear  = { viewModel.handleIntent(HomeIntent.ClearSearch) }
                )
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
                            Text(
                                text  = state.errorMessage!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    else -> {
                        val displayList = if (state.searchQuery.isNotBlank())
                            state.searchResults else state.podcasts

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement   = Arrangement.spacedBy(8.dp)
                        ) {
                            items(displayList) { podcast ->
                                PodcastCardVertical(
                                    podcast = podcast,
                                    onClick = {
                                        selectedPodcast = podcast
                                        onPodcastClick(podcast)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ── Divider ───────────────────────────────
            VerticalDivider(
                modifier  = Modifier.fillMaxHeight(),
                thickness = 1.dp,
                color     = Color(0xFFE0E0F0)
            )

            // ── Colonne droite ─────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (selectedPodcast != null) {
                    Text(
                        text       = selectedPodcast!!.title,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = PrimaryDark
                    )
                } else {
                    Text(
                        text     = "🎙️",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text       = "Sélectionnez un podcast",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = PrimaryDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text     = "Cliquez sur un podcast pour voir les détails",
                        fontSize = 14.sp,
                        color    = Color.Gray
                    )
                }
            }
        }
    }
}

