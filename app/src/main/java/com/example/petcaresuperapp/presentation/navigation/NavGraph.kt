package com.example.petcaresuperapp.presentation.navigation

import androidx.compose.animation.*
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
import com.example.petcaresuperapp.presentation.ui.AdoptPetScreen
import com.example.petcaresuperapp.presentation.ui.PetDetailsScreen
import com.example.petcaresuperapp.presentation.ui.AdoptionRequestScreen
import com.example.petcaresuperapp.presentation.ui.MyAdoptionRequestsScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    
    // Pet Module
    object PetProfile : Screen("pet_profile")
    object HealthCard : Screen("health_card")
    object Notifications : Screen("notifications")
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
    object CreatePost : Screen("create_post")
    object SocialComments : Screen("comment_screen/{postId}")
    
    // Adoption
    object Adoption : Screen("adoption")
    object AdoptionDetail : Screen("adoption_detail/{petId}")
    object AdoptionApplication : Screen("adoption_application/{petName}")
    object SuccessStories : Screen("success_stories")
    
    // New Shelter Integration Routes
    object AdoptPetsList : Screen("adopt_pets")
    object PetDetailsNew : Screen("pet_details/{petId}")
    object AdoptionRequestNew : Screen("adoption_request/{petId}/{petName}")
    object MyRequests : Screen("my_requests")
    
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
    object ProductDetail : Screen("product_detail/{productId}")
    object Cart : Screen("cart")
    object MyOrders : Screen("my_orders")
    
    // Profile
    object UserProfile : Screen("user_profile")
    object Settings : Screen("settings")
    
    // Discovery
    object DiscoveryMap : Screen("discovery_map")
}

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class DrawerItem(
    val title: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut()
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut()
        }
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
            MainScaffold(navController = navController) { padding ->
                HomeScreen(navController = navController, paddingValues = padding)
            }
        }
        
        // Pet Routes
        composable(route = Screen.PetProfile.route) { 
            MainScaffold(navController = navController) { padding ->
                PetProfileScreen(navController) 
            }
        }
        composable(route = Screen.AddPet.route) { 
            MainScaffold(navController = navController, showTopBar = false) { padding ->
                AddPetScreen(navController) 
            }
        }
        composable(route = Screen.HealthCard.route) { 
            MainScaffold(navController = navController) { padding ->
                HealthCardScreen(navController) 
            }
        }
        composable(route = Screen.Notifications.route) { 
            MainScaffold(navController = navController, showTopBar = false) { padding ->
                NotificationScreen(navController) 
            }
        }
        composable(route = Screen.ActivityTracker.route) { 
            MainScaffold(navController = navController, showTopBar = false) { padding ->
                ActivityTrackerScreen(navController) 
            }
        }
        composable(route = Screen.GpsTracking.route) { 
            MainScaffold(navController = navController) { padding ->
                GpsTrackingScreen(navController) 
            }
        }
        composable(route = Screen.BlockchainIdentity.route) { 
            MainScaffold(navController = navController) { padding ->
                BlockchainIdentityScreen(navController) 
            }
        }
        composable(route = Screen.HealthAnalytics.route) { 
            MainScaffold(navController = navController) { padding ->
                HealthAnalyticsScreen(navController) 
            }
        }
        
        // Vet Routes
        composable(route = Screen.VetSearch.route) { 
            MainScaffold(navController = navController) { padding ->
                VetSearchScreen(navController) 
            }
        }
        composable(
            route = Screen.VetDetail.route,
            arguments = listOf(navArgument("vetId") { type = NavType.StringType })
        ) { backStackEntry ->
            MainScaffold(navController = navController) { padding ->
                VetDetailScreen(navController, backStackEntry.arguments?.getString("vetId"))
            }
        }
        composable(
            route = Screen.AppointmentBooking.route,
            arguments = listOf(navArgument("vetId") { type = NavType.StringType })
        ) { backStackEntry ->
            MainScaffold(navController = navController) { padding ->
                AppointmentBookingScreen(navController, backStackEntry.arguments?.getString("vetId"))
            }
        }
        composable(route = Screen.MyAppointments.route) { 
            MainScaffold(navController = navController) { padding ->
                MyAppointmentsScreen(navController) 
            }
        }
        composable(route = Screen.Telemedicine.route) { 
            MainScaffold(navController = navController) { padding ->
                TelemedicineScreen(navController) 
            }
        }
        composable(route = Screen.VideoCall.route) { 
            MainScaffold(navController = navController) { padding ->
                VideoCallScreen(navController) 
            }
        }
        composable(route = Screen.QrScanner.route) { 
            MainScaffold(navController = navController) { padding ->
                QrScannerScreen(navController) 
            }
        }
        
        // Community Routes
        composable(route = Screen.Community.route) { 
            MainScaffold(navController = navController) { padding ->
                CommunityFeedScreen(navController, padding) 
            }
        }
        composable(route = Screen.LostFound.route) { 
            MainScaffold(navController = navController) { padding ->
                LostFoundScreen(navController) 
            }
        }
        composable(route = Screen.ReportPet.route) { 
            MainScaffold(navController = navController) { padding ->
                ReportPetScreen(navController) 
            }
        }
        composable(route = Screen.Chat.route) { 
            MainScaffold(navController = navController) { padding ->
                ChatScreen(navController) 
            }
        }
        composable(route = Screen.RescueForum.route) { 
            MainScaffold(navController = navController) { padding ->
                RescueForumScreen(navController) 
            }
        }
        composable(route = Screen.CreatePost.route) {
            MainScaffold(navController = navController) { padding ->
                CreatePostScreen(navController)
            }
        }
        composable(
            route = Screen.SocialComments.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
            MainScaffold(navController = navController) { padding ->
                CommentScreen(navController, postId, padding)
            }
        }
        
        // Adoption Routes
        composable(route = Screen.Adoption.route) { 
            MainScaffold(navController = navController) { padding ->
                AdoptionListScreen(navController) 
            }
        }
        composable(
            route = Screen.AdoptionDetail.route,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            MainScaffold(navController = navController) { padding ->
                AdoptionDetailScreen(navController, backStackEntry.arguments?.getString("petId"))
            }
        }
        composable(
            route = Screen.AdoptionApplication.route,
            arguments = listOf(navArgument("petName") { type = NavType.StringType })
        ) { backStackEntry ->
            MainScaffold(navController = navController) { padding ->
                AdoptionApplicationScreen(navController, backStackEntry.arguments?.getString("petName") ?: "")
            }
        }
        composable(route = Screen.SuccessStories.route) { 
            MainScaffold(navController = navController) { padding ->
                SuccessStoriesScreen(navController) 
            }
        }
        
        // New Shelter Integration Routes
        composable(route = Screen.AdoptPetsList.route) {
            MainScaffold(navController = navController) { padding ->
                AdoptPetScreen(
                    onPetClick = { petId ->
                        navController.navigate("pet_details/$petId")
                    }
                )
            }
        }
        composable(
            route = Screen.PetDetailsNew.route,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId") ?: return@composable
            PetDetailsScreen(
                petId = petId,
                onBackClick = { navController.popBackStack() },
                onAdoptClick = { id, name ->
                    navController.navigate("adoption_request/$id/$name")
                }
            )
        }
        composable(
            route = Screen.AdoptionRequestNew.route,
            arguments = listOf(
                navArgument("petId") { type = NavType.StringType },
                navArgument("petName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId") ?: return@composable
            val petName = backStackEntry.arguments?.getString("petName") ?: return@composable
            AdoptionRequestScreen(
                petId = petId,
                petName = petName,
                onBackClick = { navController.popBackStack() },
                onSubmitSuccess = { navController.navigate(Screen.MyRequests.route) }
            )
        }
        composable(route = Screen.MyRequests.route) {
            MyAdoptionRequestsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Dashboard Routes
        composable(route = Screen.VetDashboard.route) { 
            MainScaffold(navController = navController) { padding ->
                VetDashboardScreen(navController) 
            }
        }
        composable(route = Screen.ShelterDashboard.route) { 
            MainScaffold(navController = navController) { padding ->
                ShelterDashboardScreen(navController) 
            }
        }
        composable(route = Screen.StoreDashboard.route) { 
            MainScaffold(navController = navController) { padding ->
                StoreDashboardScreen(navController) 
            }
        }
        composable(
            route = Screen.AdoptionRequestDetail.route,
            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
        ) { backStackEntry ->
            MainScaffold(navController = navController) { padding ->
                AdoptionRequestDetailScreen(navController, backStackEntry.arguments?.getString("requestId"))
            }
        }

        // Other Routes
        composable(route = Screen.AiAssistant.route) { 
            MainScaffold(navController = navController, showTopBar = false) { padding ->
                AiAssistantScreen(navController) 
            }
        }
        composable(route = Screen.EmergencySos.route) { 
            MainScaffold(navController = navController) { padding ->
                EmergencySosScreen(navController) 
            }
        }
        composable(route = Screen.Education.route) { 
            MainScaffold(navController = navController) { padding ->
                EducationScreen(navController) 
            }
        }
        composable(route = Screen.Marketplace.route) { 
            MainScaffold(navController = navController, showTopBar = false) { padding ->
                MarketplaceScreen(navController) 
            }
        }
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            MainScaffold(navController = navController, showTopBar = false) { padding ->
                ProductDetailScreen(
                    navController = navController,
                    productId = backStackEntry.arguments?.getString("productId") ?: ""
                )
            }
        }
        composable(route = Screen.Cart.route) { 
            MainScaffold(navController = navController, showTopBar = false) { padding ->
                CartScreen(navController) 
            }
        }
        composable(route = Screen.MyOrders.route) { 
            MainScaffold(navController = navController, showTopBar = false) { padding ->
                MyOrdersScreen(navController) 
            }
        }
        composable(route = Screen.UserProfile.route) { 
            MainScaffold(navController = navController) { padding ->
                UserProfileScreen(navController) 
            }
        }
        composable(route = Screen.Settings.route) { 
            MainScaffold(navController = navController) { padding ->
                SettingsScreen(navController) 
            }
        }
        composable(route = Screen.DiscoveryMap.route) { 
            MainScaffold(navController = navController) { padding ->
                DiscoveryMapScreen(navController) 
            }
        }
    }
}
