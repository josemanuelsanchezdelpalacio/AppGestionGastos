package com.dam2jms.appgestiongastos.models

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam2jms.appgestiongastos.data.CurrencyConverter
import com.dam2jms.appgestiongastos.states.Transaccion
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.ui.theme.Blanco
import com.dam2jms.appgestiongastos.ui.theme.NaranjaClaro
import com.dam2jms.appgestiongastos.ui.theme.NaranjaOscuro
import com.dam2jms.appgestiongastos.ui.theme.RojoClaro
import com.dam2jms.appgestiongastos.ui.theme.VerdeClaro
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel: ViewModel() {

    //estado que se actualizara con datos de ingresos y gastos
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()


    private val db = Firebase.firestore
    private val currencyViewModel = CurrencyViewModel()

    //inicializa la lectura de transacciones
    init {
        leerTransacciones()
    }

    /** calculo y actualizo la rueda de balance a partir de los ingresos y gastos del usuario
     *
     * calculo el total de ingresos y gastos y luego calculo los ahorros con la diferencia entre esos datos
     * actualizo el estado del UI con los valores de ingresos mensuales, gastos mensuales y ahorros
     * @param ahorrosDiarios filtra y suma los ingresos y gastos del dia de hoy
     * @param primerDiaDelMes calcula el primer dia del mes para filtrar ingresos y gastos
     * @param ahorrosMensuales filtra y suma los ingresos y gastos desde el primer dia
     *
     * */
    private fun actualizacionBalance() {

        val hoy = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val ingresosDiarios = _uiState.value.ingresos
            .filter { LocalDate.parse(it.fecha, formatter) == hoy }
            .sumOf { it.cantidad }

        val gastosDiarios = _uiState.value.gastos
            .filter { LocalDate.parse(it.fecha, formatter) == hoy }
            .sumOf { it.cantidad }

        val ahorrosDiarios = ingresosDiarios - gastosDiarios

        val primerDiaDelMes = hoy.withDayOfMonth(1)

        val ingresosMensuales = _uiState.value.ingresos
            .filter { LocalDate.parse(it.fecha, formatter) >= primerDiaDelMes }
            .sumOf { it.cantidad }

        val gastosMensuales = _uiState.value.gastos
            .filter { LocalDate.parse(it.fecha, formatter) >= primerDiaDelMes }
            .sumOf { it.cantidad }

        val ahorrosMensuales = ingresosMensuales - gastosMensuales

        //actualiza el estado de la UI con los nuevos valores
        _uiState.update { currentUiState ->
            currentUiState.copy(
                ingresosDiarios = ingresosDiarios.toLong(),
                gastosDiarios = gastosDiarios.toLong(),
                ingresosMensuales = ingresosMensuales.toLong(),
                gastosMensuales = gastosMensuales.toLong(),
                ahorrosDiarios = ahorrosDiarios.toLong(),
                ahorrosMensuales = ahorrosMensuales.toLong()
            )
        }
    }

    /**muestra las colecciones de ingresos y gastos en firesotore
     *
     * obtengo el ID del usuario actual y leo sus datos en las colecciones "Ingresos" y "gastos"
     * actualizo el UI cada vez que hay un cambio en estas colecciones
     * @param IDUsuario obtiene el ID del usuario actual
     * */
    private fun leerTransacciones(){

        val IDusuario = Firebase.auth.currentUser?.uid?: return

        //lee cambios en la coleccion de ingresos
        db.collection("users")
            .document(IDusuario)
            .collection("ingresos")
            .addSnapshotListener{ snapshots, e ->
                if(e != null){
                    return@addSnapshotListener
                }

                //mapea los documentos a objetos Transaccion
                val ingresos = snapshots?.documents?.mapNotNull {
                    it.toObject(Transaccion::class.java)
                }?: emptyList()

                //actualiza el estado de la UI con los ingresos leidos
                _uiState.update { it.copy(ingresos = ingresos) }
                //recalcula el balance
                actualizacionBalance()
            }


        //hace lo mismo que lo anterior pero leyendo gastos
        db.collection("users")
            .document(IDusuario)
            .collection("gastos")
            .addSnapshotListener{ snapshots, e ->
                if(e != null){
                    return@addSnapshotListener
                }

                val gastos = snapshots?.documents?.mapNotNull {
                    it.toObject(Transaccion::class.java)
                }?: emptyList()
                _uiState.update { it.copy(gastos = gastos) }
                actualizacionBalance()
            }
    }

    /** metodo que actualiza la moneda en la UI a una nueva moneda
     * @param nuevaMoneda a la que se deben convertir los valores
     * */
    fun actualizarMoneda(nuevaMoneda: String) {
        viewModelScope.launch {
            val monedaActual = _uiState.value.monedaActual

            //obtengo la tasa de cambio de la moneda actual a la nueva moneda
            val tasaCambio = try {
                currencyViewModel.obtenerTasaCambio(monedaActual, nuevaMoneda)
                //si hay un error, mantenemos el valor original sin convertir
            } catch (e: Exception) { 1.0 }

            //actualizo los valores en la nueva moneda
            _uiState.update { currentState ->
                currentState.copy(
                    ingresosDiarios = (currentState.ingresosDiarios * tasaCambio).toLong(),
                    gastosDiarios = (currentState.gastosDiarios * tasaCambio).toLong(),
                    ingresosMensuales = (currentState.ingresosMensuales * tasaCambio).toLong(),
                    gastosMensuales = (currentState.gastosMensuales * tasaCambio).toLong(),
                    ahorrosDiarios = (currentState.ahorrosDiarios * tasaCambio).toLong(),
                    ahorrosMensuales = (currentState.ahorrosMensuales * tasaCambio).toLong(),
                    monedaActual = nuevaMoneda
                )
            }
        }
    }
}
