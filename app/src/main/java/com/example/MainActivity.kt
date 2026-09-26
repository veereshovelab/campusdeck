package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.CampusViewModel
import com.example.ui.components.glassEffect
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val viewModel: CampusViewModel = viewModel()
        MainAppContent(viewModel = viewModel)
      }
    }
  }
}

enum class CampusTab(val route: String) {
  FEED("feed"),
  ASSIGNMENTS("assignments"),
  MARKETPLACE("marketplace"),
  GIGS("gigs"),
  PROFILE("profile"),
  EDIT_PROFILE("edit_profile"),
  COLLEGE_SEARCH("college_search"),
  COLLEGE_HUB("college_hub/{collegeName}"),
  MESSAGES("messages"),
  CHAT("chat/{chatId}")
}

@Composable
fun MainAppContent(viewModel: CampusViewModel) {
  val currentUser by viewModel.currentUser.collectAsState()
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  if (currentUser == null) {
    LoginScreen(viewModel = viewModel)
  } else {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      containerColor = BentoBackground,
      bottomBar = {
        val mainTabs = listOf(CampusTab.FEED, CampusTab.ASSIGNMENTS, CampusTab.MESSAGES, CampusTab.MARKETPLACE, CampusTab.GIGS, CampusTab.PROFILE)
        val showBottomBar = currentRoute in mainTabs.map { it.route }
        
        if (showBottomBar) {
          NavigationBar(
            containerColor = Color.Transparent,
            contentColor = BentoTextMain,
            modifier = Modifier
              .padding(horizontal = 16.dp, vertical = 8.dp)
              .glassEffect(cornerRadius = 32.dp)
          ) {
            mainTabs.forEach { tab ->
              // Only show Messages for Students
              if (tab == CampusTab.MESSAGES && currentUser?.role != "Student") return@forEach

              NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = {
                  if (tab.route == CampusTab.FEED.route) {
                    navController.popBackStack(CampusTab.FEED.route, inclusive = false)
                  } else if (currentRoute != tab.route) {
                    navController.navigate(tab.route) {
                      popUpTo(navController.graph.startDestinationId) { saveState = true }
                      launchSingleTop = true
                      restoreState = true
                    }
                  }
                },
                icon = {
                  val icon = when (tab) {
                    CampusTab.FEED -> Icons.Default.Campaign
                    CampusTab.ASSIGNMENTS -> Icons.AutoMirrored.Filled.Assignment
                    CampusTab.MARKETPLACE -> Icons.Default.MenuBook
                    CampusTab.GIGS -> Icons.Default.Handshake
                    CampusTab.PROFILE -> Icons.Default.Person
                    CampusTab.MESSAGES -> Icons.Default.Email // Placeholder icon
                    else -> Icons.Default.Campaign
                  }
                  Icon(icon, contentDescription = tab.name)
                },
                label = { Text(tab.name.lowercase().capitalize(), fontWeight = if (currentRoute == tab.route) FontWeight.Bold else FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = BentoLilacContent,
                  selectedTextColor = BentoLilacContent,
                  unselectedIconColor = BentoTextSecondary,
                  unselectedTextColor = BentoTextSecondary,
                  indicatorColor = BentoLilacContainer
                )
              )
            }
          }
        }
      }
    ) { innerPadding ->
      NavHost(
        navController = navController,
        startDestination = CampusTab.FEED.route,
        modifier = Modifier.padding(innerPadding),
        enterTransition = { fadeIn(animationSpec = tween(350)) + slideInHorizontally(initialOffsetX = { it / 3 }) + scaleIn(initialScale = 0.96f) },
        exitTransition = { fadeOut(animationSpec = tween(350)) + slideOutHorizontally(targetOffsetX = { -it / 3 }) + scaleOut(targetScale = 0.96f) },
        popEnterTransition = { fadeIn(animationSpec = tween(350)) + slideInHorizontally(initialOffsetX = { -it / 3 }) + scaleIn(initialScale = 0.96f) },
        popExitTransition = { fadeOut(animationSpec = tween(350)) + slideOutHorizontally(targetOffsetX = { it / 3 }) + scaleOut(targetScale = 0.96f) }
      ) {
        composable(CampusTab.FEED.route) {
          HomeScreen(
            viewModel = viewModel,
            onProfileClick = { navController.navigate(CampusTab.PROFILE.route) },
            onStartChat = { email ->
              viewModel.startNewChat(email) { success, msg, chatId ->
                if (success && chatId != null) {
                  navController.navigate("chat/$chatId")
                }
              }
            }
          )
        }
        composable(CampusTab.ASSIGNMENTS.route) {
          AssignmentScreen(viewModel = viewModel)
        }
        composable(CampusTab.MARKETPLACE.route) {
          MarketplaceScreen(viewModel = viewModel)
        }
        composable(CampusTab.GIGS.route) {
          GigScreen(
            viewModel = viewModel,
            onStartChat = { email ->
              viewModel.startNewChat(email) { success, msg, chatId ->
                if (success && chatId != null) {
                  navController.navigate("chat/$chatId")
                }
              }
            }
          )
        }
        composable(CampusTab.PROFILE.route) {
          ProfileScreen(viewModel = viewModel, onEditClick = { navController.navigate(CampusTab.EDIT_PROFILE.route) })
        }
        composable(CampusTab.EDIT_PROFILE.route) {
          EditProfileScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable(CampusTab.COLLEGE_SEARCH.route) {
          CollegeSearchScreen(
            viewModel = viewModel,
            onCollegeSelect = { name ->
              navController.navigate(CampusTab.COLLEGE_HUB.route.replace("{collegeName}", name))
            }
          )
        }
        composable(CampusTab.COLLEGE_HUB.route, arguments = listOf(navArgument("collegeName") { type = NavType.StringType })) { backStackEntry ->
          val collegeName = backStackEntry.arguments?.getString("collegeName") ?: ""
          CollegeHubScreen(viewModel = viewModel, collegeName = collegeName, onBack = { navController.popBackStack() })
        }
        composable(CampusTab.MESSAGES.route) {
          MessageInboxScreen(viewModel = viewModel, onChatClick = { chatId ->
            navController.navigate("chat/$chatId")
          })
        }
        composable(CampusTab.CHAT.route, arguments = listOf(navArgument("chatId") { type = NavType.IntType })) { backStackEntry ->
          val chatId = backStackEntry.arguments?.getInt("chatId") ?: -1
          ChatScreen(viewModel = viewModel, chatId = chatId, onBack = { navController.popBackStack() })
        }
      }
    }
  }
}

private fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
