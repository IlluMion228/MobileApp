package com.example.noteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.noteapp.uiView.AddEditScreen
import com.example.noteapp.uiView.HomeScreen
import com.example.noteapp.ui.theme.NoteAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteAppTheme {
                NoteAppNavigation()
            }
        }
    }
}

@Composable
fun NoteAppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        // 1. Екран "Списък"
        composable("home") {
            HomeScreen(
                onNavigateToAdd = {
                    navController.navigate("add_edit/-1")
                },
                onNavigateToEdit = { id ->
                    navController.navigate("add_edit/$id")
                }
            )
        }

        // 2. Екран "Редакция"
        composable(
            route = "add_edit/{noteId}",
            arguments = listOf(
                navArgument("noteId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1

            AddEditScreen(
                noteId = noteId,
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}
