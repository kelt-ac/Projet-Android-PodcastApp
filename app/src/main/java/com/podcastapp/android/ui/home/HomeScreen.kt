package com.podcastapp.android.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.podcastapp.android.core.PrimaryDark

@Composable
fun HomeScreen(onLogout: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎙️",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Bienvenue sur PodcastApp !",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryDark
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Vous êtes connecté avec succès",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryDark
            )
        ) {
            Text("Se déconnecter", color = androidx.compose.ui.graphics.Color.White)
        }
    }
}