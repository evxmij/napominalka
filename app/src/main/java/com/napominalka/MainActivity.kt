package com.napominalka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.napominalka.theme.NapominalkaTheme
import com.napominalka.ui.diary.DiaryScreen
import com.napominalka.ui.home.HomeScreen
import com.napominalka.ui.plan.PlanScreen
import com.napominalka.ui.session.SessionScreen
import com.napominalka.ui.settings.SettingsScreen
import com.napominalka.ui.stats.StatsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startRoute = if (intent?.getBooleanExtra(EXTRA_QUICK_START, false) == true) "session" else "home"
        setContent { NapominalkaApp(startRoute = startRoute) }
    }

    companion object {
        const val EXTRA_QUICK_START = "quick_start"
    }
}

@Composable
fun NapominalkaApp(
    startRoute: String = "home",
    modifier: Modifier = Modifier,
    vm: MainViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val routes = listOf("home", "session", "plan", "stats", "diary", "settings")
    val state by vm.uiState.collectAsState()
    val activeCategory by vm.activeCategory.collectAsState()
    val activeNote by vm.activeNote.collectAsState()
    val startedAt by vm.activeStartedAtMs.collectAsState()

    NapominalkaTheme {
        Scaffold(
            modifier = modifier,
            bottomBar = {
                val entry by navController.currentBackStackEntryAsState()
                NavigationBar {
                    routes.forEach { route ->
                        NavigationBarItem(
                            selected = entry?.destination?.route == route,
                            onClick = { navController.navigate(route) },
                            label = { Text(route) },
                            icon = {},
                        )
                    }
                }
            },
        ) { padding ->
            NavHost(navController = navController, startDestination = startRoute) {
                composable("home") {
                    HomeScreen(
                        paddingValues = padding,
                        state = state,
                        onStartSession = vm::startSession,
                        onStopSession = vm::stopSession,
                    )
                }
                composable("session") {
                    SessionScreen(
                        paddingValues = padding,
                        category = activeCategory,
                        note = activeNote,
                        startedAtMs = startedAt,
                        onStart = vm::startSession,
                        onStop = vm::stopSession,
                        onRotateCategory = vm::rotateCategory,
                        onChangeNote = vm::setActiveNote,
                    )
                }
                composable("plan") {
                    PlanScreen(
                        paddingValues = padding,
                        state = state,
                        onAddTopic = vm::addTopic,
                        onAddTask = vm::addTask,
                        onToggleTask = vm::toggleTask,
                    )
                }
                composable("stats") { StatsScreen(padding, state) }
                composable("diary") {
                    DiaryScreen(
                        paddingValues = padding,
                        state = state,
                        onSaveReview = { score, did, blockers, plan ->
                            vm.saveTodayReview(score, did, blockers, plan, state.todayMinutes * 60L)
                        },
                    )
                }
                composable("settings") { SettingsScreen(padding) }
            }
        }
    }
}
