package com.dam2jms.appgestiongastos.states

data class UiState(

    //para loginscreen
    var sesionIniciada: Boolean = false,
    val email: String = "",
    val password: String = "",
    var visibilidadPassword: Boolean = false


)
