package com.dam2jms.appgestiongastos.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dam2jms.appgestiongastos.models.HomeViewModel
import com.dam2jms.appgestiongastos.models.LoginViewModel
import com.dam2jms.appgestiongastos.screens.HomeScreen
import com.dam2jms.appgestiongastos.screens.LoginScreen

@Composable
fun AppNavigation(){

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreen.LoginScreen.route) {
        composable(AppScreen.LoginScreen.route){
            LoginScreen(navController, mvvm = LoginViewModel())
        }
        composable(AppScreen.HomeScreen.route){
            HomeScreen(navController, mvvm = HomeViewModel())
        }


    }
}