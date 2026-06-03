package com.hearthealth.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.hearthealth.app.ui.viewmodel.AuthViewModel
import com.hearthealth.app.ui.viewmodel.SyncViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    syncViewModel: SyncViewModel,
    onBack: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val syncState by syncViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Cloud Sync") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Auth Section
            AuthSection(
                isSignedIn = authState.isSignedIn,
                isAnonymous = authState.isAnonymous,
                email = authState.email,
                uid = authState.uid,
                isLoading = authState.isLoading,
                error = authState.error,
                onSignInAnonymously = { authViewModel.signInAnonymously() },
                onSignInWithEmail = { email, pass -> authViewModel.signInWithEmail(email, pass) },
                onCreateAccount = { email, pass -> authViewModel.createAccount(email, pass) },
                onLinkEmail = { email, pass -> authViewModel.linkAnonymousToEmail(email, pass) },
                onSignOut = { authViewModel.signOut() },
                onClearError = { authViewModel.clearError() }
            )

            // Sync Section (only if signed in)
            if (authState.isSignedIn) {
                SyncSection(
                    isSyncing = syncState.isSyncing,
                    lastResult = syncState.lastSyncResult,
                    error = syncState.error,
                    onPush = { syncViewModel.pushToCloud() },
                    onPull = { syncViewModel.pullFromCloud() }
                )
            }
        }
    }
}

@Composable
private fun AuthSection(
    isSignedIn: Boolean,
    isAnonymous: Boolean,
    email: String?,
    uid: String?,
    isLoading: Boolean,
    error: String?,
    onSignInAnonymously: () -> Unit,
    onSignInWithEmail: (String, String) -> Unit,
    onCreateAccount: (String, String) -> Unit,
    onLinkEmail: (String, String) -> Unit,
    onSignOut: () -> Unit,
    onClearError: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Person, null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Authentication",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(12.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (isSignedIn) {
                SignedInView(
                    isAnonymous = isAnonymous,
                    email = email,
                    uid = uid,
                    onLinkEmail = onLinkEmail,
                    onSignOut = onSignOut
                )
            } else {
                SignedOutView(
                    onSignInAnonymously = onSignInAnonymously,
                    onSignInWithEmail = onSignInWithEmail,
                    onCreateAccount = onCreateAccount
                )
            }

            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            error,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                        TextButton(onClick = onClearError) { Text("Dismiss") }
                    }
                }
            }
        }
    }
}

@Composable
private fun SignedInView(
    isAnonymous: Boolean,
    email: String?,
    uid: String?,
    onLinkEmail: (String, String) -> Unit,
    onSignOut: () -> Unit
) {
    Text(
        if (isAnonymous) "Signed in anonymously" else "Signed in as ${email ?: "unknown"}",
        style = MaterialTheme.typography.bodyMedium
    )
    if (uid != null) {
        Text(
            "UID: ${uid.take(12)}...",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Spacer(Modifier.height(12.dp))

    if (isAnonymous) {
        var emailInput by remember { mutableStateOf("") }
        var passInput by remember { mutableStateOf("") }

        Text(
            "Link to email account to keep your data:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = passInput,
            onValueChange = { passInput = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { onLinkEmail(emailInput.trim(), passInput) },
            enabled = emailInput.isNotBlank() && passInput.length >= 6,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Link Email Account") }
    }

    Spacer(Modifier.height(8.dp))
    OutlinedButton(onClick = onSignOut, modifier = Modifier.fillMaxWidth()) {
        Text("Sign Out")
    }
}

@Composable
private fun SignedOutView(
    onSignInAnonymously: () -> Unit,
    onSignInWithEmail: (String, String) -> Unit,
    onCreateAccount: (String, String) -> Unit
) {
    var emailInput by remember { mutableStateOf("") }
    var passInput by remember { mutableStateOf("") }
    var isCreateMode by remember { mutableStateOf(false) }

    Button(
        onClick = onSignInAnonymously,
        modifier = Modifier.fillMaxWidth()
    ) { Text("Quick Start (Anonymous)") }

    Spacer(Modifier.height(12.dp))

    Text(
        "— or sign in with email —",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    Spacer(Modifier.height(8.dp))

    OutlinedTextField(
        value = emailInput,
        onValueChange = { emailInput = it },
        label = { Text("Email") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(4.dp))
    OutlinedTextField(
        value = passInput,
        onValueChange = { passInput = it },
        label = { Text("Password (6+ chars)") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {
                if (isCreateMode) onCreateAccount(emailInput.trim(), passInput)
                else onSignInWithEmail(emailInput.trim(), passInput)
            },
            enabled = emailInput.isNotBlank() && passInput.length >= 6,
            modifier = Modifier.weight(1f)
        ) { Text(if (isCreateMode) "Create Account" else "Sign In") }

        TextButton(onClick = { isCreateMode = !isCreateMode }) {
            Text(if (isCreateMode) "Have account?" else "New user?")
        }
    }
}

@Composable
private fun SyncSection(
    isSyncing: Boolean,
    lastResult: String?,
    error: String?,
    onPush: () -> Unit,
    onPull: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Cloud, null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Cloud Sync",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(12.dp))

            if (isSyncing) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Syncing...")
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = onPush, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.CloudUpload, null, modifier = Modifier.padding(end = 4.dp))
                        Text("Push")
                    }
                    OutlinedButton(onClick = onPull, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.CloudDownload, null, modifier = Modifier.padding(end = 4.dp))
                        Text("Pull")
                    }
                }
            }

            if (lastResult != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    lastResult,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun Modifier.align(alignment: Alignment.Horizontal): Modifier = this
