package com.example.chatanalysisforinsights.ui.screens


import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.chatanalysisforinsights.BuildConfig
import com.example.chatanalysisforinsights.data.apiService.ChatGPTRequest
import com.example.chatanalysisforinsights.data.apiService.DashboardData
import com.example.chatanalysisforinsights.data.apiService.OpenAIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val REQUEST_CODE = 100

private val TAG = "Chat Analysis"


fun parseInsights(responseText: String): DashboardData {
    // Use regex or string matching to extract insights from responseText
    val sentiment = Regex("Sentiment: ([A-Za-z]+)").find(responseText)?.groupValues?.get(1) ?: "Unknown"
    val positivityNegativityRatio = Regex("Positivity/Negativity Ratio: ([\\d%/]+)").find(responseText)?.groupValues?.get(1) ?: "Unknown"
    val dominantSpeaker = Regex("Dominant Speaker: ([A-Za-z ]+)").find(responseText)?.groupValues?.get(1) ?: "Unknown"

    val passiveParticipantsMatch = Regex("Passive Participants: \\[([A-Za-z, ]+)\\]").find(responseText)?.groupValues?.get(1)
    val passiveParticipants = passiveParticipantsMatch?.split(", ") ?: listOf("None")

    val replyFrequency = Regex("Reply Frequency: ([A-Za-z]+)").find(responseText)?.groupValues?.get(1) ?: "Unknown"
    val timeGaps = Regex("Time Gaps: ([\\dA-Za-z ]+)").find(responseText)?.groupValues?.get(1) ?: "Unknown"

    val topicsMatch = Regex("Topics Discussed: \\[([A-Za-z, ]+)\\]").find(responseText)?.groupValues?.get(1)
    val topics = topicsMatch?.split(", ") ?: listOf("None")

    return DashboardData(
        sentiment = sentiment,
        positivityNegativityRatio = positivityNegativityRatio,
        dominantSpeaker = dominantSpeaker,
        passiveParticipants = passiveParticipants,
        replyFrequency = replyFrequency,
        timeGaps = timeGaps,
        topics = topics
    )
}



suspend fun fetchInsightsFromChatGPT(uri: Uri, context: Context): DashboardData {
    // Read file content (chat logs)
    val chatLogs = readTextFromUri(uri, context)
    Log.d(TAG, "chat logs: $chatLogs")

    // Create the prompt for ChatGPT
    val prompt = """
        Analyze the following chat log:
        $chatLogs
        Provide the following insights:
        1. Sentiment Analysis.
        2. Behavioral Insights (dominant speakers, passive participants, tone shifts).
        3. Engagement Metrics (reply frequency, time gaps).
        4. Topic Analysis (categorize and list the topics).
    """.trimIndent()

    val apiKey = BuildConfig.API_KEY
    val request = ChatGPTRequest(prompt = prompt)

    return try {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val openAIService = retrofit.create(OpenAIService::class.java)

        val response = openAIService.getChatInsights(apiKey = apiKey, request = request)

        Log.d(TAG, "response from openai server is $response")

        // Parse the response text into DashboardData
        val responseText = response.choices.firstOrNull()?.text ?: ""

        Log.d(TAG, "responseText: $responseText")
        parseInsights(responseText)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching insights", e)
        // Handle API error
        DashboardData(
            sentiment = "Unknown",
            positivityNegativityRatio = "Unknown",
            dominantSpeaker = "Unknown",
            passiveParticipants = emptyList(),
            replyFrequency = "Unknown",
            timeGaps = "Unknown",
            topics = emptyList()
        )
    }
}

// Helper function to read text from file URI
private fun readTextFromUri(uri: Uri, context: Context): String {
    return context.contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() ?: "" }
}


@Composable
fun ChatUploadScreen(
    onProcessClick: (Uri) -> Unit,
    onFilePicked: (Uri?) -> Unit
) {
    val context = LocalContext.current
    val fileUri = remember { mutableStateOf<Uri?>(null) }
    val isProcessing = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Using a proper ActivityResultLauncher
    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        fileUri.value = uri
        onFilePicked(uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Upload Chat Logs",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Button for picking file
        OutlinedButton(
            onClick = {
                pickFileLauncher.launch("*/*")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Send, contentDescription = "Pick a file")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Pick Chat File")
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (fileUri.value != null) {
            Text(
                text = "Selected file: ${fileUri.value?.lastPathSegment}",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = "No file selected",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Process button
        Button(
            onClick = {
                fileUri.value?.let {
                    isProcessing.value = true
                    // Use the regular function parameter to handle processing
                    onProcessClick(it)
                }
            },
            enabled = fileUri.value != null && !isProcessing.value,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isProcessing.value) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Processing...")
            } else {
                Text(text = "Process Chat")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error message display
        errorMessage.value?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}


