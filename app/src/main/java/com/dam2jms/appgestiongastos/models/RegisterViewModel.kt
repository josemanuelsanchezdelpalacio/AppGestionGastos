package com.dam2jms.appgestiongastos.models

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.utils.Validaciones
import com.dam2jms.appgestiongastos.utils.Validaciones.validaContraseña
import com.dam2jms.appgestiongastos.utils.Validaciones.validarCorreo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**Permite crear un nuevo usuario en Firebase */
class RegisterViewModel : AuthViewModel() {


    /** Permite crear un nuevo usuario en Firebase y crear un documento en Firestore */
    fun registrarUsuario(email: String, password: String, context: Context){

        if(!validarCorreo(context, email)){
            Toast.makeText(context, "Correo electronico no valido", Toast.LENGTH_SHORT).show()
            return
        }

        if(!validaContraseña(context, password)){
            Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        //crear usuario en Firestore
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    //obtengo el ID del usuario autenticado
                    val userId = auth.currentUser?.uid?: return@addOnCompleteListener

                    //creo el documento en firestore
                    val db = Firebase.firestore
                    val userMap = hashMapOf(
                        "email" to email,
                        "userId" to userId,
                        "createdAt" to System.currentTimeMillis() //fecha de creacion
                    )

                    db.collection("users")
                        .document()
                        .set(userMap) //creo el documento con los datos del usuario
                        .addOnSuccessListener {
                            Toast.makeText(context, "Usuario registrado y guardado en FireStore", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error al guardar el usuario en FireStore", Toast.LENGTH_SHORT).show()
                        }
                }else{
                    val errorMessage = task.exception?.localizedMessage ?: "Error al crear el usuario"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }
}
