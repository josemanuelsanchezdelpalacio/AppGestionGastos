package com.dam2jms.appgestiongastos.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dam2jms.appgestiongastos.data.CategoriaAPI
import com.dam2jms.appgestiongastos.models.CurrencyViewModel
import com.dam2jms.appgestiongastos.models.HomeViewModel
import com.dam2jms.appgestiongastos.models.LoginViewModel
import com.dam2jms.appgestiongastos.models.RegisterViewModel
import com.dam2jms.appgestiongastos.models.TransactionViewModel
import com.dam2jms.appgestiongastos.screens.AddTransactionScreen
import com.dam2jms.appgestiongastos.screens.HistoryScreen
import com.dam2jms.appgestiongastos.screens.HomeScreen
import com.dam2jms.appgestiongastos.screens.LoginScreen
import com.dam2jms.appgestiongastos.screens.RegisterScreen
import com.dam2jms.appgestiongastos.screens.TransactionScreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(){

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreen.LoginScreen.route) {
        composable(AppScreen.LoginScreen.route){
            LoginScreen(navController, mvvm = LoginViewModel())
        }
        composable(AppScreen.RegisterScreen.route){
            RegisterScreen(navController, mvvm = RegisterViewModel())
        }

        composable(AppScreen.HomeScreen.route){
            HomeScreen(navController, mvvm = HomeViewModel(), currencyViewModel = CurrencyViewModel())
        }
        composable(
            route = AppScreen.TransactionScreen.route,
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedDate = backStackEntry.arguments?.getString("date") ?: LocalDate.now().format(
                DateTimeFormatter.ISO_DATE)
            TransactionScreen(navController, mvvm = TransactionViewModel(), seleccionarFecha = selectedDate)
        }
        composable(AppScreen.AddTransactionScreen.route){
            val categoryApi = CategoriaAPI
            AddTransactionScreen(navController, mvvm = TransactionViewModel())
        }
        composable(AppScreen.HistoryScreen.route){
            HistoryScreen(navController, mvvm = TransactionViewModel())
        }
    }
}