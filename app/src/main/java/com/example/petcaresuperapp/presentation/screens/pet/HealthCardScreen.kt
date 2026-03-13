package com.example.petcaresuperapp.presentation.screens.pet

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.petcaresuperapp.ui.theme.*
import com.example.petcaresuperapp.data.model.VetAppointment
import com.example.petcaresuperapp.data.model.MedicalRecord
import com.example.petcaresuperapp.presentation.components.*
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
                title = {
                    Text(
                        "Pet Health",
                        style = Typography.headlineLarge,
                        color = TextWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = BackgroundDark,
                contentColor = Primary2026
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
                                title,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
fun HealthRecordsFragment(
    viewModel: HealthViewModel
) {

    val context = LocalContext.current
    val scanner = remember { GmsBarcodeScanning.getClient(context) }

    val myPetId by viewModel.myPetId.collectAsState()

    val vaccinations = listOf(
        Vaccination(
            "Rabies",
            "15 May 2024",
            "Dr. Smith",
            "Completed",
            Icons.Default.Shield
        ),
        Vaccination(
            "Distemper",
            "20 Jun 2024",
            "Dr. Sarah",
            "Completed",
            Icons.Default.Vaccines
        ),
        Vaccination(
            "Parvovirus",
            "10 Oct 2024",
            "Dr. Mike",
            "Scheduled",
            Icons.Default.Timer
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(
            top = 20.dp,
            bottom = 24.dp
        )
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
                                Toast.makeText(
                                    context,
                                    "Invalid QR Code",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener {

                            Toast.makeText(
                                context,
                                "Scan failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            )
        }

        item {

            Text(
                "Health Summary",
                style = Typography.titleLarge,
                color = TextWhite
            )
        }

        item {
            HealthSummary()
        }

        item {

            Text(
                "Vaccination History",
                style = Typography.titleLarge,
                color = TextWhite
            )
        }

        items(vaccinations) { vaccine ->

            VaccinationCard(
                name = vaccine.name,
                date = vaccine.date,
                vet = vaccine.vet,
                status = vaccine.status,
                icon = vaccine.icon
            )
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun QRSection(
    petId: String?,
    onScanClick: () -> Unit
) {

    var qrBitmap by remember {
        mutableStateOf<android.graphics.Bitmap?>(null)
    }

    LaunchedEffect(petId) {

        if (!petId.isNullOrEmpty()) {

            qrBitmap =
                com.example.petcaresuperapp.utils.QRCodeGenerator
                    .generateQRCode(petId)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(32.dp)
            ),
        shape = RoundedCornerShape(32.dp),
        color = SurfaceDark
    ) {

        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(24.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {

                if (qrBitmap != null) {

                    Image(
                        bitmap = qrBitmap!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {

                    Icon(
                        Icons.Default.QrCode2,
                        contentDescription = null,
                        tint = Primary2026,
                        modifier = Modifier.size(120.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Buddy's Health Pass",
                style = Typography.headlineSmall,
                color = TextWhite
            )

            Text(
                "Show this QR for instant veterinary access",
                style = Typography.bodyMedium,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onScanClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary2026
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {

                Icon(
                    Icons.Default.QrCodeScanner,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text("Scan Pet ID")
            }
        }
    }
}

@Composable
fun HealthSummary() {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Info2026.copy(alpha = 0.1f)
    ) {

        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Info2026.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = Info2026
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                "Buddy is in perfect health. All vaccinations are up to date.",
                style = Typography.bodyMedium,
                color = TextWhite
            )
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
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Book a Vet Appointment",
                style = Typography.headlineSmall,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Find trusted veterinarians and schedule appointments.",
                style = Typography.bodyMedium,
                color = TextGray
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
        item { Spacer(modifier = Modifier.height(60.dp)) }
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
        title = { Text(text = "Book with $vetName", style = Typography.titleLarge, color = TextWhite) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = selectedPet,
                    onValueChange = { selectedPet = it },
                    label = { Text("Pet Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedLabelColor = Primary2026,
                        unfocusedLabelColor = TextGray
                    )
                )
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = { selectedDate = it },
                    label = { Text("Date (e.g. 20 Oct 2024)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedLabelColor = Primary2026,
                        unfocusedLabelColor = TextGray
                    )
                )
                OutlinedTextField(
                    value = selectedTime,
                    onValueChange = { selectedTime = it },
                    label = { Text("Time (e.g. 10:00 AM)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedLabelColor = Primary2026,
                        unfocusedLabelColor = TextGray
                    )
                )
                OutlinedTextField(
                    value = problemDescription,
                    onValueChange = { problemDescription = it },
                    label = { Text("Problem Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedLabelColor = Primary2026,
                        unfocusedLabelColor = TextGray
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    onConfirm(selectedPet, selectedDate, selectedTime, problemDescription) 
                },
                enabled = !isLoading && selectedPet.isNotBlank() && selectedDate.isNotBlank() && selectedTime.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary2026),
                shape = RoundedCornerShape(12.dp)
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
                Text("Cancel", color = TextGray)
            }
        },
        containerColor = SurfaceDark
    )
}

@Composable
fun VetCardPlaceholder(name: String, specialty: String, rating: String, onBookClick: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Primary2026.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Primary2026, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, style = Typography.titleMedium, color = TextWhite)
                    Text(specialty, style = Typography.bodySmall, color = TextGray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(rating, style = Typography.labelSmall, color = TextGray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { onBookClick(name) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary2026),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Book Appointment", style = Typography.labelLarge)
            }
        }
    }
}