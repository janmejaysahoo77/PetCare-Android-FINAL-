package com.example.petcaresuperapp.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch

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
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
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
                        title = { Text("PetCare") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate(Screen.AiAssistant.route) }) {
                                Icon(Icons.Rounded.AutoAwesome, contentDescription = "AI Assistant")
                            }
                            IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                                Icon(Icons.Rounded.Notifications, contentDescription = "Notifications")
                            }
                        }
                    )
                }
            },
            bottomBar = {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) }
                        )
                    }
                }
            },
            floatingActionButton = {
                if (currentRoute == Screen.Home.route) {
                    FloatingActionButton(
                        onClick = { navController.navigate(Screen.AddPet.route) },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = "Add Pet")
                    }
                }
            }
        ) { paddingValues ->
            content(paddingValues)
        }
    }
}
