package com.dam2jms.appgestiongastos.components

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
object DatePickerComponents {

    /** metodo para crear un selector de fecha
     * @param context el contexto necesario para el DatePicker
     * @param fechaActual fecha actual que se usara como fecha inicial
     * @param onFechaSeleccionada lambda que se ejecuta cuando el usuario selecciona una nueva fecha
     * **/
    fun showDatePicker(context: Context, fechaActual: LocalDate, onFechaSeleccionada: (LocalDate) -> Unit) {

        //obtengo el año, mes y dia actuales
        val añoActual = fechaActual.year
        val mesActual = fechaActual.monthValue - 1
        val diaActual = fechaActual.dayOfMonth

        //creo y muestro el selector de fecha
        DatePickerDialog(
            context, { _, añoSeleccionado, mesSeleccionado, diaSeleccionado ->
                //cuando se selecciona una fecha se crea un objeto LocalDate
                val nuevaFecha = LocalDate.of(añoSeleccionado, mesSeleccionado + 1, diaSeleccionado)

                //ejecuto la funcion lambda con la nueva fecha
                onFechaSeleccionada(nuevaFecha)
            }, añoActual, mesActual, diaActual
        ).show()
    }
}
