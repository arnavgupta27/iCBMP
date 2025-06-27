package com.example.icbmpfinalboss.ui.screens.ota

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtaUpdateScreen() {
    var updateStatus by remember { mutableStateOf(UpdateStatus.UpdateAvailable) }
    var downloadProgress by remember { mutableStateOf(0f) }
    var autoUpdateEnabled by remember { mutableStateOf(false) }
    var installDuringOffHours by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Software Updates",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Current Version: v2.1.3",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Last Updated: Dec 15, 2024",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Update Status Section
        when (updateStatus) {
            UpdateStatus.UpToDate -> UpToDateCard()
            UpdateStatus.UpdateAvailable -> UpdateAvailableCard(
                onDownloadClick = {
                    updateStatus = UpdateStatus.Downloading
                    // Start mock download
                },
                onLaterClick = { /* Handle later */ }
            )
            UpdateStatus.Downloading -> DownloadingCard(
                progress = downloadProgress,
                onCancelClick = {
                    updateStatus = UpdateStatus.UpdateAvailable
                    downloadProgress = 0f
                }
            )
            UpdateStatus.Installing -> InstallingCard()
            UpdateStatus.UpdateComplete -> UpdateCompleteCard(
                onRestartClick = { /* Handle restart */ }
            )
            UpdateStatus.UpdateFailed -> UpdateFailedCard(
                onRetryClick = { updateStatus = UpdateStatus.UpdateAvailable }
            )
        }

        // Settings Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null
                    )
                    Text(
                        text = "Auto-Update Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Auto-update toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Download updates automatically",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Updates will be downloaded in the background",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = autoUpdateEnabled,
                        onCheckedChange = { autoUpdateEnabled = it }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Install during off-hours toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Install during off-hours",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Install updates when vehicle is not in use",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = installDuringOffHours,
                        onCheckedChange = { installDuringOffHours = it }
                    )
                }
            }
        }

        // Manual check button
        OutlinedButton(
            onClick = { /* Handle manual check */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Check for Updates")
        }
    }

    // Mock download progress simulation
    LaunchedEffect(updateStatus) {
        if (updateStatus == UpdateStatus.Downloading) {
            while (downloadProgress < 1f && updateStatus == UpdateStatus.Downloading) {
                kotlinx.coroutines.delay(100)
                downloadProgress += 0.01f
            }
            if (updateStatus == UpdateStatus.Downloading) {
                updateStatus = UpdateStatus.Installing
                kotlinx.coroutines.delay(3000) // Mock installation time
                updateStatus = UpdateStatus.UpdateComplete
            }
        }
    }
}

// Update status enum
enum class UpdateStatus {
    UpToDate,
    UpdateAvailable,
    Downloading,
    Installing,
    UpdateComplete,
    UpdateFailed
}


@Composable
private fun UpdateAvailableCard(
    onDownloadClick: () -> Unit,
    onLaterClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🆕 Update Available",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Version 2.2.0",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Size: 245 MB",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "What's New:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            Column(
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            ) {
                Text("• Improved battery efficiency", style = MaterialTheme.typography.bodySmall)
                Text("• Enhanced navigation features", style = MaterialTheme.typography.bodySmall)
                Text("• Security improvements", style = MaterialTheme.typography.bodySmall)
                Text("• Bug fixes and performance optimizations", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDownloadClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Download Now")
                }
                OutlinedButton(
                    onClick = onLaterClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Later")
                }
            }
        }
    }
}

@Composable
private fun DownloadingCard(
    progress: Float,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📥 Downloading Update...",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${(progress * 100).toInt()}% complete",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Keep device connected to power",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onCancelClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel Download")
            }
        }
    }
}
@Composable
private fun UpToDateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "✅ Your vehicle is up to date",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You have the latest software version",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InstallingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚙️ Installing Update...",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            CircularProgressIndicator()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Please do not turn off the vehicle",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Installation will complete automatically",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun UpdateCompleteCard(
    onRestartClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎉 Update Complete!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Version 2.2.0 has been successfully installed",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Restart your vehicle to use the new features",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRestartClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Restart Now")
            }
        }
    }
}

@Composable
private fun UpdateFailedCard(
    onRetryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "❌ Update Failed",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "The update could not be completed",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Please check your internet connection and try again",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRetryClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Retry")
                }
                OutlinedButton(
                    onClick = { /* Handle contact support */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Contact Support")
                }
            }
        }
    }
}
