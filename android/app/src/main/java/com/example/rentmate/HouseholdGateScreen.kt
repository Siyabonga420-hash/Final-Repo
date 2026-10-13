package com.example.rentmate

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun HouseholdGateScreen(onApproved: (String) -> Unit, onLogout: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("rentmate_prefs", Context.MODE_PRIVATE) }
    val scope = rememberCoroutineScope()
    var available by remember { mutableStateOf<List<HouseholdDto>>(emptyList()) }
    var myRequests by remember { mutableStateOf<List<HouseholdDto>>(emptyList()) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var loaded by remember { mutableStateOf(false) }

    suspend fun refresh() {
        busy = true
        error = null
        try {
            // Never trust the previously cached household ID for authorization.
            prefs.edit().remove("current_household_id").apply()
            val mine = ApiClient.householdApi.myHouseholds().households
            myRequests = mine
            val approved = mine.firstOrNull { it.status == "approved" }
            if (approved != null) {
                prefs.edit().putString("current_household_id", approved.id).apply()
                onApproved(approved.id)
                return
            }
            available = ApiClient.householdApi.availableHouseholds().households
            loaded = true
        } catch (e: Exception) {
            error = if (e is HttpException && e.code() == 401) {
                "Your session has expired. Please log in again."
            } else {
                "Could not load households. Check your connection and try again."
            }
        } finally {
            busy = false
        }
    }

    LaunchedEffect(Unit) { refresh() }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Find Your Household", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Choose an available household and request manager approval.")
        Spacer(Modifier.height(12.dp))
        Row {
            OutlinedButton(onClick = { scope.launch { refresh() } }, enabled = !busy) {
                Text("Refresh status")
            }
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = {
                prefs.edit().remove("auth_token").remove("current_household_id").apply()
                onLogout()
            }) { Text("Log out") }
        }
        if (busy) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        val pending = myRequests.filter { it.status == "pending" }
        if (pending.isNotEmpty()) {
            Text("Awaiting manager approval", style = MaterialTheme.typography.titleMedium)
            pending.forEach { Text("• ${it.name} — Pending") }
            Spacer(Modifier.height(12.dp))
        }
        if (loaded && available.isEmpty()) Text("No available households yet. Ask a property manager to add one.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(available, key = { it.id }) { household ->
                val spaces = (household.capacity ?: 0) - (household.occupied ?: 0)
                val requested = myRequests.any { it.id == household.id && it.status == "pending" }
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(household.name, style = MaterialTheme.typography.titleMedium)
                        Text("${spaces.coerceAtLeast(0)} space(s) available")
                        Button(
                            enabled = !busy && !requested && spaces > 0 && pending.isEmpty(),
                            onClick = {
                                scope.launch {
                                    busy = true
                                    error = null
                                    try {
                                        ApiClient.householdApi.joinHousehold(JoinHouseholdRequest(household.id))
                                        refresh()
                                    } catch (e: Exception) {
                                        error = if (e is HttpException) "Request failed (HTTP ${e.code()})." else "Request failed. Check your connection."
                                    } finally { busy = false }
                                }
                            }
                        ) { Text(if (requested) "Pending approval" else "Request to Join") }
                    }
                }
            }
        }
    }
}
