package com.example.petcaresuperapp.presentation.screens.pet

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.petcaresuperapp.data.model.VetAppointment
import com.example.petcaresuperapp.data.model.MedicalRecord
import com.example.petcaresuperapp.ui.theme.*
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.launch

data class Vaccination(
    val name: String,
    val date: String,
    val vet: String,
    val status: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthCardScreen(
    navController: NavController,
    vetViewModel: VetAppointmentViewModel = hiltViewModel(),
    healthViewModel: HealthViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf("Health Records", "Vet Appointment")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pet Health", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = BackgroundColor,
                contentColor = PrimaryColor
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selectedContentColor = PrimaryColor,
                        unselectedContentColor = TextSecondary
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> HealthRecordsFragment(healthViewModel)
                    1 -> VetAppointmentFragment(vetViewModel)
                }
            }
        }
    }
}

@Composable
fun HealthRecordsFragment(viewModel: HealthViewModel) {
    val context = LocalContext.current
    val scanner = remember { GmsBarcodeScanning.getClient(context) }
    val recordsState by viewModel.recordsState.collectAsState()
    val myPetId by viewModel.myPetId.collectAsState()

    val vaccinations = listOf(
        Vaccination("Rabies", "15 May 2024", "Dr. Smith", "Completed", Icons.Default.Shield),
        Vaccination("Distemper", "20 Jun 2024", "Dr. Sarah", "Completed", Icons.Default.Vaccines),
        Vaccination("Parvovirus", "10 Oct 2024", "Dr. Mike", "Scheduled", Icons.Default.Timer)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {
        item {
            QRSection(
                petId = myPetId,
                onScanClick = {
                    scanner.startScan()
                    .addOnSuccessListener { barcode ->
                        val petId = barcode.rawValue
                        if (petId != null) {
                            viewModel.fetchMedicalRecords(petId)
                        } else {
                            Toast.makeText(context, "Invalid QR Code", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Scan cancelled or failed", Toast.LENGTH_SHORT).show()
                    }
            })
        }

        when (val state = recordsState) {
            is RecordsState.Idle -> {
                item {
                    Text(
                        "Vaccination History",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                items(vaccinations) { vaccine ->
                    VaccineCard(vaccine)
                }
                item {
                    HealthSummary()
                }
            }
            is RecordsState.Loading -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryColor)
                    }
                }
            }
            is RecordsState.Empty -> {
                item {
                    Text(
                        text = "No medical records found for this pet.",
                        fontSize = 16.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
            is RecordsState.Error -> {
                item {
                    Text(
                        text = state.message,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
            is RecordsState.Success -> {
                item {
                    Text(
                        "Medical Records",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                items(state.records) { record ->
                    MedicalRecordCard(record)
                }
            }
        }
    }
}

@Composable
fun MedicalRecordCard(record: MedicalRecord) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceColor,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(record.petName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            
            if (record.diagnosis.isNotEmpty()) {
                Text("Diagnosis: ${record.diagnosis}", fontSize = 14.sp, color = TextPrimary)
            }
            if (record.treatmentNotes.isNotEmpty()) {
                Text("Notes: ${record.treatmentNotes}", fontSize = 14.sp, color = TextSecondary)
            }
            if (record.prescription.isNotEmpty()) {
                Text("Prescription: ${record.prescription}", fontSize = 14.sp, color = TextPrimary)
            }
            if (record.vaccinationName.isNotEmpty()) {
                Text("Vaccination: ${record.vaccinationName} (${record.vaccinationDate})", fontSize = 14.sp, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                val visitText = if (record.nextVisitDate.isNotEmpty()) "Next Visit: ${record.nextVisitDate}" else ""
                Text(visitText, fontSize = 12.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
                Text("Vet: ${record.vetName}", fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun VetAppointmentFragment(viewModel: VetAppointmentViewModel) {
    val context = LocalContext.current
    val bookingState by viewModel.bookingState.collectAsState()
    
    var showBookingDialog by remember { mutableStateOf(false) }
    var selectedVet by remember { mutableStateOf<Pair<String, String>?>(null) } // Pair of VetId, VetName

    LaunchedEffect(bookingState) {
        when (bookingState) {
            is BookingState.Success -> {
                Toast.makeText(context, "Appointment booked successfully", Toast.LENGTH_SHORT).show()
                showBookingDialog = false
                viewModel.resetState()
            }
            is BookingState.Error -> {
                Toast.makeText(context, (bookingState as BookingState.Error).message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    if (showBookingDialog && selectedVet != null) {
        BookingDialog(
            vetName = selectedVet!!.second,
            isLoading = bookingState is BookingState.Loading,
            onDismiss = { showBookingDialog = false },
            onConfirm = { petName, date, time, description ->
                viewModel.bookAppointment(
                    VetAppointment(
                        petName = petName,
                        vetId = selectedVet!!.first,
                        vetName = selectedVet!!.second,
                        appointmentDate = date,
                        appointmentTime = time,
                        problemDescription = description
                    )
                )
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {
        item {
            Text(
                text = "Book a Vet Appointment",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Find trusted veterinarians and schedule appointments.",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }

        item {
            VetCardPlaceholder(
                name = "Dr. John Smith",
                specialty = "General Veterinarian",
                rating = "4.9 (120 reviews)"
            ) { vetName ->
                selectedVet = Pair("vet_001", vetName)
                showBookingDialog = true
            }
        }
        item {
            VetCardPlaceholder(
                name = "Dr. Emily Davis",
                specialty = "Pet Surgeon",
                rating = "4.8 (95 reviews)"
            ) { vetName ->
                selectedVet = Pair("vet_002", vetName)
                showBookingDialog = true
            }
        }
        item {
            VetCardPlaceholder(
                name = "Dr. Michael Lee",
                specialty = "Pet Dermatologist",
                rating = "4.7 (80 reviews)"
            ) { vetName ->
                selectedVet = Pair("vet_003", vetName)
                showBookingDialog = true
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDialog(
    vetName: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (petName: String, date: String, time: String, description: String) -> Unit
) {
    var selectedPet by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var problemDescription by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text(text = "Book with $vetName", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = selectedPet,
                    onValueChange = { selectedPet = it },
                    label = { Text("Pet Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = { selectedDate = it },
                    label = { Text("Date (e.g. 20 Oct 2024)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = selectedTime,
                    onValueChange = { selectedTime = it },
                    label = { Text("Time (e.g. 10:00 AM)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = problemDescription,
                    onValueChange = { problemDescription = it },
                    label = { Text("Problem Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    onConfirm(selectedPet, selectedDate, selectedTime, problemDescription) 
                },
                enabled = !isLoading && selectedPet.isNotBlank() && selectedDate.isNotBlank() && selectedTime.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Confirm Booking")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = BackgroundColor
    )
}

@Composable
fun VetCardPlaceholder(name: String, specialty: String, rating: String, onBookClick: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceColor,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(AccentGradStart.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = AccentGradStart, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                    Text(specialty, fontSize = 14.sp, color = TextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(rating, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onBookClick(name) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Book Appointment", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun QRSection(petId: String?, onScanClick: () -> Unit) {
    val context = LocalContext.current
    var qrBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(petId) {
        if (!petId.isNullOrEmpty()) {
            qrBitmap = com.example.petcaresuperapp.utils.QRCodeGenerator.generateQRCode(petId)
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceColor,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap!!.asImageBitmap(),
                        contentDescription = "Pet QR Code",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = "Default QR Placeholder",
                        modifier = Modifier.fillMaxSize(),
                        tint = PrimaryColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Buddy's Health ID", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(
                "Show this QR code for quick vet access",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onScanClick,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scan Pet QR Code", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun VaccineCard(vaccine: Vaccination) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceColor,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (vaccine.status == "Completed") SuccessGradEnd.copy(alpha = 0.1f) 
                        else SecondaryGradStart.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    vaccine.icon, 
                    contentDescription = null, 
                    tint = if (vaccine.status == "Completed") SuccessGradStart else SecondaryColor
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(vaccine.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${vaccine.date} • ${vaccine.vet}", fontSize = 12.sp, color = TextSecondary)
            }
            Badge(
                containerColor = if (vaccine.status == "Completed") SuccessGradStart.copy(alpha = 0.1f) 
                                 else AccentGradStart.copy(alpha = 0.1f)
            ) {
                Text(
                    vaccine.status,
                    color = if (vaccine.status == "Completed") SuccessGradStart else AccentGradStart,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun HealthSummary() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = PrimaryColor,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Health Summary: Buddy is in perfect health. No pending alerts.",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}
