package com.dam2jms.appgestiongastos.navigation

sealed class AppScreen(val route: String){

    object LoginScreen: AppScreen("login_screen")
    object RegisterScreen: AppScreen("register_screen")

    object HomeScreen: AppScreen("home_screen")
    object TransactionScreen: AppScreen("transaction_screen/{date}"){
        fun createRoute(date: String) = "transaction_screen/$date"
    }
    object AddTransactionScreen: AppScreen("addTransaction_screen")
    object HistoryScreen: AppScreen("history_screen")
    object EditTransactionScreen: AppScreen("editTransaction_screen/{transactionId}"){
        fun createRoute(transactionId: String) = "editTransaction_screen/$transactionId"
    }
}