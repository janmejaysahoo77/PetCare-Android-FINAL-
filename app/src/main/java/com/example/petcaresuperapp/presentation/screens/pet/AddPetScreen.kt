package com.example.petcaresuperapp.presentation.screens.pet

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.petcaresuperapp.presentation.components.GradientButton
import com.example.petcaresuperapp.ui.theme.*
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    navController: NavHostController,
    viewModel: RegisterPetViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Add Pet", "Adopt Pet")

    // Form states
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val context = LocalContext.current
    val registerState by viewModel.registerState.collectAsState()

    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterPetState.Success -> {
                Toast.makeText(context, "Pet Registered Successfully", Toast.LENGTH_SHORT).show()
                name = ""
                breed = ""
                age = ""
                weight = ""
                selectedGender = ""
                selectedImageUri = null
                viewModel.resetState()
            }
            is RegisterPetState.Error -> {
                Toast.makeText(context, (registerState as RegisterPetState.Error).message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pet Center", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
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
            // Fixed TabRow
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = BackgroundColor,
                contentColor = PrimaryColor,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            // Scrollable Content area
            Box(modifier = Modifier.fillMaxSize()) {
                if (selectedTabIndex == 0) {
                    AddPetForm(
                        name = name,
                        onNameChange = { name = it },
                        breed = breed,
                        onBreedChange = { breed = it },
                        age = age,
                        onAgeChange = { age = it },
                        weight = weight,
                        onWeightChange = { weight = it },
                        selectedGender = selectedGender,
                        onGenderChange = { selectedGender = it },
                        isLoading = registerState is RegisterPetState.Loading,
                        onSave = {
                            // Validation
                            when {
                                name.isBlank() -> Toast.makeText(context, "Pet Name cannot be empty", Toast.LENGTH_SHORT).show()
                                breed.isBlank() -> Toast.makeText(context, "Breed cannot be empty", Toast.LENGTH_SHORT).show()
                                age.toIntOrNull() == null -> Toast.makeText(context, "Age must be a number", Toast.LENGTH_SHORT).show()
                                weight.toDoubleOrNull() == null -> Toast.makeText(context, "Weight must be a number", Toast.LENGTH_SHORT).show()
                                selectedGender.isBlank() -> Toast.makeText(context, "Please select a gender", Toast.LENGTH_SHORT).show()
                                else -> viewModel.registerPet(name, breed, age, weight, selectedGender, selectedImageUri)
                            }
                        },
                        imageUri = selectedImageUri,
                        onAddPhoto = { photoPickerLauncher.launch("image/*") }
                    )
                } else {
                    com.example.petcaresuperapp.presentation.ui.AdoptPetScreen(
                        onPetClick = { petId ->
                            navController.navigate("pet_details/$petId")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AddPetForm(
    name: String,
    onNameChange: (String) -> Unit,
    breed: String,
    onBreedChange: (String) -> Unit,
    age: String,
    onAgeChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit,
    selectedGender: String,
    onGenderChange: (String) -> Unit,
    isLoading: Boolean,
    onSave: () -> Unit,
    imageUri: Uri?,
    onAddPhoto: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Photo Upload Placeholder
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(PrimaryColor.copy(alpha = 0.1f))
                .clickable { if (!isLoading) onAddPhoto() },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(32.dp))
                    Text("Add Photo", fontSize = 12.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        PetInputField(label = "Pet Name", value = name, onValueChange = onNameChange, icon = Icons.Default.Pets)
        PetInputField(label = "Breed", value = breed, onValueChange = onBreedChange, icon = Icons.Default.Category)
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                PetInputField(
                    label = "Age",
                    value = age,
                    onValueChange = onAgeChange,
                    icon = Icons.Default.CalendarToday,
                    keyboardType = KeyboardType.Number
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                PetInputField(
                    label = "Weight (kg)",
                    value = weight,
                    onValueChange = onWeightChange,
                    icon = Icons.Default.MonitorWeight,
                    keyboardType = KeyboardType.Decimal
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Gender",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GenderChip("Male", selectedGender == "Male", Modifier.weight(1f)) { onGenderChange("Male") }
            GenderChip("Female", selectedGender == "Female", Modifier.weight(1f)) { onGenderChange("Female") }
        }
        
        Spacer(modifier = Modifier.height(40.dp))

        if (isLoading) {
            CircularProgressIndicator(color = PrimaryColor)
        } else {
            GradientButton(
                text = "Register Pet",
                onClick = onSave,
                gradient = PrimaryGradient
            )
        }
        // Extra spacer for bottom padding in scroll
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PetInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp)) {
        Text(label, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(icon, contentDescription = null, tint = PrimaryColor) },
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = PrimaryColor,
                unfocusedContainerColor = SurfaceColor,
                focusedContainerColor = SurfaceColor
            )
        )
    }
}

@Composable
fun GenderChip(label: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(50.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) PrimaryColor else SurfaceColor,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, DividerColor)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (isSelected) Color.White else TextSecondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
