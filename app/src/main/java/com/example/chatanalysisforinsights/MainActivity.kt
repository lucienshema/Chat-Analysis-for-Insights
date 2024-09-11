package com.example.chatanalysisforinsights

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatanalysisforinsights.data.apiService.DashboardData
import com.example.chatanalysisforinsights.ui.screens.ChatUploadScreen
import com.example.chatanalysisforinsights.ui.screens.ExportScreen
import com.example.chatanalysisforinsights.ui.screens.InsightsDashboardScreen
import com.example.chatanalysisforinsights.ui.screens.fetchInsightsFromChatGPT
import com.example.chatanalysisforinsights.ui.theme.ChatAnalysisForInsightsTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            ChatAnalysisForInsightsTheme {
                Scaffold(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    MainAppScreen(navController = navController)
                }
            }
        }
    }
}

@Composable
fun ChatUploadScreenCaller(
    onFileProcessed: (Boolean) -> Unit,
    onDashboardDataReady: (DashboardData) -> Unit
) {
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var isProcessed by remember { mutableStateOf(false) }
    var dashboardData by remember { mutableStateOf<DashboardData?>(null) }
    val context = LocalContext.current

    ChatUploadScreen(
        onProcessClick = { uri ->
            fileUri = uri
            isProcessed = true
            onFileProcessed(true)
        },
        onFilePicked = { pickedUri ->
            fileUri = pickedUri
            isProcessed = false
            onFileProcessed(false)
            dashboardData = null // Clear previous dashboard data
        }
    )

    // Use LaunchedEffect to handle side-effects based on state
    LaunchedEffect(isProcessed) {
        if (isProcessed && fileUri != null) {
            // Fetch insights from ChatGPT after file is processed
            dashboardData = fetchInsightsFromChatGPT(fileUri!!, context)
            if (dashboardData != null) {
                onDashboardDataReady(dashboardData!!)
            }
        }
    }

    // Optional: you can handle post-processing here if needed
}


@Composable
fun MainAppScreen(navController: NavHostController) {
    val insightsState = remember { mutableStateOf<DashboardData?>(null) }
    val isLoading = remember { mutableStateOf(false) }
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "upload") {
        composable("upload") {
            ChatUploadScreenCaller(
                onFileProcessed = { isProcessed ->
                    // No need to handle fetching insights directly here
                },
                onDashboardDataReady = { dashboardData ->
                    insightsState.value = dashboardData
                    isLoading.value = false
                    navController.navigate("insights")
                }
            )
        }

        composable("insights") {
            val data = insightsState.value
            if (isLoading.value) {
                CircularProgressIndicator()
            } else if (data != null) {
                InsightsDashboardScreen(data = data)
            } else {
                Text("Error fetching insights or no data available")
            }
        }

        composable("export") {
            ExportScreen { format ->
                // Handle export action based on the selected format
            }
        }
    }
}

