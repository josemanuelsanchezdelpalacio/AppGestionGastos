package com.dam2jms.appgestiongastos.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dam2jms.appgestiongastos.states.Transaccion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        val userId = Firebase.auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("ingresos")
            .get()
            .addOnSuccessListener { ingresosSnapshot ->
                val ingresos = ingresosSnapshot.documents.mapNotNull { document ->
                    val transaccion = document.toObject(Transaccion::class.java)
                    transaccion?.apply { id = document.id }
                }
                db.collection("users")
                    .document(userId)
                    .collection("gastos")
                    .get()
                    .addOnSuccessListener { gastosSnapshot ->
                        val gastos = gastosSnapshot.documents.mapNotNull { document ->
                            val transaccion = document.toObject(Transaccion::class.java)
                            transaccion?.apply { id = document.id }
                        }
                        onSuccess(ingresos + gastos)
                    }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Agrega una nueva transacción a Firestore.
     *
     * @param transaccion La transacción a agregar.
     * @param onSuccess Función a ejecutar en caso de éxito.
     * @param onFailure Función a ejecutar en caso de error.
     */
    fun añadirTransaccion(coleccion: String, transaccion: Transaccion, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection(coleccion)
            .add(transaccion)
            .addOnSuccessListener { documentReference ->
                // Asignar el ID del documento al objeto Transaccion
                documentReference.update("id", documentReference.id)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * metodo para eliminar una transaccion especifica de Firestore.
     * @param collection Colección de la transacción ("ingresos" o "gastos")
     * @param transaccionId ID de la transacción a eliminar
     * @param onSuccess Función a ejecutar en caso de éxito
     * @param onFailure Función a ejecutar en caso de error
     */
    fun eliminarTransaccion(coleccion: String, transaccionId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection(coleccion)
            .document(transaccionId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun editarTransaccion(coleccion: String, transaccion: Transaccion, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return

        // Asegúrate de que la transacción tiene un ID antes de intentar actualizar
        if (transaccion.id.isEmpty()) {
            onFailure(Exception("El ID de la transacción no puede estar vacío."))
            return
        }

        // Actualizamos la transacción en la colección especificada
        db.collection("users")
            .document(userId)
            .collection(coleccion)
            .document(transaccion.id)  // Usamos el ID de la transacción para editarla
            .set(transaccion)  // Puedes usar update si prefieres actualizar solo campos específicos
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}


