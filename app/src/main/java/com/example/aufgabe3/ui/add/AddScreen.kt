package com.example.aufgabe3.ui.add

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.aufgabe3.model.BookingEntry
import com.example.aufgabe3.viewmodel.SharedViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    var name by remember { mutableStateOf("") }
    var arrivalDate by remember { mutableStateOf<LocalDate?>(null) }
    var departureDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDateRangePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Booking Entry") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = if (arrivalDate != null && departureDate != null) {
                    "${arrivalDate!!.format(dateFormatter)} - ${departureDate!!.format(dateFormatter)}"
                } else {
                    ""
                },
                onValueChange =  {},
                label = { Text("Select Date Range") },
                enabled = false,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDateRangePicker = true },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // TODO Error handling and creating new BookingEntry and save in sharedViewModel
                    if (name.isBlank() || arrivalDate == null || departureDate == null) {
                        // Fehlerbehandlung
                        Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                    } else {
                        sharedViewModel.addBookingEntry(
                            BookingEntry(name, arrivalDate!!, departureDate!!)
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Save")
            }
        }
    }

    // TODO implement DateRangePicker Dialog logic
    if (showDateRangePicker) {
        DateRangePickerModal(
            onDateRangeSelected = { startDate, endDate ->
                arrivalDate = startDate
                departureDate = endDate
                showDateRangePicker = false
            },
            onDismiss = { showDateRangePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (LocalDate, LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    // TODO implement DateRangePicker see https://developer.android.com/develop/ui/compose/components/datepickers?hl=de
    val state = rememberDateRangePickerState()
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val startMillis = state.selectedStartDateMillis
                val endMillis = state.selectedEndDateMillis
                if (startMillis != null && endMillis != null) {
                    val startDate = Instant.ofEpochMilli(startMillis)
                        .atZone(ZoneId.systemDefault()).toLocalDate()
                    val endDate = Instant.ofEpochMilli(endMillis)
                        .atZone(ZoneId.systemDefault()).toLocalDate()
                    onDateRangeSelected(startDate, endDate)
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            DateRangePicker(state = state)
        }
    )
}