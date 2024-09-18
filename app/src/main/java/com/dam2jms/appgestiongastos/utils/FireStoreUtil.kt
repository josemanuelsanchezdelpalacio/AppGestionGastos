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
    fun añadirTransaccion(collection: String, transaccion: Transaccion, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection(collection)
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
     * Elimina una transacción específica de Firestore.
     * @param collection Colección de la transacción ("ingresos" o "gastos")
     * @param transaccionId ID de la transacción a eliminar
     * @param onSuccess Función a ejecutar en caso de éxito
     * @param onFailure Función a ejecutar en caso de error
     */
    fun eliminarTransaccion(collection: String, transaccionId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection(collection)
            .document(transaccionId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Actualiza una transacción existente en Firestore.
     *
     * @param collection Colección de la transacción ("ingresos" o "gastos")
     * @param transaccion La transacción actualizada
     * @param onSuccess Función a ejecutar en caso de éxito
     * @param onFailure Función a ejecutar en caso de error
     */
    fun actualizarTransaccion(collection: String, transaccion: Transaccion, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection(collection)
            .document(transaccion.id)
            .set(transaccion)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getUserId(): String {
        return Firebase.auth.currentUser?.uid ?: throw IllegalStateException("No user logged in")
    }


}


