package com.example.myvestidorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myvestidorapp.ui.screens.EditorScreen
import com.example.myvestidorapp.ui.screens.ProfileScreen
import com.example.myvestidorapp.ui.theme.MyVestidorAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyVestidorAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: MainViewModel = viewModel()
                    
                    NavHost(
                        navController = navController,
                        startDestination = "profile"
                    ) {
                        composable("profile") {
                            ProfileScreen(
                                viewModel = viewModel,
                                onContinue = {
                                    navController.navigate("editor") {
                                        popUpTo("profile") { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        composable("editor") {
                            EditorScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}