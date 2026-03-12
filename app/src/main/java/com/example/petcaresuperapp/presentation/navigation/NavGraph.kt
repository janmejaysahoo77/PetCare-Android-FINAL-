package com.example.petcaresuperapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.petcaresuperapp.presentation.screens.auth.LoginScreen
import com.example.petcaresuperapp.presentation.screens.auth.SignUpScreen
import com.example.petcaresuperapp.presentation.screens.home.HomeScreen
import com.example.petcaresuperapp.presentation.screens.onboarding.OnboardingScreen
import com.example.petcaresuperapp.presentation.screens.splash.SplashScreen
import com.example.petcaresuperapp.presentation.screens.pet.*
import com.example.petcaresuperapp.presentation.screens.vet.*
import com.example.petcaresuperapp.presentation.screens.community.*
import com.example.petcaresuperapp.presentation.screens.adoption.*
import com.example.petcaresuperapp.presentation.screens.dashboards.*
import com.example.petcaresuperapp.presentation.screens.ai.*
import com.example.petcaresuperapp.presentation.screens.emergency.*
import com.example.petcaresuperapp.presentation.screens.education.*
import com.example.petcaresuperapp.presentation.screens.store.*
import com.example.petcaresuperapp.presentation.screens.profile.*
import com.example.petcaresuperapp.presentation.screens.discovery.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    
    // Pet Module
    object PetProfile : Screen("pet_profile")
    object HealthCard : Screen("health_card")
    object Reminders : Screen("reminders")
    object ActivityTracker : Screen("activity_tracker")
    object AddPet : Screen("add_pet")
    object GpsTracking : Screen("gps_tracking")
    object BlockchainIdentity : Screen("blockchain_identity")
    object HealthAnalytics : Screen("health_analytics")
    
    // Vet Module
    object VetSearch : Screen("vet_search")
    object VetDetail : Screen("vet_detail/{vetId}")
    object AppointmentBooking : Screen("book_appointment/{vetId}")
    object MyAppointments : Screen("my_appointments")
    object Telemedicine : Screen("telemedicine")
    object VideoCall : Screen("video_call")
    object QrScanner : Screen("qr_scanner")
    
    // Community
    object Community : Screen("community")
    object LostFound : Screen("lost_found")
    object ReportPet : Screen("report_pet")
    object Chat : Screen("chat")
    object RescueForum : Screen("rescue_forum")
    
    // Adoption
    object Adoption : Screen("adoption")
    object AdoptionDetail : Screen("adoption_detail/{petId}")
    object AdoptionApplication : Screen("adoption_application/{petName}")
    object SuccessStories : Screen("success_stories")
    
    // Dashboards
    object VetDashboard : Screen("vet_dashboard")
    object ShelterDashboard : Screen("shelter_dashboard")
    object StoreDashboard : Screen("store_dashboard")
    object AdoptionRequestDetail : Screen("adoption_request_detail/{requestId}")
    
    // AI Tools
    object AiAssistant : Screen("ai_assistant")
    
    // Emergency
    object EmergencySos : Screen("emergency_sos")
    
    // Education
    object Education : Screen("education")
    
    // Marketplace
    object Marketplace : Screen("marketplace")
    
    // Profile
    object UserProfile : Screen("user_profile")
    
    // Discovery
    object DiscoveryMap : Screen("discovery_map")
}

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(onNext = { destination ->
                navController.navigate(destination) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(onFinished = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }
        composable(route = Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        
        // Pet Routes
        composable(route = Screen.PetProfile.route) { PetProfileScreen(navController) }
        composable(route = Screen.AddPet.route) { AddPetScreen(navController) }
        composable(route = Screen.HealthCard.route) { HealthCardScreen(navController) }
        composable(route = Screen.Reminders.route) { RemindersScreen(navController) }
        composable(route = Screen.ActivityTracker.route) { ActivityTrackerScreen(navController) }
        composable(route = Screen.GpsTracking.route) { GpsTrackingScreen(navController) }
        composable(route = Screen.BlockchainIdentity.route) { BlockchainIdentityScreen(navController) }
        composable(route = Screen.HealthAnalytics.route) { HealthAnalyticsScreen(navController) }
        
        // Vet Routes
        composable(route = Screen.VetSearch.route) { VetSearchScreen(navController) }
        composable(
            route = Screen.VetDetail.route,
            arguments = listOf(navArgument("vetId") { type = NavType.StringType })
        ) { backStackEntry ->
            VetDetailScreen(navController, backStackEntry.arguments?.getString("vetId"))
        }
        composable(
            route = Screen.AppointmentBooking.route,
            arguments = listOf(navArgument("vetId") { type = NavType.StringType })
        ) { backStackEntry ->
            AppointmentBookingScreen(navController, backStackEntry.arguments?.getString("vetId"))
        }
        composable(route = Screen.MyAppointments.route) { MyAppointmentsScreen(navController) }
        composable(route = Screen.Telemedicine.route) { TelemedicineScreen(navController) }
        composable(route = Screen.VideoCall.route) { VideoCallScreen(navController) }
        composable(route = Screen.QrScanner.route) { QrScannerScreen(navController) }
        
        // Community Routes
        composable(route = Screen.Community.route) { CommunityFeedScreen(navController) }
        composable(route = Screen.LostFound.route) { LostFoundScreen(navController) }
        composable(route = Screen.ReportPet.route) { ReportPetScreen(navController) }
        composable(route = Screen.Chat.route) { ChatScreen(navController) }
        composable(route = Screen.RescueForum.route) { RescueForumScreen(navController) }
        
        // Adoption Routes
        composable(route = Screen.Adoption.route) { AdoptionListScreen(navController) }
        composable(
            route = Screen.AdoptionDetail.route,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            AdoptionDetailScreen(navController, backStackEntry.arguments?.getString("petId"))
        }
        composable(
            route = Screen.AdoptionApplication.route,
            arguments = listOf(navArgument("petName") { type = NavType.StringType })
        ) { backStackEntry ->
            AdoptionApplicationScreen(navController, backStackEntry.arguments?.getString("petName") ?: "")
        }
        composable(route = Screen.SuccessStories.route) { SuccessStoriesScreen(navController) }

        // Dashboard Routes
        composable(route = Screen.VetDashboard.route) { VetDashboardScreen(navController) }
        composable(route = Screen.ShelterDashboard.route) { ShelterDashboardScreen(navController) }
        composable(route = Screen.StoreDashboard.route) { StoreDashboardScreen(navController) }
        composable(
            route = Screen.AdoptionRequestDetail.route,
            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
        ) { backStackEntry ->
            AdoptionRequestDetailScreen(navController, backStackEntry.arguments?.getString("requestId"))
        }

        // Other Routes
        composable(route = Screen.AiAssistant.route) { AiAssistantScreen(navController) }
        composable(route = Screen.EmergencySos.route) { EmergencySosScreen(navController) }
        composable(route = Screen.Education.route) { EducationScreen(navController) }
        composable(route = Screen.Marketplace.route) { MarketplaceScreen(navController) }
        composable(route = Screen.UserProfile.route) { UserProfileScreen(navController) }
        composable(route = Screen.DiscoveryMap.route) { DiscoveryMapScreen(navController) }
    }
}
