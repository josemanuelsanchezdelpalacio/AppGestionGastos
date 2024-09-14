package com.dam2jms.appgestiongastos.states

data class UiState(

    //para loginscreen
    var sesionIniciada: Boolean = false,
    val email: String = "",
    val password: String = "",
    var visibilidadPassword: Boolean = false,

    //para homescreen
    val ingresos: List<Transaccion> = emptyList(),
    val gastos: List<Transaccion> = emptyList(),
    val ingresosDiarios: Long = 0L,
    val ingresosMensuales: Long = 0L,
    val gastosDiarios: Long = 0L,
    val gastosMensuales: Long = 0L,
    val ahorrosDiarios: Long = 0L,
    val ahorrosMensuales: Long = 0L,
    val monedaActual : String = "EUR",

    //para transaccionScreen
    var id: String = "",
    var cantidad: String = "",
    var categoria: String = "",
    var fecha: String = "",
    var tipo: String = ""

)


