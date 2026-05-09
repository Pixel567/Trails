package com.poznantrails

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.window.layout.WindowMetricsCalculator
import com.poznantrails.data.CYCLING_TRAILS
import com.poznantrails.data.HIKING_TRAILS
import com.poznantrails.data.Trail
import com.poznantrails.ui.components.StopwatchFab
import com.poznantrails.ui.screens.AboutScreen
import com.poznantrails.ui.screens.HomeScreen
import com.poznantrails.ui.screens.SavedTimesScreen
import com.poznantrails.ui.screens.TrailDetailContent
import com.poznantrails.ui.screens.TrailDetailScreen
import com.poznantrails.ui.theme.LocalAppPalette
import com.poznantrails.ui.theme.PoznanTrailsTheme
import com.poznantrails.viewmodel.TrailsViewModel

private object Routes {
    const val TRAILS_GRAPH = "trails_graph"
    const val HOME         = "home"
    const val TRAIL_DETAIL = "trail/{trailId}"
    const val SAVED        = "saved"
    const val ABOUT        = "about"

    fun trailDetail(id: String) = "trail/$id"
}

private data class BottomTab(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val bottomTabs = listOf(
    BottomTab(Routes.TRAILS_GRAPH, "Trasy",       Icons.AutoMirrored.Filled.List),
    BottomTab(Routes.SAVED,        "Zapisane",    Icons.Default.Schedule),
    BottomTab(Routes.ABOUT,        "O nas",       Icons.Default.Info)
)

class MainActivity : ComponentActivity() {

    private val viewModel: TrailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                val slideUp = ObjectAnimator.ofFloat(
                    splashScreenView,
                    View.TRANSLATION_Y,
                    0f,
                    -splashScreenView.height.toFloat()
                )
                slideUp.interpolator = AnticipateInterpolator()
                slideUp.duration = 500L

                slideUp.doOnEnd { splashScreenView.remove() }
                slideUp.start()
            }
        }

        enableEdgeToEdge()

        setContent {
            PoznanTrailsTheme {
                val palette = LocalAppPalette.current
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                val windowMetrics = WindowMetricsCalculator.getOrCreate()
                    .computeCurrentWindowMetrics(this)
                val widthDp = windowMetrics.bounds.width() / resources.displayMetrics.density
                val isTablet = widthDp >= 600

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val viewingTrail: Trail? = remember(currentRoute, navBackStackEntry, uiState.selectedTrail, isTablet) {
                    when {
                        isTablet && currentRoute == Routes.HOME -> uiState.selectedTrail
                        currentRoute == Routes.TRAIL_DETAIL -> {
                            val tid = navBackStackEntry?.arguments?.getString("trailId")
                            (HIKING_TRAILS + CYCLING_TRAILS).find { it.id == tid }
                        }
                        else -> null
                    }
                }

                Scaffold(
                    containerColor = palette.background,
                    bottomBar = {
                        NavigationBar(
                            containerColor = palette.surface,
                            contentColor   = palette.onSurface
                        ) {
                            bottomTabs.forEach { tab ->
                                val selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == tab.route } == true
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = {
                                        navController.navigate(tab.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState    = true
                                        }
                                    },
                                    icon  = { Icon(tab.icon, contentDescription = tab.label) },
                                    label = { Text(tab.label) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor   = palette.hikingPrimary,
                                        selectedTextColor   = palette.hikingPrimary,
                                        indicatorColor      = palette.hikingSoft,
                                        unselectedIconColor = palette.onSurfaceVariant,
                                        unselectedTextColor = palette.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    },
                    floatingActionButton = {
                        StopwatchFab(
                            active       = uiState.stopwatch,
                            viewingTrail = viewingTrail,
                            onStart      = viewModel::startStopwatch,
                            onToggle     = viewModel::toggleStopwatch,
                            onFinish     = viewModel::finishStopwatch
                        )
                    },
                    floatingActionButtonPosition = FabPosition.End
                ) { innerPadding ->
                    NavHost(
                        navController    = navController,
                        startDestination = Routes.TRAILS_GRAPH,
                        modifier         = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        navigation(
                            route = Routes.TRAILS_GRAPH,
                            startDestination = Routes.HOME
                        ) {
                            composable(Routes.HOME) {
                                if (isTablet) {
                                    Row(modifier = Modifier.fillMaxSize()) {
                                        Box(modifier = Modifier.width(400.dp).fillMaxHeight()) {
                                            HomeScreen(
                                                uiState      = uiState,
                                                onTypeToggle = viewModel::toggleType,
                                                onTrailClick = viewModel::selectTrail
                                            )
                                        }
                                        VerticalDivider(
                                            modifier = Modifier.fillMaxHeight().width(1.dp),
                                            color = palette.outline
                                        )
                                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                            val selected = uiState.selectedTrail
                                            if (selected != null) {
                                                TrailDetailContent(
                                                    trail            = selected,
                                                    descriptionState = uiState.descriptions[selected.id],
                                                    onBack           = null,
                                                    onRetry          = { viewModel.retryDescription(selected) },
                                                    isTablet         = true,
                                                    modifier         = Modifier.fillMaxSize()
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        "Wybierz trasę z listy",
                                                        color = palette.onSurfaceVariant,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    HomeScreen(
                                        uiState      = uiState,
                                        onTypeToggle = viewModel::toggleType,
                                        onTrailClick = { trail ->
                                            viewModel.selectTrail(trail)
                                            navController.navigate(Routes.trailDetail(trail.id))
                                        }
                                    )
                                }
                            }

                            composable(
                                route = Routes.TRAIL_DETAIL,
                                arguments = listOf(navArgument("trailId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val trailId = backStackEntry.arguments?.getString("trailId")
                                    ?: return@composable
                                val allTrails = HIKING_TRAILS + CYCLING_TRAILS
                                val trail = allTrails.find { it.id == trailId } ?: return@composable

                                TrailDetailScreen(
                                    trail            = trail,
                                    descriptionState = uiState.descriptions[trail.id],
                                    onBack           = { navController.popBackStack() },
                                    onRetry          = { viewModel.retryDescription(trail) }
                                )
                            }
                        }

                        composable(Routes.SAVED) {
                            SavedTimesScreen(
                                savedTimes = uiState.savedTimes,
                                onDelete   = viewModel::deleteSavedTime
                            )
                        }

                        composable(Routes.ABOUT) {
                            AboutScreen()
                        }
                    }
                }
            }
        }
    }
}
