package com.example.chatanalysisforinsights.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun ExportScreen(onExportClick: (String) -> Unit) {
    val selectedFormat = remember { mutableStateOf("PDF") }
    var isDropdownMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Export Results", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Format Selection Dropdown
        val formats = listOf("PDF", "CSV")
        DropdownMenu(
            expanded = true,
            onDismissRequest = { /*TODO*/ }
        ) {
            formats.forEach { format ->
                DropdownMenuItem(onClick = {
                    selectedFormat.value = format
                    isDropdownMenuExpanded = false
                }, text = { Text("CSV") })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Export Button
        Button(onClick = { onExportClick(selectedFormat.value) }) {
            Text(text = "Export as ${selectedFormat.value}")
        }
    }
}
