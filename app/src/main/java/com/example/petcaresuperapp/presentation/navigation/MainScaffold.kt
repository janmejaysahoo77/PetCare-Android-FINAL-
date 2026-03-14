package com.example.petcaresuperapp.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch
import com.example.petcaresuperapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavController,
    showTopBar: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem("Home", Screen.Home.route, Icons.Rounded.Home),
        BottomNavItem("Health", Screen.HealthCard.route, Icons.Rounded.HealthAndSafety),
        BottomNavItem("Activity", Screen.ActivityTracker.route, Icons.Rounded.DirectionsRun),
        BottomNavItem("Community", Screen.Community.route, Icons.Rounded.Groups),
        BottomNavItem("PetShop", Screen.Marketplace.route, Icons.Rounded.ShoppingCart)
    )

    val drawerItems = listOf(
        DrawerItem("Profile", Screen.UserProfile.route, Icons.Rounded.Person),
        DrawerItem("Settings", Screen.Settings.route, Icons.Rounded.Settings),
        DrawerItem("Logout", Screen.Login.route, Icons.Rounded.ExitToApp)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = SurfaceDark,
                drawerContentColor = TextWhite
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        "PetCare",
                        style = Typography.headlineLarge,
                        color = Primary2026
                    )
                }
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (item.title == "Logout") {
                                navController.navigate(item.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            } else {
                                navController.navigate(item.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Primary2026.copy(alpha = 0.2f),
                            selectedIconColor = Primary2026,
                            selectedTextColor = Primary2026,
                            unselectedIconColor = TextGray,
                            unselectedTextColor = TextGray
                        ),
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (showTopBar) {
                    TopAppBar(
                        title = { 
                            Text(
                                "PetCare", 
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            ) 
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextWhite)
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate(Screen.AiAssistant.route) }) {
                                Icon(Icons.Rounded.AutoAwesome, contentDescription = "AI Assistant", tint = Primary2026)
                            }
                            IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                                Icon(Icons.Rounded.Notifications, contentDescription = "Notifications", tint = TextWhite)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = BackgroundDark,
                            titleContentColor = TextWhite
                        )
                    )
                }
            },
            bottomBar = {
                val bottomBarRoutes = listOf(
                    Screen.Home.route,
                    Screen.HealthCard.route,
                    Screen.ActivityTracker.route,
                    Screen.Community.route,
                    Screen.Marketplace.route
                )

                if (currentRoute in bottomBarRoutes) {
                    NavigationBar(
                        containerColor = SurfaceDark.copy(alpha = 0.95f),
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(Screen.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    val iconScale by animateFloatAsState(
                                        targetValue = if (selected) 1.2f else 1f,
                                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                        label = "navIconScale"
                                    )
                                    Icon(
                                        item.icon, 
                                        contentDescription = item.title,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .scale(iconScale),
                                        tint = if (selected) Primary2026 else TextGray
                                    )
                                },
                                label = {
                                    Text(
                                        item.title,
                                        fontSize = 10.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) Primary2026 else TextGray
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Primary2026.copy(alpha = 0.1f),
                                    selectedIconColor = Primary2026,
                                    unselectedIconColor = TextGray,
                                    selectedTextColor = Primary2026,
                                    unselectedTextColor = TextGray
                                )
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                if (currentRoute == Screen.Home.route) {
                    var pressed by remember { mutableStateOf(false) }
                    val scale by animateFloatAsState(
                        targetValue = if (pressed) 0.9f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "fabScale"
                    )

                    FloatingActionButton(
                        onClick = { 
                            navController.navigate(Screen.AddPet.route)
                        },
                        containerColor = Primary2026,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .scale(scale)
                            .padding(bottom = 8.dp),
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = "Add Pet", modifier = Modifier.size(28.dp))
                    }
                }
            },
            containerColor = BackgroundDark
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                content(paddingValues)
            }
        }
    }
}
