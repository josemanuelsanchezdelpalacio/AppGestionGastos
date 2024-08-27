package com.dam2jms.appgestiongastos.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.dam2jms.appgestiongastos.states.Transaccion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object FireStoreUtil {
    private val db = FirebaseFirestore.getInstance()

    /**
     * Obtiene todas las transacciones de Firestore para el usuario actual.
     *
     * @param onSuccess Función a ejecutar en caso de éxito con la lista de transacciones.
     * @param onFailure Función a ejecutar en caso de error.
     */
    fun obtenerTransacciones(onSuccess: (List<Transaccion>) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("transacciones")  // Cambia esto si usas diferentes colecciones para ingresos y gastos
            .get()
            .addOnSuccessListener { snapshot ->
                val transacciones = snapshot.documents.mapNotNull { it.toObject(Transaccion::class.java) }
                onSuccess(transacciones)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    /**
     * Agrega una nueva transacción a Firestore.
     *
     * @param transaccion La transacción a agregar.
     * @param onSuccess Función a ejecutar en caso de éxito.
     * @param onFailure Función a ejecutar en caso de error.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun agregarTransaccion(transaccion: Transaccion, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val coleccion = if (transaccion.tipo == "ingreso") "ingresos" else "gastos"
        val idTransaccion = db.collection("users")
            .document(userId)
            .collection(coleccion)
            .document().id

        val nuevaTransaccion = transaccion.copy(
            id = idTransaccion,
            fecha = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        )

        db.collection("users")
            .document(userId)
            .collection(coleccion)
            .document(idTransaccion)
            .set(nuevaTransaccion)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}


