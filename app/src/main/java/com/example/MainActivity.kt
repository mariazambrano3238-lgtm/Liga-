package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.AppDatabase
import com.example.data.TournamentRepository
import com.example.ui.DashboardScreen
import com.example.ui.TournamentViewModel
import com.example.ui.TournamentViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room Database and Repository
        val database = AppDatabase.getInstance(this)
        val repository = TournamentRepository(database)

        // VM Creation using our custom Factory
        val viewModel: TournamentViewModel by viewModels {
            TournamentViewModelFactory(repository)
        }

        setContent {
            MyApplicationTheme {
                DashboardScreen(viewModel = viewModel)
            }
        }
    }
}
