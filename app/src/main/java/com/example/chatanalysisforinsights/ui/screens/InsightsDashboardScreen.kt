package com.example.chatanalysisforinsights.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.chatanalysisforinsights.data.apiService.DashboardData

@Composable
fun InsightsDashboardScreen(data: DashboardData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Scrollable column for large content
    ) {
        Text(
            text = "Insights Dashboard",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Sentiment Insights Section
        InsightCard(
            title = "Sentiment Analysis",
            content = {
                Column {
                    Text(text = "Overall Sentiment: ${data.sentiment}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Positivity/Negativity Ratio: ${data.positivityNegativityRatio}", style = MaterialTheme.typography.bodyMedium)
                }
            },
            icon = Icons.Default.Person
        )

        // Behavioral Insights Section
        InsightCard(
            title = "Behavioral Insights",
            content = {
                Column {
                    Text(text = "Dominant Speaker: ${data.dominantSpeaker}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Passive Participants: ${data.passiveParticipants.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
                }
            },
            icon = Icons.Default.Person
        )

        // Engagement Metrics Section
        InsightCard(
            title = "Engagement Metrics",
            content = {
                Column {
                    Text(text = "Reply Frequency: ${data.replyFrequency}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Average Time Gaps: ${data.timeGaps}", style = MaterialTheme.typography.bodyMedium)
                }
            },
            icon = Icons.Default.Menu
        )

        // Topic Analysis Section
        InsightCard(
            title = "Topics of Discussion",
            content = {
                Column {
                    data.topics.forEach { topic ->
                        Text(text = "• $topic", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            },
            icon = Icons.Default.List
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Placeholder for Future Graphical Representations
        Text(
            text = "Graphical Insights (Coming Soon)",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun InsightCard(title: String, content: @Composable () -> Unit, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* Toggle collapse state */ },
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(imageVector = icon, contentDescription = "$title Icon", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, style = MaterialTheme.typography.bodySmall)
            }
            content()
        }
    }
}
