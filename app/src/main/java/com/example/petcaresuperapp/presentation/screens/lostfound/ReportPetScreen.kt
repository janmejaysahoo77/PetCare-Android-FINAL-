package com.example.petcaresuperapp.presentation.screens.lostfound

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petcaresuperapp.data.model.LostFoundPost
import com.example.petcaresuperapp.ui.theme.*
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPetScreen(
    navController: NavController,
    viewModel: LostFoundViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    var selectedPostType by remember { mutableStateOf("Lost Pet") }
    val postTypes = listOf("Lost Pet", "Found Pet", "Injured Animal")

    var petName by remember { mutableStateOf("") }

    var expandedAnimalType by remember { mutableStateOf(false) }
    var selectedAnimalType by remember { mutableStateOf("Dog") }
    val animalTypes = listOf("Dog", "Cat", "Bird", "Cow", "Other")

    var breed by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isFetchingLocation by remember { mutableStateOf(false) }
    var date by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var reward by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Media (images + videos)
    val selectedMediaUris = remember { mutableStateListOf<Uri>() }

    val multipleMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uris ->
        selectedMediaUris.clear()
        selectedMediaUris.addAll(uris)
    }

    // Location permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            isFetchingLocation = true
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                fusedClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        try {
                            @Suppress("DEPRECATION")
                            val geocoder = Geocoder(context, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                            if (!addresses.isNullOrEmpty()) {
                                val addr = addresses[0]
                                val parts = listOfNotNull(
                                    addr.subLocality,
                                    addr.locality,
                                    addr.adminArea
                                )
                                location = parts.joinToString(", ")
                            } else {
                                location = "${loc.latitude}, ${loc.longitude}"
                            }
                        } catch (e: Exception) {
                            location = "${loc.latitude}, ${loc.longitude}"
                        }
                    } else {
                        location = "Unable to fetch location"
                    }
                    isFetchingLocation = false
                }.addOnFailureListener {
                    location = "Location fetch failed"
                    isFetchingLocation = false
                }
            } catch (e: SecurityException) {
                location = "Permission denied"
                isFetchingLocation = false
            }
        }
    }

    fun fetchCurrentLocation() {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            isFetchingLocation = true
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                fusedClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        try {
                            @Suppress("DEPRECATION")
                            val geocoder = Geocoder(context, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                            if (!addresses.isNullOrEmpty()) {
                                val addr = addresses[0]
                                val parts = listOfNotNull(
                                    addr.subLocality,
                                    addr.locality,
                                    addr.adminArea
                                )
                                location = parts.joinToString(", ")
                            } else {
                                location = "${loc.latitude}, ${loc.longitude}"
                            }
                        } catch (e: Exception) {
                            location = "${loc.latitude}, ${loc.longitude}"
                        }
                    } else {
                        location = "Unable to fetch location"
                    }
                    isFetchingLocation = false
                }.addOnFailureListener {
                    location = "Location fetch failed"
                    isFetchingLocation = false
                }
            } catch (e: SecurityException) {
                location = "Permission denied"
                isFetchingLocation = false
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Animal", fontWeight = FontWeight.Bold, color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Post Type Section
            Text("Post Type", fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 16.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                postTypes.forEach { type ->
                    val isSelected = selectedPostType == type
                    val color = when (type) {
                        "Lost Pet" -> Color(0xFFE53935)
                        "Found Pet" -> Color(0xFF43A047)
                        "Injured Animal" -> Color(0xFFFF9800)
                        else -> Primary2026
                    }
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPostType = type },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) color.copy(alpha = 0.2f) else SurfaceDark,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E1E1E))
                    ) {
                        Text(
                            text = type,
                            modifier = Modifier.padding(vertical = 12.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) color else TextGray
                        )
                    }
                }
            }

            // Media Upload Section (Image + Video)
            Text("Photos / Videos", fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 16.sp)

            if (selectedMediaUris.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceDark)
                        .border(1.dp, Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                        .clickable {
                            multipleMediaLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Upload", tint = Primary2026, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tap to upload photos or videos", color = TextGray, fontSize = 14.sp)
                        Text("Up to 5 files", color = TextGray.copy(alpha = 0.6f), fontSize = 12.sp)
                    }
                }
            } else {
                // Show selected media thumbnails
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectedMediaUris) { uri ->
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(uri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Selected media",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Remove button
                            IconButton(
                                onClick = { selectedMediaUris.remove(uri) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(28.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    // Add more button
                    item {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceDark)
                                .border(1.dp, Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                                .clickable {
                                    multipleMediaLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add More", tint = Primary2026, modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }

            // Form Fields
            OutlinedTextField(
                value = petName,
                onValueChange = { petName = it },
                label = { Text("Pet Name (if known)", color = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedTextFieldColors()
            )

            // Animal Type Dropdown
            Box {
                OutlinedTextField(
                    value = selectedAnimalType,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Animal Type", color = TextGray) },
                    trailingIcon = {
                        IconButton(onClick = { expandedAnimalType = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Type", tint = TextWhite)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedAnimalType = true },
                    colors = outlinedTextFieldColors()
                )
                DropdownMenu(
                    expanded = expandedAnimalType,
                    onDismissRequest = { expandedAnimalType = false },
                    modifier = Modifier
                        .background(SurfaceDark)
                        .fillMaxWidth(0.9f)
                ) {
                    animalTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type, color = TextWhite) },
                            onClick = {
                                selectedAnimalType = type
                                expandedAnimalType = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("Breed / Color Marking", color = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedTextFieldColors()
            )

            // Location field with auto-fetch button
            OutlinedTextField(
                value = if (isFetchingLocation) "Fetching location..." else location,
                onValueChange = { location = it },
                label = { Text("Location (Area, City)", color = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(
                        onClick = { fetchCurrentLocation() }
                    ) {
                        if (isFetchingLocation) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Primary2026,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.MyLocation,
                                contentDescription = "Get Location",
                                tint = Primary2026
                            )
                        }
                    }
                },
                colors = outlinedTextFieldColors()
            )

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (e.g., 14 March 2026)", color = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedTextFieldColors()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description & Distinguishing Features", color = TextGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                colors = outlinedTextFieldColors()
            )

            OutlinedTextField(
                value = reward,
                onValueChange = { reward = it },
                label = { Text("Reward (Optional)", color = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedTextFieldColors()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Contact Phone Number", color = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedTextFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (location.isEmpty()) {
                        Toast.makeText(context, "Please provide a location", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val statusStr = selectedPostType.split(" ").first()
                    viewModel.submitReport(
                        petName = petName,
                        animalType = selectedAnimalType,
                        breed = breed,
                        location = location,
                        description = description + "\nDate: $date",
                        status = statusStr,
                        reward = reward.takeIf { it.isNotEmpty() },
                        mediaUris = selectedMediaUris,
                        onSuccess = {
                            Toast.makeText(context, "Report submitted successfully!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onError = { message ->
                            Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = Primary2026),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Submit Report", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Primary2026,
    unfocusedBorderColor = SurfaceDark,
    focusedContainerColor = SurfaceDark,
    unfocusedContainerColor = SurfaceDark,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    cursorColor = Primary2026
)

private val CircleShape = RoundedCornerShape(50)
