package com.example.petcaresuperapp.presentation.screens.pet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.petcaresuperapp.data.model.ContactRequest
import com.example.petcaresuperapp.data.model.LostFoundPost
import com.example.petcaresuperapp.data.model.VetAppointment
import com.example.petcaresuperapp.presentation.navigation.Screen
import com.example.petcaresuperapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold, color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary2026)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                item {
                    NotificationHeader(notifications.size)
                }
                
                if (notifications.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No active updates", color = TextGray)
                        }
                    }
                } else {
                    item {
                        Text(
                            "Live Tracking & Updates",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }
                    items(notifications) { item ->
                        when (item) {
                            is NotificationItem.Appointment -> AppointmentTrackingTile(item.appointment)
                            is NotificationItem.Tracking -> LostFoundTrackingTile(item.post)
                            is NotificationItem.Contact -> ContactRequestTile(
                                request = item.request,
                                currentUserId = viewModel.currentUserId,
                                onAccept = { viewModel.updateRequestStatus(item.request.requestId, "accepted") },
                                onReject = { viewModel.updateRequestStatus(item.request.requestId, "rejected") },
                                onChat = { navController.navigate(Screen.Chat.route) },
                                onCall = { /* Intent to call */ },
                                onVideoCall = { navController.navigate(Screen.VideoCall.route) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationHeader(activeCount: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = SurfaceDark,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Primary2026.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Primary2026)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(if (activeCount > 0) "Updates Available" else "All Caught Up!", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextWhite)
                Text("You have $activeCount active appointments.", fontSize = 12.sp, color = TextGray)
            }
        }
    }
}

@Composable
fun AppointmentTrackingTile(appointment: VetAppointment) {
    val (progress, statusText, statusColor) = when (appointment.status.lowercase()) {
        "pending" -> Triple(0.33f, "Waiting for Confirmation", Color(0xFFFFA000))
        "confirmed" -> Triple(0.66f, "Appointment Confirmed", Secondary2026)
        "completed" -> Triple(1.0f, "Completed", Primary2026)
        "cancelled" -> Triple(0f, "Cancelled", Color.Red)
        else -> Triple(0.33f, appointment.status.uppercase(), Color.Gray)
    }

    val timeString = try {
        val sdf = SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault())
        sdf.format(Date(appointment.appointmentTimestamp))
    } catch (e: Exception) {
        "Scheduled Time"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceDark,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Primary2026.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Primary2026)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Vet: ${appointment.vetName}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextWhite)
                    Text("Pet: ${appointment.petName}", fontSize = 13.sp, color = TextGray)
                }
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        appointment.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(timeString, fontSize = 13.sp, color = TextGray)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = statusColor,
                    trackColor = Color.White.copy(alpha = 0.05f),
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Pending", fontSize = 10.sp, color = if (progress >= 0.33f) TextWhite else TextGray)
                    Text("Confirmed", fontSize = 10.sp, color = if (progress >= 0.66f) TextWhite else TextGray)
                    Text("Completed", fontSize = 10.sp, color = if (progress >= 1.0f) TextWhite else TextGray)
                }
            }
        }
    }
}
@Composable
fun LostFoundTrackingTile(post: LostFoundPost) {
    val statusColor = when (post.status.lowercase()) {
        "lost" -> Color.Red
        "found" -> Primary2026
        else -> Secondary2026
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceDark,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(statusColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(if (post.status == "lost") Icons.Default.Search else Icons.Default.CheckCircle, contentDescription = null, tint = statusColor)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Lost Pet: ${post.petName}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextWhite)
                    Text("Last seen: ${post.location}", fontSize = 13.sp, color = TextGray)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        post.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { 0.5f }, // Placeholder for tracking progress
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                color = statusColor,
                trackColor = Color.White.copy(alpha = 0.05f)
            )
        }
    }
}

@Composable
fun ContactRequestTile(
    request: ContactRequest,
    currentUserId: String,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onChat: () -> Unit,
    onCall: () -> Unit,
    onVideoCall: () -> Unit
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    
    val isOwner = request.receiverId == currentUserId
    val isSender = request.senderId == currentUserId

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceDark,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Primary2026.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Primary2026)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Help for ${request.petName}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextWhite)
                    val subTitle = when (request.status) {
                        "accepted" -> "Request Accepted"
                        "rejected" -> "Request Declined"
                        else -> if (isOwner) "Potentially spotted your pet!" else "Waiting for owner's response"
                    }
                    Text(subTitle, fontSize = 13.sp, color = TextGray)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Box(modifier = Modifier.fillMaxWidth()) {
                when (request.status) {
                    "pending" -> {
                        if (isOwner) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedButton(
                                    onClick = onReject,
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                                ) {
                                    Text("Decline")
                                }
                                Button(
                                    onClick = onAccept,
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Primary2026),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Contact", color = Color.White)
                                }
                            }
                        } else {
                            Button(
                                onClick = { /* Already requested */ },
                                enabled = false,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(disabledContainerColor = BackgroundDark, disabledContentColor = TextGray),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Requested")
                            }
                        }
                    }
                    "accepted" -> {
                        Column {
                            Button(
                                onClick = { showMenu = true },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary2026),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Accepted", color = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                                }
                            }
                            
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                modifier = Modifier.background(SurfaceDark).width(200.dp)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Chat", color = TextWhite) },
                                    onClick = { showMenu = false; onChat() },
                                    leadingIcon = { Icon(Icons.Default.Chat, contentDescription = null, tint = Primary2026) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Call", color = TextWhite) },
                                    onClick = { 
                                        showMenu = false
                                        val intent = Intent(Intent.ACTION_DIAL)
                                        context.startActivity(intent)
                                    },
                                    leadingIcon = { Icon(Icons.Default.Call, contentDescription = null, tint = Primary2026) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Video Call", color = TextWhite) },
                                    onClick = { showMenu = false; onVideoCall() },
                                    leadingIcon = { Icon(Icons.Default.VideoCall, contentDescription = null, tint = Secondary2026) }
                                )
                            }
                        }
                    }
                    "rejected" -> {
                        Text("This request was declined.", color = Color.Red, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
