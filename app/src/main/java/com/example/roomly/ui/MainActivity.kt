package com.example.roomly.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.roomly.data.DemoFurnitureRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { Surface { AppNavigation() } } }
    }
}

@Composable
private fun AppNavigation(vm: CatalogViewModel = viewModel()) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "catalog") {
        composable("catalog") {
            CatalogScreen(vm.products) { nav.navigate("ar/${it.id}") }
        }
        composable("ar/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) { entry ->
            val item = DemoFurnitureRepository.byId(entry.arguments?.getString("id").orEmpty())
            if (item == null) CatalogScreen(vm.products) { nav.navigate("ar/${it.id}") }
            else ArViewerScreen(item) { nav.popBackStack() }
        }
    }
}
